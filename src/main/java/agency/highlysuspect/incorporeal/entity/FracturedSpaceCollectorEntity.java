package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.IncTags;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.tile.TileOpenCrate;
import vazkii.botania.mixin.AccessorItemEntity;

import java.util.List;
import java.util.UUID;

public class FracturedSpaceCollectorEntity extends Entity {
	public FracturedSpaceCollectorEntity(EntityType<?> type, Level world) {
		super(type, world);
		
		//I guess this is a good place to do it?
		setInvulnerable(true);
		setNoGravity(true);
	}
	
	public FracturedSpaceCollectorEntity(Level world, BlockPos cratePos, Player player) {
		this(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, world);
		
		this.cratePos = cratePos;
		this.ownerUuid = player.getUUID();
	}
	
	private int age = 0;
	private BlockPos cratePos = BlockPos.ZERO;
	private UUID ownerUuid;
	
	private static final double RADIUS = 2;
	private static final int MAX_AGE = 30;
	private static final int AGE_SPECIAL_START = Mth.floor((MAX_AGE * 3f / 4f));
	private static final int MANA_COST_PER_ITEM = 500;
	private static final ItemStack TOOL_STACK = new ItemStack(IncItems.FRACTURED_SPACE_ROD);
	
	@Override
	public void tick() {
		setDeltaMovement(0, 0, 0);
		super.tick();
		
		age++;
		
		if(level.isClientSide && age < MAX_AGE) {
			doSparkles();
		} else if(age > AGE_SPECIAL_START) {
			AABB aabb = getBoundingBox().inflate(RADIUS, 1, RADIUS);
			List<ItemEntity> nearbyItemEnts = level.getEntitiesOfClass(ItemEntity.class, aabb, ent -> ent != null && Math.hypot(ent.getX() - getX(), ent.getZ() - getZ()) <= RADIUS);
			
			for(ItemEntity ent : nearbyItemEnts) {
				double xDifference = getX() - ent.getX();
				double zDifference = getZ() - ent.getZ();
				
				ent.setDeltaMovement(ent.getDeltaMovement().add(xDifference * .3, 0, zDifference * .3));
				ent.hurtMarked = true;
			}
			
			if(age >= MAX_AGE) {
				//time to transport the items
				//first find out who to deduct the mana from
				if(ownerUuid == null) {
					remove(); return;
				}
				
				Player player = level.getPlayerByUUID(ownerUuid);
				
				if(player == null) {
					remove(); return;
				}
				
				//Chunkload the immediate area for a little bit
				if(level instanceof ServerLevel) {
					ChunkPos ticketPos = new ChunkPos(cratePos);
					((ServerLevel) level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, ticketPos, 3, player.getId());
				}
				
				BlockState state = level.getBlockState(cratePos);
				BlockEntity tile = level.getBlockEntity(cratePos);
				
				if(state.is(IncTags.Blocks.OPEN_CRATES) && tile instanceof TileOpenCrate && ((TileOpenCrate) tile).canEject()) {
					boolean redstone = isCratePowered(level, cratePos);
					
					//delete all items and emit them from the crate
					for(ItemEntity ent : nearbyItemEnts) {
						if(!ent.isAlive()) continue;
						
						ItemStack stack = ent.getItem();
						int count = stack.getCount();
						int cost = count * MANA_COST_PER_ITEM;
						//do they have enough mana for this item stack?
						if(ManaItemHandler.instance().requestManaExact(TOOL_STACK, player, cost, false)) {
							//ok,now do it for real
							ManaItemHandler.instance().requestManaExact(TOOL_STACK, player, cost, true);
							fakeCrateEject(level, cratePos, redstone, stack);
							ent.remove();
						}
					}
				}
				
				//my work here is done.
				remove();
			}
		}
	}
	
	private static boolean isCratePowered(Level world, BlockPos pos) {
		//Uses the exact same logic open crates do to check if they're powered!
		for(Direction dir : Direction.values()) {
			if(world.getSignal(pos.relative(dir), dir) != 0) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void fakeCrateEject(Level world, BlockPos pos, boolean redstone, ItemStack stack) {
		//mostly a copy of the open crate ejection logic, but doesn't touch the buffered item in the crate
		double ejectY = pos.getY() - EntityType.ITEM.getHeight();
		ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, ejectY, pos.getZ() + 0.5, stack);
		item.setDeltaMovement(Vec3.ZERO);
		
		if(redstone) //noinspection ConstantConditions
			((AccessorItemEntity) item).setAge(-200);
		
		world.addFreshEntity(item);
	}
	
	private static final int PARTICLE_COUNT = 12;
	private void doSparkles() {
		//ALSO YEAH i know this looks WAY different than it did in 1.12
		//I kinda prefer the new look though
		
		double ageFraction = age / (double) MAX_AGE;
		//double radiusMult = 4 * (ageFraction - ageFraction * ageFraction); //simple and cute easing function
		double radiusMult = 1.6 * (ageFraction - Math.pow(ageFraction, 7)); //less simple but cuter easing function
		double particleAngle = age / 25d;
		double height = radiusMult / 2;
		
		for(int i = 0; i < PARTICLE_COUNT; i++, particleAngle += (2 * Math.PI) / PARTICLE_COUNT) {
			double x = Math.cos(particleAngle) * RADIUS * radiusMult;
			double z = Math.sin(particleAngle) * RADIUS * radiusMult;
			
			float size = (float) (1 + ageFraction * 5 * Math.random());
			
			SparkleParticleData uwu = SparkleParticleData.sparkle(size, 0.1f, 0.85f, 0.65f, 5);
			level.addParticle(uwu, getX() + x, getY() + height, getZ() + z, 0, 0, 0);
		}
		
		double x = Math.cos(Math.random() * Math.PI * 2) * RADIUS * radiusMult;
		double z = Math.cos(Math.random() * Math.PI * 2) * RADIUS * radiusMult;
		
		WispParticleData awa = WispParticleData.wisp(.5f, .1f, 85f, .65f, 0.5f);
		level.addParticle(awa, getX() + x, getY() + height, getZ() + z, 0f, 0f, 0f);
		
		if(age >= MAX_AGE - 2) {
			level.addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 0, 0, 0);
			SparkleParticleData uwu = SparkleParticleData.sparkle(2f, 0.9f, 0.45f, 0.05f, 2);
			for(int i = 0; i < 5; i++) {
				level.addParticle(uwu, getX(), getY(), getZ(), 0, 0, 0);
			}
		}
	}
	
	@Override
	protected void defineSynchedData() {
		//Nothing
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		age = compound.getInt("Age");
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Age", age);
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
