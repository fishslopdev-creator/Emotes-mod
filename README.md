# FreeMotes

A Fabric mod for **Minecraft 1.21.11** that recreates the emote and cosmetic side of Essential.
There's an emote wheel and a shop where every item is free.

## Controls
| Key | Action |
|-----|--------|
| **B** (hold) | Emote wheel: point at an emote and release to play it. Tap B to keep the wheel open and click instead. Right-click stops the current emote. |
| **N** | Shop: claim emotes and cosmetics for 0 coins, set up the wheel and wear cosmetics. |
| Move / jump / sneak / attack | Cancels your emote |

You can rebind both keys under *Options → Controls → Miscellaneous*.

## Chonky animation
Emotes run on a millisecond clock, so the timeline has 1 ms resolution. Frames are never blended: each
keyframe snaps in instantly and holds until the next one. Spins jump in 15°/45° steps and floating moves
in visible hops, which gives emotes the blocky look of vanilla Minecraft animation. Props and cosmetics
(propellers, wings, halos) animate in the same stepped way.

## Emotes (19)
* **Common:** Wave, Clap, Facepalm, Nod, Nope
* **Rare:** Dab, Floss, Flex, Heart Hands (giant pixel heart), Rock Out (guitar), Sword Salute (diamond sword)
* **Epic:** Boombox Groove (pulsing boombox), Windmill (breakdance spin), Backflip, Rain Cloud (personal storm), Zen Mode (levitating with orbiting orbs)
* **Legendary:** Champion (golden trophy), Pyromancer (fire juggling and a ring of fire), Fireworks Show
* **SUPREME: Ascension** (~10 s): the player charges up and detonates, then rises into a pillar of light.
  While floating they get a crown, three spinning halo rings, eight orbiting crystals, energy wings, a ground
  rune circle and flickering aura flames. Then a sonic shockwave goes off, a totem and firework finale plays,
  and the player slams down for a superhero landing.

## Cosmetics (16)
* **Hats:** Top Hat, Party Hat, Propeller Cap (spins), Wizard Hat, Viking Helmet, Royal Crown, Halo, Cat Ears, Devil Horns, Chef Hat
* **Face:** Deal-With-It Shades, Fancy Mustache, Cyber Visor (scanning light)
* **Back:** Angel Wings, Dragon Wings, Adventurer Pack, Jetpack (flickering flames)

## Multiplayer
Install the mod on the server as well. Other modded players will then see your emotes, props and
cosmetics. On a vanilla server, or a server without the mod, only you see them. Singleplayer and LAN work
with no extra setup.

## Building
```
./gradlew build      # jar ends up in build/libs/
```
Requires Java 21. GitHub Actions builds the jar on every push (see the *Actions* tab → *freemotes-jar*
artifact) and attaches it to a release when you push a `v*` tag.
