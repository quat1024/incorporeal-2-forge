# Rhododendrite

I think the cygnus system in Incorporeal is really janky and bad, so here's the successor, rhododendrite.

First of all - it's a tree now. The pun here (rhododendron, a type of tree / dendrite, meaning neuron communication) is too fucking perfect lmao.. craft the sapling at the runic altar with like corporea sparks and a sapling or something, definitely No fuckiNG SKYTUCHING

It's like an average-sized tree with dark chocolately (livingwood-esque) and pink leaves, you can use it for decoration. But the tree can be "activated" via wand of the forest on one of the logs. The activated block turns into a Rhododendrite Core, a block with a transparent inner bit. Rhododendrite opcodes are blocks placed in-world and assigned to the core in the same way cygnus opcodes are assigned to the master cygnus spark; cores can be dyed 16 colors (just like corporea sparks). Cores also have a 6-way ffacing direction.

here [ [ is the rhodo core facing right, # means an empty log
```
initial:
[#[123
```
Pull retracts data inwards toward the core. If there's data in front, it goes inside the core. If the core already has data, it is discarded.
```
initial -> pull:
[1[23#
```
Add adds the number inside the core with the number its looking at, then pulls the rest inwards.
```
initial -> pull -> add:
[3[3##

initial -> pull -> add -> add:
[6[###
```
Push pushes data outwards, emptying the core. Any data that falls off the end of a log is discarded.
```
initial -> pull -> push:
[#[123

initial -> push -> push:
[#[#12

initial -> pull -> add -> push:
[#[33#
```
Interaction with leaves (represented with the ~ character) is like this
```
initial
[#[123~~45#

initial -> push
[#[#12~~345

initial -> push -> push
[#[##1~~234
```
facing opcodes
```
initial
##[#[1#

initial -> pull
##[1[##

initial -> pull -> face left
##]1]##

initial -> pull -> face left -> push
#1]#]##
```

---

Rhododendron logs have their own comparator outputs.

The cygnus funnel is copied as-is. Reading data "pushes" it from inside the core, writing data pulls it out, just like the push and pop opcodes.

A decision I have to make: 
* Cygnus retainers have no analog; any rhododendrite log can be used for this purpose.
* Cygnus retainers have no analog *period*. Only logs in-line with the rhodo core are "awakened" and can hold data (i.e. are tile entities). If you need scratch space, rotate the core. (leaning towards this)

Basically every operation can be thought of as "mutating" the thing inside the core by *using* whatever's directly outside it. This means "divide by" takes the thing inside the core and divides it by what's in front. "Set request count" takes the request inside the core, and sets its count to the number in front. stuff like that.

Not sure what to do about the "error" datatype. I kiiiinda like it, but maybe opcodes just shouldn't do anything if they don't work? Comparator on the opcode that lights up on an error condition

Crystal cubes are copied over pretty much as-is. (I want to use a "listener" system, instead of just checking for updates every tick, though.) Some way of sensing the core's rotation would be nice? Not adding "rotation: north" "rotation: east" crystal cube conditions though, more like "if its facing the same way as this thing"... doesnt really fit into the crystal cube model though. I can add a "rotation sensor" block?

"core prism" that can be linked to a core (with some generous max distance) and shows whats inside, (incl. its comparator strength). mainly for wireless display if you cover all 6 sides lol