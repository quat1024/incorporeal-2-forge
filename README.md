Incorporeal 2 + Rhododendrite
=============================

Let's try this again. Forge this time.

Two mods for the price of one: Incorporeal 2, a fairly straightforward port of Incorporeal less the "cygnus network", and Rhododendrite, that mechanic's successor. Them being two separate mods is mainly for organizational purposes - eventually I want to separate the mods into their own .jars? Rhodo might get its own standalone Fabric port. Idk. I hesitate because it won't be very useful without Botania/a corporea network to power with it, and it also depends on a bit of code from incorporeal 2 and botania

# Incorporeal 2

## Things left to port

* documentation Loooll
* natural redstone circuitry from redstone root plants
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

## wood things i have to do

trapdoor texture, door item and block texture, sign texture

- boat (huge pain in the tail)
- sign (needs a remapped mixin to add it to the tile entity)
- make it flammable (needs either a remapped mixin, or subclassing every block for a poorly-designed Forge extension)

reason im shying away from remapped mixins is they're currently broken in this workspace for some reason, i'll file a bug report later

* Unfuck the loot table for potted rhodo sapling