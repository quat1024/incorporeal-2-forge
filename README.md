Incorporeal 2 + Rhododendrite
=============================

Let's try this again. Forge this time. Jk Fabric this time.

# things i gotta do

* Fix config
* Fix PotionSoulCoreCollectorEntity::attributeEvent
* Fix CorporeaSoulCoreTile::corporeaIndexRequestEvent
* Fix TicketConjurerItem::chatEvent
* port capabilities over to cardinal capabilities
* Fix RedstoneRootCropBlock::interactEvent

* Fix corporea ticket written_ticket item property
* Fix entity renderers (register them, idk how)
* fix block entity renderers
* fix ISTER (probably copy botania)

* fix Red String Liar logic

## other things to do

Move more things to datagen, ~~i really dont like forge datagens though~~wheres my excuse now huh

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