package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Despacito;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import javax.annotation.Nullable;

public class FunnySubTile extends TileEntityFunctionalFlower {
	protected FunnySubTile(int range, int ticksBetweenNotes, int pitchShift, double sparkleHeight, TileEntityType<?> type) {
		super(type);
		this.range = range;
		this.ticksBetweenNotes = ticksBetweenNotes;
		this.pitchShift = pitchShift;
		this.sparkleHeight = sparkleHeight;
	}
	
	public static FunnySubTile big() {
		return new FunnySubTile(4, 4, 0, 0.75, IncTileTypes.FUNNY_BIG);
	}
	
	public static FunnySubTile small() {
		return new FunnySubTile(2, 3, 7, 0.6, IncTileTypes.FUNNY_SMALL);
	}
	
	private final int range;
	private final int ticksBetweenNotes;
	private final int pitchShift;
	private final double sparkleHeight;
	
	private final int NOTE_MANA_COST = 10;
	
	private int clock = -1;
	
	@SuppressWarnings({"StatementWithEmptyBody", "UnnecessaryContinue"}) //but i like it
	@Override
	public void tickFlower() {
		super.tickFlower();
		assert world != null;
		
		if(world.isRemote) return;
		
		if(redstoneSignal == 15) clock = -1; //reset
		else if(redstoneSignal > 0 || getMana() < NOTE_MANA_COST) {
			//pause
		}
		else {
			clock++;
			markDirty();
			
			int ticksBetween = (ticksBetweenNotes / (overgrowth || overgrowthBoost ? 2 : 1));
			if(ticksBetween == 0) ticksBetween = 1;
			
			int tick = clock;
			if(tick < 0 || tick % ticksBetween != 0) return;
			tick /= ticksBetween;
			
			BlockPos flutePos = null;
			BlockPos snarePos = null;
			BlockPos basedrumPos = null;
			BlockPos bassPos = null;
			BlockPos pos = getEffectivePos();
			
			for(BlockPos bp : BlockPos.getAllInBoxMutable(pos.add(-range, 0, -range), pos.add(range, 1, range))) {
				BlockState state = world.getBlockState(bp);
				if(state.getBlock() == Blocks.NOTE_BLOCK) {
					NoteBlockInstrument inst = state.get(BlockStateProperties.NOTE_BLOCK_INSTRUMENT);
					
					if(inst == NoteBlockInstrument.FLUTE && flutePos == null) {
						flutePos = bp.toImmutable();
						continue;
					}
					
					if(inst == NoteBlockInstrument.SNARE && snarePos == null) {
						snarePos = bp.toImmutable();
						continue;
					}
					
					if(inst == NoteBlockInstrument.BASEDRUM && basedrumPos == null) {
						basedrumPos = bp.toImmutable();
						continue;
					}
					
					if(inst == NoteBlockInstrument.BASS && bassPos == null) {
						bassPos = bp.toImmutable();
						continue;
					}
				}
			}
			
			//play music
			Vector3d particleSrc = world.getBlockState(pos).getOffset(world, pos).add(pos.getX() + .5, pos.getY() + sparkleHeight, pos.getZ() + .5);
			
			boolean dirtyMana = doIt(world, pos, tick, particleSrc, flutePos, NoteBlockInstrument.FLUTE);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(world, pos, tick, particleSrc, snarePos, NoteBlockInstrument.SNARE);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(world, pos, tick, particleSrc, basedrumPos, NoteBlockInstrument.BASEDRUM);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(world, pos, tick, particleSrc, bassPos, NoteBlockInstrument.BASS);
			if(dirtyMana) sync();
		}
	}
	
	private boolean doIt(World world, BlockPos pos, int tick, Vector3d particleSrc, BlockPos noteblockPos, NoteBlockInstrument inst) {
		if(noteblockPos == null) return false;
		
		int[] notes = Despacito.notesForTick(tick, inst);
		if(notes != null) {
			//IncorporeticNetCommon.sendFunnyFlower((ServerWorld) world, particleSrc, new Vec3d(noteblockPos.getX() + .5, noteblockPos.getY() + .5, noteblockPos.getZ() + .5), 2, notes);
			for(int note : notes) {
				if(getMana() > NOTE_MANA_COST) {
					addMana(-NOTE_MANA_COST);
					float convertedPitch = (float) Math.pow(2, (note - 12 + pitchShift) / 12d);
					world.playSound(null, pos, inst.getSound(), SoundCategory.RECORDS, 3f, convertedPitch);
				}
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean acceptsRedstone() {
		return true;
	}
	
	@Override
	public int getMaxMana() {
		return 2000;
	}
	
	@Override
	public int getColor() {
		return 0xbb4422;
	}
	
	@Nullable
	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getEffectivePos(), range);
	}
	
	@Override
	public void writeToPacketNBT(CompoundNBT cmp) {
		super.writeToPacketNBT(cmp);
		cmp.putInt("Clock", clock);
	}
	
	@Override
	public void readFromPacketNBT(CompoundNBT cmp) {
		super.readFromPacketNBT(cmp);
		clock = cmp.getInt("Clock");
	}
}
