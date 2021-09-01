static class Blah {
	@SuppressWarnings("StatementWithEmptyBody")
	public void recheckPaths(World world, BlockPos pos) {
		for(Direction dir : Direction.values()) {
			boolean activePath = true;
			for(int i = 1; i < CorePathTracing.MAX_RANGE; i++) {
				BlockPos posThere = pos.offset(dir, i);
				BlockState stateThere = world.getBlockState(posThere);
				
				boolean isLeaves = stateThere.getBlock() == RhoBlocks.RHODODENDRITE.leaves;
				boolean isAwakened = stateThere.getBlock() == RhoBlocks.AWAKENED_LOG;
				boolean isAwakenedCorrectly = isAwakened && stateThere.get(AwakenedLogBlock.DISTANCE) == i && stateThere.get(AwakenedLogBlock.FACING) == dir.getOpposite();
				boolean isRegularLog = stateThere.getBlock() == RhoBlocks.RHODODENDRITE.log;
				
				if(activePath) {
					if(isLeaves || isAwakenedCorrectly) {
						//Already correct; pass.
					} else if(isRegularLog) {
						//The log can be activated.
						world.setBlockState(posThere, RhoBlocks.AWAKENED_LOG.getDefaultState()
							.with(AwakenedLogBlock.FACING, dir.getOpposite())
							.with(AwakenedLogBlock.DISTANCE, i));
					} else {
						//That's the end of this path.
						activePath = false;
					}
				} else {
					if(isAwakenedCorrectly) {
						//The path is obstructed, but this awakened log is still pointing towards me. It should be deactivated.
						world.setBlockState(posThere, ((AwakenedLogBlock) stateThere.getBlock()).unawakenedState(stateThere));
					}
				}
			}
		}
	}
}