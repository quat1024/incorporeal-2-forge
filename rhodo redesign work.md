# Before releasing

- `funnel2` is intentionally named to trigger FML missing mappings warning, work out what to do with that.

- actually remove test1 and test2 this time lmao

### solidified request.cap

* The purpose of SolidifiedRequest.Cap ("solidified request holder").
  * Used for the Retainer Evaporator
  * Used for `CorePathTracing#readStackOps` but CorePathTracing is gone.
  * Totally possible to make the evaporator-thingie work using funnelables, I think.

Should I add data travel time (womp womp) like, it'll look nice, though???? Idk if making data travel time "purely a visual thing" would be hard.

### binding stuff

Basically the 3 blocks (cell, op/condition, funnel) bind in ways that are frustartingly *similar* but not *the same* lmao

* Cells bind to other cells
* Op binds to cells, but if it sees another op (and maybe funnel) block, it can bind through it
* Funnels bind to anything funnelable, but forwards *and* backwards

First attempt threw a `BlockPos binding` in AbstractComputerTile but that doesn't cleanly fit the funnel's reverse-bind.

Pushing the logic down into each subclass causes a lot of code duplication. I compromised by putting the main loop as a method that can be configured via callback functions...?

## funnel

The red-stringiness of the funnel here is predicated on it only binding to blocks. Else it doesn't feel red-stringy. Right? Maybe not.

* Is that true? Maybe it can bind to entities just fine; instead of binding to a blockpos, I store a supplier of the funnel-capability itself, as well as a vec3d for the real position
  * This is harder than it sounds. What goes in that supplier<rhodofunnelable> when its, say, a tile entity.
* An alternative is making some kind of "interface" block that the funnels can bind to, and it does the more fancy funnelable checks.. Dunno actually
  * Realy not as nice though.

## missing

[13:43:41] [Render thread/WARN] [ne.mi.re.GameData/REGISTRIES]: Missing minecraft:item:
rhododendrite:awakened_log
rhododendrite:condition
rhododendrite:core
rhododendrite:funnel
rhododendrite:opcode

Missing minecraft:block:
rhododendrite:awakened_log
rhododendrite:condition
rhododendrite:core
rhododendrite:funnel
rhododendrite:opcode