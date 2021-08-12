package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.tile.TileOpenCrate;
import vazkii.botania.mixin.AccessorItemEntity;

import java.util.List;
import java.util.UUID;

public class FracturedSpaceCollectorEntity extends Entity {
	public FracturedSpaceCollectorEntity(EntityType<?> type, World world) {
		super(type, world);
		
		//I guess this is a good place to do it?
		setInvulnerable(true);
		setNoGravity(true);
	}
	
	public FracturedSpaceCollectorEntity(World world, BlockPos cratePos, PlayerEntity player) {
		this(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, world);
		
		this.cratePos = cratePos;
		this.ownerUuid = player.getUniqueID();
	}
	
	private int age = 0;
	private BlockPos cratePos = BlockPos.ZERO;
	private UUID ownerUuid;
	
	private static final double RADIUS = 2;
	private static final int MAX_AGE = 30;
	private static final int AGE_SPECIAL_START = MathHelper.floor((MAX_AGE * 3f / 4f));
	private static final int MANA_COST_PER_ITEM = 500;
	private static final ItemStack TOOL_STACK = new ItemStack(IncItems.FRACTURED_SPACE_ROD);
	
	@Override
	public void tick() {
		setMotion(0, 0, 0);
		super.tick();
		
		age++;
		
		if(world.isRemote && age < MAX_AGE) {
			doSparkles();
		} else if(age > AGE_SPECIAL_START) {
			AxisAlignedBB aabb = getBoundingBox().grow(RADIUS, 1, RADIUS);
			List<ItemEntity> nearbyItemEnts = world.getEntitiesWithinAABB(ItemEntity.class, aabb, ent -> ent != null && Math.hypot(ent.getPosX() - getPosX(), ent.getPosZ() - getPosZ()) <= RADIUS);
			
			for(ItemEntity ent : nearbyItemEnts) {
				double xDifference = getPosX() - ent.getPosX();
				double zDifference = getPosZ() - ent.getPosZ();
				
				ent.setMotion(ent.getMotion().add(xDifference * .3, 0, zDifference * .3));
				ent.velocityChanged = true;
			}
			
			if(age >= MAX_AGE) {
				//time to transport the items
				//first find out who to deduct the mana from
				if(ownerUuid == null) {
					remove(); return;
				}
				
				PlayerEntity player = world.getPlayerByUuid(ownerUuid);
				
				if(player == null) {
					remove(); return;
				}
				
				//Chunkload the immediate area for a little bit
				if(world instanceof ServerWorld) {
					ChunkPos ticketPos = new ChunkPos(cratePos);
					((ServerWorld) world).getChunkProvider().registerTicket(TicketType.POST_TELEPORT, ticketPos, 3, player.getEntityId());
				}
				
				BlockState state = world.getBlockState(cratePos);
				TileEntity tile = world.getTileEntity(cratePos);
				
				//TODO: block tag check (theres still the open crate/crafty crate issue) (issue #5)
				if(tile instanceof TileOpenCrate && ((TileOpenCrate) tile).canEject()) {
					boolean redstone = isCratePowered(world, cratePos);
					
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
							fakeCrateEject(world, cratePos, redstone, stack);
							ent.remove();
						}
					}
				}
				
				//my work here is done.
				remove();
			}
		}
	}
	
	private static boolean isCratePowered(World world, BlockPos pos) {
		//Uses the exact same logic open crates do to check if they're powered!
		for(Direction dir : Direction.values()) {
			if(world.getRedstonePower(pos.offset(dir), dir) != 0) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void fakeCrateEject(World world, BlockPos pos, boolean redstone, ItemStack stack) {
		//mostly a copy of the open crate ejection logic, but doesn't touch the buffered item in the crate
		double ejectY = pos.getY() - EntityType.ITEM.getHeight();
		ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, ejectY, pos.getZ() + 0.5, stack);
		item.setMotion(Vector3d.ZERO);
		
		if(redstone) //noinspection ConstantConditions
			((AccessorItemEntity) item).setAge(-200);
		
		world.addEntity(item);
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
			world.addParticle(uwu, getPosX() + x, getPosY() + height, getPosZ() + z, 0, 0, 0);
		}
		
		double x = Math.cos(Math.random() * Math.PI * 2) * RADIUS * radiusMult;
		double z = Math.cos(Math.random() * Math.PI * 2) * RADIUS * radiusMult;
		
		WispParticleData awa = WispParticleData.wisp(.5f, .1f, 85f, .65f, 0.5f);
		world.addParticle(awa, getPosX() + x, getPosY() + height, getPosZ() + z, 0f, 0f, 0f);
		
		if(age >= MAX_AGE - 2) {
			world.addParticle(ParticleTypes.LARGE_SMOKE, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
			SparkleParticleData uwu = SparkleParticleData.sparkle(2f, 0.9f, 0.45f, 0.05f, 2);
			for(int i = 0; i < 5; i++) {
				world.addParticle(uwu, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
			}
		}
	}
	
	@Override
	protected void registerData() {
		//Nothing
	}
	
	@Override
	protected void readAdditional(CompoundNBT compound) {
		age = compound.getInt("Age");
	}
	
	@Override
	protected void writeAdditional(CompoundNBT compound) {
		compound.putInt("Age", age);
	}
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
