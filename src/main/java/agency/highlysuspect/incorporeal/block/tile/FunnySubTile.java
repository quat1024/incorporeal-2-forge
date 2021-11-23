package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Despacito;
import agency.highlysuspect.incorporeal.IncNetwork;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FunnySubTile extends TileEntityFunctionalFlower {
	protected FunnySubTile(int range, int ticksBetweenNotes, int pitchShift, double sparkleHeight, BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.range = range;
		this.ticksBetweenNotes = ticksBetweenNotes;
		this.pitchShift = pitchShift;
		this.sparkleHeight = sparkleHeight;
	}
	
	public static FunnySubTile big() {
		return new FunnySubTile(4, 4, 0, 0.75, IncBlockEntityTypes.FUNNY_BIG);
	}
	
	public static FunnySubTile small() {
		return new FunnySubTile(2, 3, 7, 0.6, IncBlockEntityTypes.FUNNY_SMALL);
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
		assert level != null;
		
		if(level.isClientSide) return;
		
		if(redstoneSignal == 15) clock = -1; //reset
		else if(redstoneSignal > 0 || getMana() < NOTE_MANA_COST) {
			//pause
		}
		else {
			clock++;
			setChanged();
			
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
			
			for(BlockPos bp : BlockPos.betweenClosed(pos.offset(-range, 0, -range), pos.offset(range, 1, range))) {
				BlockState state = level.getBlockState(bp);
				if(state.getBlock() == Blocks.NOTE_BLOCK) {
					NoteBlockInstrument inst = state.getValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT);
					
					if(inst == NoteBlockInstrument.FLUTE && flutePos == null) {
						flutePos = bp.immutable();
						continue;
					}
					
					if(inst == NoteBlockInstrument.SNARE && snarePos == null) {
						snarePos = bp.immutable();
						continue;
					}
					
					if(inst == NoteBlockInstrument.BASEDRUM && basedrumPos == null) {
						basedrumPos = bp.immutable();
						continue;
					}
					
					if(inst == NoteBlockInstrument.BASS && bassPos == null) {
						bassPos = bp.immutable();
						continue;
					}
				}
			}
			
			//play music
			Vec3 particleSrc = level.getBlockState(pos).getOffset(level, pos).add(pos.getX() + .5, pos.getY() + sparkleHeight, pos.getZ() + .5);
			
			List<Pair<IncNetwork.SparkleLine, byte[]>> sparkleData = new ArrayList<>();
			
			boolean dirtyMana = doIt(level, pos, tick, particleSrc, flutePos, NoteBlockInstrument.FLUTE, sparkleData);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(level, pos, tick, particleSrc, snarePos, NoteBlockInstrument.SNARE, sparkleData);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(level, pos, tick, particleSrc, basedrumPos, NoteBlockInstrument.BASEDRUM, sparkleData);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(level, pos, tick, particleSrc, bassPos, NoteBlockInstrument.BASS, sparkleData);
			if(dirtyMana) sync();
			
			if(!sparkleData.isEmpty()) IncNetwork.sendToNearby(level, pos, new IncNetwork.FunnyFlower(sparkleData));
		}
	}
	
	private boolean doIt(Level world, BlockPos pos, int tick, Vec3 particleSrc, BlockPos noteblockPos, NoteBlockInstrument inst, List<Pair<IncNetwork.SparkleLine, byte[]>> sparkleLines) {
		if(noteblockPos == null) return false;
		
		byte[] notes = Despacito.notesForTick(tick, inst);
		if(notes != null) {
			sparkleLines.add(Pair.of(new IncNetwork.SparkleLine(particleSrc, Vec3.atCenterOf(noteblockPos), 2, 1f), notes));
			for(int note : notes) {
				if(getMana() > NOTE_MANA_COST) {
					addMana(-NOTE_MANA_COST);
					float convertedPitch = (float) Math.pow(2, (note - 12 + pitchShift) / 12d);
					world.playSound(null, pos, inst.getSoundEvent(), SoundSource.RECORDS, 3f, convertedPitch);
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
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);
		cmp.putInt("Clock", clock);
	}
	
	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);
		clock = cmp.getInt("Clock");
	}
}
