Incorporeal 2 + Rhododendrite
=============================

Let's try this again. ~~Forge this time.~~April fools

# things i have to finsh in fabric port

* Fix all the compile errors Lol
* Fix fabric.mod.json

# Incorporeal 2

## Things left to port

* frame screw

## Things that have been ported

* corporea solidifier
* corporea ticket
* sanvocalia flower
* red string liar
* funny despacito flower
* item frame tinkerer (there was some interest in botania taking this on)
* ender soul core
* corporea soul core (security system)
* ticket conjurer
* rod of the fractured space
* corporea retainer evaporator (possssibly obsolete???)
* unstable cube
* potion soul core
* recipes Lol
* documentation Loooll
* natural redstone circuitry
* that thing where you can plant redstone roots

## Things that will not be ported

* cygnus network
  * replaced with Rhododendrite mod
* corporea decorative blocks
  * subsumed
* corporea spark tinkerer
  * obsoleted by paintslinger lens
* that block that obstructs corporea sparks from connecting
  * it's fairly computationally expensive, and doesn't even work too well; just use paintslinger now
* lokiw block
  * wait it wasn't funny? Never has been
* forgotten shrine
  * weird fluff, only existed because the tile was left unused in botania and i found it interesting. it was deleted last year

## other things to do

Move more things to datagen, i really dont like forge datagens though

# Rhododendrite

## things i need to make

* Gotta write the Patchouli category

## wood things i have to do

trapdoor texture, door item and block texture, sign texture

- boat (huge pain in the tail)
- sign (needs a remapped mixin to add it to the tile entity)
- make it flammable (needs either a remapped mixin, or subclassing every block for a poorly-designed Forge extension)

reason im shying away from remapped mixins is they're currently broken in this workspace for some reason, i'll file a bug report later

* Unfuck the loot table for potted rhodo sapling