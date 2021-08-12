Incorporeal 2 + Rhododendrite
=============================

Let's try this again. Forge this time. This is my first Forge 1.16 mod, it's been so long since I used Forge, go easy on me :)

This .jar contains two mods for the price of one: Incorporeal 2, a fairly straightforward port of Incorporeal less the "cygnus network", and Rhododendrite, that mechanic's successor. Them being two separate mods is mainly for organizational purposes - eventually I want to separate the mods into their own .jars.

I hesitate because Rhododendrite will not be very useful without Botania/a corporea network to power with it, and it also depends on a small amount of code from both Botania and Incorporeal 2.

# list of the old incorporeal features, for myself, cuz i forgot what the mod even added

## Things to port, in rough order of importance

* recipes Lol
* documentation Loooll
* potion soul core
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

Rhodo: remove CorePredicate/CoreAction and make them just Predicate<CoreTile>. They took the world/pos/state parameters back when opcodes were individual blocks and the Core Tumbler was just a rotatable opcode. ORRRRR make then biconsumer<coretile, direction> and keep track of which face the item frame is on (Lol)

Rhodo: If an animated torch is on top of the Operator use its face to determine which item frame to read. Could be interesting

# rhododendrite wood things i have to do

trapdoor texture, door item and block texture, sign texture

- boat (huge pain in the tail)
- sign (needs a remapped mixin to add it to the tile entity)
- make it flammable (needs either a remapped mixin, or subclassing every block for a poorly-designed Forge extension)

reason im shying away from remapped mixins is they're currently broken in this workspace for some reason, i'll file a bug report later

* Unfuck the loot table for potted rhodo sapling

# Rhododendrite design decisions I'm not sure about

I'm just feeling around in the dark here; dunno if these are any fun to work around...

| Task | Incorporeal 1 | Rhododendrite | Reason |
|----- |-------------- |-------------- |------- |
| adding 5 items to a request | Copy the request somewhere, store one copy and convert the other to a number, add 5 to the number, reassemble the request | Push 5, push the request, add | Rho's math operations work by extracting numbers from the head and tail, doing the operation on the extracted numbers, then injecting the number back into the head. "extracting" from a request returns the count, and "injecting" into a request overwrites the count, so this operation works as expected |
| setting a request to exactly 5 items | Push 5, push the request, "set request size" | Push 5, push the request, duplicate, subtract, add | Since I removed "get request count" due to the above I also wanted to see if removing "set request count" operation could work, turns out it does! This might be too janky though |
| pushing 1 | Push a corporea request with 1 item and extract request count | dedicated "Push 1" opcode | Yeah i just added an opcode for this since it's so common. There's also Push 0 |

Less-than and greater-than crystal cube conditions extract numbers and compare them, but equals does not, it does a strict comparison. This is so you can detect whether two corporea requests have the same item (set their requests to the same count and check equals); if equals extracted numbers, this would always be true. I think polymorphic == can be created using "not less than & not greater than", if it's needed for some reason? A full-on type coercion scheme sounds like a bad idea.

### "Extract Number"

A hard design decision: Should raw numbers with no request assigned be a part of Rhododendrite at all?

In favor:

* "push 0" and "push 1" opcodes are handy, and don't have a reasonable default item
  * hmm, actually push 1 can be created by making the Opcode block push the request it would have done if it was a corporea funnel (as well as push 2, 4, 8, etc)
  * push 0 isn't critical, you can do push 1 push 1 subtract if you need it really bad

Against:

* Can't have type errors if you don't have types 5head
* Janky mechanics with which request's item "wins", when you add 5 stone + 5 apples
  * = 10 stone or = 10 apples: annoyingly asymmetric-feeling
  * "5 stone and 5 apples": then you can't do math anymore
  * "10 stone or apples": I actually kinda like this??? Feels like there should be a way to strip off unwanted predicates from a request though
    * could add an opcode for that

ok i might do this actually lol