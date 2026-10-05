"""Generates the FreeMotes material atlas (64x64, 4x4 tiles of 16x16).

Tiles are mostly grayscale so the per-box vertex color tints them. Run:
    python3 tools/gen_textures.py
"""
import random
from PIL import Image

OUT = "src/main/resources/assets/freemotes/textures/misc/materials.png"
T = 16
rnd = random.Random(1337)
atlas = Image.new("RGBA", (64, 64), (255, 255, 255, 255))


def tile(index, fn, contrast=1.8):
    ox, oy = (index % 4) * T, (index // 4) * T
    for y in range(T):
        for x in range(T):
            v = fn(x, y)
            if isinstance(v, (int, float)):
                v = 255 - (255 - v) * contrast  # darken the darks so patterns survive tinting
                v = (v, v, v, 255)
            elif len(v) == 3:
                v = (*v, 255)
            atlas.putpixel((ox + x, oy + y), tuple(max(0, min(255, int(c))) for c in v))


def noise(lo, hi):
    return lo + rnd.random() * (hi - lo)


# 0 PLAIN: subtle speckle
tile(0, lambda x, y: noise(225, 255), contrast=1.0)

# 1 WOOD: vertical grain with knots
grain = [noise(-25, 15) for _ in range(T)]
tile(1, lambda x, y: 200 + grain[x] + (25 if (x * 7 + y // 5) % 6 == 0 else 0)
     - (40 if (x - 10) ** 2 + (y - 5) ** 2 < 3 else 0) + noise(-8, 8))

# 2 GOLD: shiny with diagonal highlight streaks
tile(2, lambda x, y: 195 + (55 if (x + y) % 11 in (0, 1) else 0) + (30 if x + y < 6 else 0)
     - (35 if x + y > 26 else 0) + noise(-10, 10))

# 3 METAL: brushed horizontal lines + rivets in corners
def metal(x, y):
    v = 205 + (12 if y % 3 == 0 else -6) + noise(-6, 6)
    if (x, y) in ((2, 2), (13, 2), (2, 13), (13, 13)):
        v = 255
    if (x, y) in ((3, 3), (14, 3), (3, 14), (14, 14)):
        v = 140
    if x == 0 or y == 0:
        v += 25
    if x == 15 or y == 15:
        v -= 45
    return v
tile(3, metal)

# 4 CLOTH: woven checker
tile(4, lambda x, y: 215 + (20 if (x // 2 + y // 2) % 2 == 0 else -20) + noise(-8, 8))

# 5 FEATHER: rows of overlapping feather tips
def feather(x, y):
    row = y // 4
    cx = (x + row * 2) % 4
    v = 240 - (y % 4) * 12 + noise(-6, 6)
    if cx == 0:
        v -= 35  # quill line
    if y % 4 == 3:
        v -= 30
    return v
tile(5, feather)

# 6 SCALES: dragon/membrane scales
def scales(x, y):
    sx, sy = (x + (y // 4) * 2) % 4, y % 4
    v = 215 - (sx - 2) ** 2 * 6 - sy * 10 + noise(-8, 8)
    return v - (45 if sy == 3 else 0)
tile(6, scales)

# 7 GEM: facets
def gem(x, y):
    d = abs(x - 7.5) + abs(y - 7.5)
    v = 255 - d * 7
    if x == y or x == 15 - y:
        v = 255
    return v + noise(-6, 6)
tile(7, gem)

# 8 ENERGY (for glow): bright core, darker rim, sparkles
def energy(x, y):
    d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
    v = 255 - d * 9 + noise(-25, 10)
    if rnd.random() < 0.05:
        v = 255
    return max(v, 120)
tile(8, energy)

# 9 CLOUD: puffy blotches
blobs = [(rnd.randrange(16), rnd.randrange(16), rnd.uniform(3, 6)) for _ in range(7)]
tile(9, lambda x, y: 200 + max(0, max(40 - ((x - bx) ** 2 + (y - by) ** 2) / r ** 2 * 40 for bx, by, r in blobs)) + noise(-6, 6))

# 10 LEATHER: stitched border
def leather(x, y):
    v = 195 + noise(-15, 15)
    if (x in (1, 14) or y in (1, 14)) and (x + y) % 2 == 0:
        v = 245
    return v
tile(10, leather)

# 11 GRILLE: speaker mesh dots
tile(11, lambda x, y: 70 if (x % 2 == 0 and y % 2 == 0) else 200 + noise(-10, 10))

# 12 FUR: short vertical strands
tile(12, lambda x, y: 210 + (30 if (x * 5 + y * 3) % 7 == 0 else 0) - (30 if (x * 3 + y) % 5 == 0 else 0) + noise(-10, 10))

# 13 STRIPES: diagonal party stripes (white / light gray)
tile(13, lambda x, y: 255 if ((x + y) // 3) % 2 == 0 else 175)

# 14 STARS: magic fabric with little stars
def stars(x, y):
    v = 205 + noise(-12, 12)
    for sx, sy in ((3, 4), (11, 2), (7, 10), (13, 12), (2, 13)):
        if (abs(x - sx) + abs(y - sy)) <= 1:
            v = 255
    return v
tile(14, stars)

# 15 BLADE: polished edge with a fuller down the middle
def blade(x, y):
    v = 235 + noise(-5, 5)
    if 6 <= x <= 9:
        v -= 25
    if x in (0, 15):
        v = 255
    return v
tile(15, blade)

atlas.save(OUT)
print("wrote", OUT)
