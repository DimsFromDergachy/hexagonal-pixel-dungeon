# Hexagonal Coordinate System - Technical Documentation

This document provides comprehensive technical details about the hexagonal coordinate system implementation in Hexagonal Pixel Dungeon.

## Table of Contents

1. [Overview](#overview)
2. [Coordinate Systems](#coordinate-systems)
3. [Implementation Details](#implementation-details)
4. [Mathematical Foundations](#mathematical-foundations)
5. [Code Reference](#code-reference)
6. [Common Operations](#common-operations)
7. [Troubleshooting](#troubleshooting)
8. [Performance Considerations](#performance-considerations)

---

## Overview

Hexagonal Pixel Dungeon uses a hexagonal grid system instead of the traditional square grid. This requires careful coordinate management to convert between different representations:

- **Offset Coordinates** - Storage format (odd-q vertical layout)
- **Cube Coordinates** - Calculation format (q, r, s where q+r+s=0)
- **Pixel Coordinates** - Screen rendering format

### Why Multiple Coordinate Systems?

Each coordinate system has specific advantages:

| System | Best For | Format |
|--------|----------|--------|
| Offset | Storage, indexing, level arrays | (x, y) |
| Cube | Distance, neighbors, pathfinding | (q, r, s) |
| Pixel | Rendering, user input | (px, py) |

**Key Principle:** Store in offset, calculate in cube, render in pixels.

---

## Coordinate Systems

### 1. Offset Coordinates (Odd-Q Vertical Layout)

**What it is:** The primary storage format. Similar to square grid coordinates but adapted for hexagons.

**Layout:**
```
     (0,0) (1,0) (2,0) (3,0)
  (0,1) (1,1) (2,1) (3,1)
     (0,2) (1,2) (2,2) (3,2)
  (0,3) (1,3) (2,3) (3,3)
```

**Characteristics:**
- Odd columns are shifted down by half a tile
- X increases going right
- Y increases going down
- Easy to convert to/from 1D array index: `index = y * width + x`
- Used by level generation and storage

**Visual Pattern:**
```
   / \ / \ / \ / \
  |0,0|1,0|2,0|3,0|
   \ / \ / \ / \ /
  |0,1|1,1|2,1|3,1|
   / \ / \ / \ / \
  |0,2|1,2|2,2|3,2|
   \ / \ / \ / \ /
```

### 2. Cube Coordinates

**What it is:** A 3D coordinate system where hexagons lie on a plane where q+r+s=0.

**Format:** (q, r, s) where q + r + s = 0

**Why use it:**
- Distance calculation is trivial: `max(|q1-q2|, |r1-r2|, |s1-s2|)`
- Neighbor finding is simple: add direction vectors
- Rotation and reflection are straightforward
- Many hex algorithms work naturally in cube coords

**Axes:**
```
      +r
       \
        \
   +q ------- -s
        /
       /
      +s
```

**Direction Vectors (6 neighbors):**
```
(+1, 0, -1)  (+1, -1, 0)
       \ /
        *
       / \
(-1, +1, 0)  (0, +1, -1)
```

**Constraint:** The third axis (s) is always: `s = -q - r`

### 3. Pixel Coordinates

**What it is:** Screen space coordinates for rendering.

**Tile Dimensions:**
- Width: 18 pixels
- Height: 16 pixels
- Aspect ratio: 18:16 (not 1:1 like square tiles)

**Hex Geometry:**
```
     14 pixels wide at center
    /‾‾‾‾‾‾‾‾‾‾‾‾‾‾\
   /                \
  /                  \  16 pixels tall
  \                  /
   \                /
    \______________/
    
     8 pixel horizontal offset per row
```

**Transformation Matrix (Hex to Pixel):**
```
| px |   | 14   0 | | q |
| py | = |  8  16 | | r |
```

**Inverse Matrix (Pixel to Hex):**
```
| q |   |  1/14      0 | | px |
| r | = | -1/28   1/16 | | py |
```

---

## Implementation Details

### HexMath.java - Core Utilities

Located in: `SPD-classes/src/main/java/com/watabou/utils/HexMath.java`

**Key Constants:**

```java
public static boolean HEX_MODE = true;
public static float RATIO = 14f / 18f;  // 0.777... for width adjustment

// Hex to Pixel transformation matrix
private static float A11 = 14f,  A12 = 0f;
private static float A21 = 8f,   A22 = 16f;

// Pixel to Hex inverse matrix
private static float H11 = 1f/14f,   H12 = 0f;
private static float H21 = -1f/28f,  H22 = 1f/16f;
```

### Conversion Functions

#### 1. Offset to Pixel

```java
public static PointF HexToPixel(int x, int y) {
    // Convert offset to cube
    int q = x;
    int r = y - x / 2;
    
    // Apply transformation matrix
    return new PointF(A11 * q + A12 * r, A21 * q + A22 * r);
}
```

**Example:**
- Input: (2, 3) in offset coords
- Cube: q=2, r=3-2/2=3-1=2
- Pixel: (14*2 + 0*2, 8*2 + 16*2) = (28, 48)

#### 2. Pixel to Offset

```java
public static Point PixelToHex(PointF point) {
    // Adjust for tile center
    point = point.offset(-9f, -8f);
    
    // Apply inverse matrix to get cube coords
    float f_q = H11 * point.x + H12 * point.y;
    float f_r = H21 * point.x + H22 * point.y;
    float f_s = 0 - f_q - f_r;
    
    // Round to nearest hex (cube coordinate rounding)
    int q = Math.round(f_q);
    int r = Math.round(f_r);
    int s = Math.round(f_s);
    
    // Fix rounding errors (restore constraint q+r+s=0)
    float q_diff = Math.abs(q - f_q);
    float r_diff = Math.abs(r - f_r);
    float s_diff = Math.abs(s - f_s);
    
    if (q_diff > r_diff && q_diff > s_diff)
        q = 0 - r - s;
    else if (r_diff > s_diff)
        r = 0 - q - s;
    else
        s = 0 - q - r;
    
    // Convert cube to offset
    return FromCube(q, r);
}
```

**Why the complex rounding?**
- Floating point to integer conversion can violate q+r+s=0
- We find which coordinate has the largest rounding error
- Recalculate that coordinate to restore the constraint

#### 3. Offset ↔ Cube Conversion

```java
// Offset to Cube
public static Point ToCube(int x, int y) {
    return new Point(x, y - x / 2);
}

// Cube to Offset (odd-q vertical layout)
public static Point FromCube(int q, int r) {
    return new Point(q, r + q / 2);
}
```

**Examples:**

| Offset (x, y) | Cube (q, r, s) | Notes |
|---------------|----------------|-------|
| (0, 0) | (0, 0, 0) | Origin |
| (1, 0) | (1, -1, 0) | Right neighbor |
| (0, 1) | (0, 1, -1) | Down-left |
| (1, 1) | (1, 0, -1) | Down-right |
| (2, 1) | (2, 0, -2) | Two right, offset |

---

## Mathematical Foundations

### Why Odd-Q Vertical Layout?

**Odd-Q** means odd columns (q=1, 3, 5...) are shifted down by half a tile.

**Benefits:**
1. Simpler array indexing than other layouts
2. Less waste in rectangular level arrays
3. Natural fit for vertical scrolling games
4. Compatible with original square grid storage

**Alternatives considered:**
- Even-Q: Even columns offset (not used)
- Pointy-top variants: Rotate 90° (not used)
- Axial coordinates: Store only (q, r) (not used - less intuitive)

### Distance Calculation

**Manhattan Distance in Cube Coordinates:**

```
distance(hex1, hex2) = max(|q1-q2|, |r1-r2|, |s1-s2|)
```

**Implementation:**

```java
public static int distance(int x1, int y1, int x2, int y2) {
    // Convert to cube
    int q1 = x1;
    int r1 = y1 - x1 / 2;
    int s1 = 0 - q1 - r1;
    
    int q2 = x2;
    int r2 = y2 - x2 / 2;
    int s2 = 0 - q2 - r2;
    
    // Max of absolute differences
    return GameMath.Max(
        Math.abs(q1 - q2),
        Math.abs(r1 - r2),
        Math.abs(s1 - s2)
    );
}
```

**Why it works:**
- In cube coords, movement along each axis is orthogonal
- Moving one hex changes exactly one coordinate by ±1
- Maximum change across all three axes = minimum steps needed

### Euclidean Distance

For actual pixel distance (useful for ranges, FOV):

```java
public static float trueDistance(int x1, int y1, int x2, int y2) {
    PointF p1 = HexToPixel(x1, y1);
    PointF p2 = HexToPixel(x2, y2);
    
    return (float) Math.sqrt(
        Math.pow(p1.x - p2.x, 2f) + 
        Math.pow(p1.y - p2.y, 2f)
    );
}
```

---

## Code Reference

### Where Hex Coordinates Are Used

#### 1. DungeonTileMap.java

**Purpose:** Main dungeon rendering and input handling

```java
public static final int WIDTH = HexMath.HEX_MODE ? 18 : 16;
public static final int HEIGHT = 16;

// Convert screen tap/click to tile
public int screenToTile(PointF point, boolean wallAssist) {
    Point p = HexMath.PixelToHex(point);
    // ... boundary checking ...
    return p.x + p.y * Dungeon.level.width();
}

// Convert tile index to pixel position
public static PointF tileCenterToWorld(int pos) {
    int x = pos % Dungeon.level.width();
    int y = pos / Dungeon.level.width();
    return HexMath.HexToPixel(x, y).offset(adjustX, adjustY);
}
```

#### 2. Level.java

**Purpose:** Game logic, distance checks

```java
public int distance(int a, int b) {
    int ax = a % width();
    int ay = a / width();
    int bx = b % width();
    int by = b / width();
    return HexMath.distance(ax, ay, bx, by);
}
```

#### 3. Sprite.java

**Purpose:** Character and item positioning

```java
public PointF worldToCamera(int cell) {
    if (HexMath.HEX_MODE)
        return DungeonTileMap.tileCenterToWorld(cell)
            .offset(-width() / 2f, -height() / 2f - height() * perspectiveRaise);
    else
        return DungeonTileMap.tileToWorld(cell)
            .offset(0, -height() * perspectiveRaise);
}
```

#### 4. HexTileMap.java

**Purpose:** Low-level tile rendering

```java
// Adjust width for hex tiles
x1 = HexMath.RATIO * cellW * updating.left;

// Offset odd columns
y1 = y0 + (HexMath.HEX_MODE ? (j & 1) * 0.5f * cellH : 0);

// Increment by adjusted width
x1 += cellW * HexMath.RATIO;
```

---

## Common Operations

### Finding Neighbors

**Square Grid (8 directions):**
```java
int[] NEIGHBORS = {-1, +1, -width, +width, -width-1, -width+1, +width-1, +width+1};
```

**Hexagonal Grid (6 directions):**

In odd-q vertical layout, neighbors depend on column parity:

**Even columns (x % 2 == 0):**
```
      [x, y-1]
  [x-1, y-1] [x+1, y-1]
      [x, y]
  [x-1, y]   [x+1, y]
```

**Odd columns (x % 2 == 1):**
```
  [x-1, y]   [x+1, y]
      [x, y]
  [x-1, y+1] [x+1, y+1]
      [x, y+1]
```

**Implementation needed in PathFinder.java:**

```java
public static int[] getHexNeighbors(int cell, int mapWidth) {
    int x = cell % mapWidth;
    int y = cell / mapWidth;
    
    if (x % 2 == 0) { // Even column
        return new int[] {
            cell - mapWidth,           // N
            cell - mapWidth + 1,       // NE
            cell + 1,                  // SE
            cell + mapWidth,           // S
            cell - 1,                  // SW
            cell - mapWidth - 1        // NW
        };
    } else { // Odd column
        return new int[] {
            cell - mapWidth,           // N
            cell + 1,                  // NE
            cell + mapWidth + 1,       // SE
            cell + mapWidth,           // S
            cell + mapWidth - 1,       // SW
            cell - 1                   // NW
        };
    }
}
```

### Line Drawing (Hex Grid)

To draw a line between two hexes:

1. Convert both to cube coordinates
2. Interpolate in cube space
3. Round each interpolated point
4. Convert back to offset

```java
public static List<Point> hexLine(int x0, int y0, int x1, int y1) {
    Point cube0 = HexMath.ToCube(x0, y0);
    Point cube1 = HexMath.ToCube(x1, y1);
    
    int N = HexMath.distance(x0, y0, x1, y1);
    List<Point> results = new ArrayList<>();
    
    for (int i = 0; i <= N; i++) {
        float t = i / (float)N;
        float q = cube0.x * (1 - t) + cube1.x * t;
        float r = cube0.y * (1 - t) + cube1.y * t;
        float s = -(q + r);
        
        // Round and convert back
        Point rounded = roundCube(q, r, s);
        results.add(HexMath.FromCube(rounded.x, rounded.y));
    }
    
    return results;
}
```

### Ring/Radius Operations

Get all hexes within radius R:

```java
public static List<Point> hexesInRange(int cx, int cy, int radius) {
    List<Point> results = new ArrayList<>();
    Point center = HexMath.ToCube(cx, cy);
    
    for (int q = -radius; q <= radius; q++) {
        for (int r = Math.max(-radius, -q - radius); 
             r <= Math.min(radius, -q + radius); r++) {
            int s = -q - r;
            results.add(HexMath.FromCube(center.x + q, center.y + r));
        }
    }
    
    return results;
}
```

---

## Troubleshooting

### Common Issues

#### Issue: Clicks are off by one tile

**Symptom:** Clicking a hex selects an adjacent hex

**Causes:**
1. Missing the -9f, -8f offset in `PixelToHex()`
2. Incorrect tile center calculation
3. Camera/viewport offset not accounted for

**Debug:**
```java
System.out.println("Screen: " + point);
System.out.println("Hex: " + HexMath.PixelToHex(point));
System.out.println("Back to Pixel: " + HexMath.HexToPixel(p.x, p.y));
```

#### Issue: Pathfinding goes through walls

**Symptom:** Characters move in unexpected directions

**Causes:**
1. Using old 4/8-directional neighbor array
2. Not checking hex-specific adjacency
3. Odd/even column neighbor logic incorrect

**Solution:** Update `PathFinder.java` with hex-aware neighbor function

#### Issue: Visual misalignment

**Symptom:** Sprites don't line up with tiles

**Causes:**
1. Sprite anchor point incorrect
2. Missing perspective offset
3. Tile width/height constants wrong

**Check:**
```java
// Should be 18, not 16
DungeonTileMap.WIDTH == 18

// Should account for hex layout
HexMath.RATIO == 14f / 18f
```

### Debugging Tools

**Visualize coordinates:**

```java
// Add to DungeonTileMap.java for debugging
public void drawCoordinateLabels() {
    for (int i = 0; i < map.length; i++) {
        int x = i % Dungeon.level.width();
        int y = i / Dungeon.level.width();
        Point cube = HexMath.ToCube(x, y);
        PointF pixel = HexMath.HexToPixel(x, y);
        
        // Draw text at pixel position
        // Format: "x,y\nq,r,s"
    }
}
```

---

## Performance Considerations

### Optimization Opportunities

1. **Cache conversions:** If converting same coordinates repeatedly
2. **Lookup tables:** Pre-compute neighbors for small grids
3. **Integer math:** Avoid floating point where possible
4. **Batch conversions:** Convert multiple points in one pass

### Current Performance

**Coordinate conversions are cheap:**
- Offset ↔ Cube: ~2 ops (addition, division)
- Hex ↔ Pixel: ~4 multiplications, 2 additions
- Distance: ~6 subtractions, 3 comparisons

**No significant performance impact observed** in testing.

### Future Optimizations

If performance becomes an issue:

1. **Pre-compute neighbor lookup table:**
```java
private static int[][] NEIGHBOR_TABLE; // [cellIndex][neighborNum]
```

2. **SIMD/vector operations** for bulk conversions

3. **Spatial hashing** for proximity queries

---

## References

### External Resources

- **Red Blob Games:** https://www.redblobgames.com/grids/hexagons/
  - Comprehensive hexagonal grid guide
  - Reference implementation in multiple languages
  
- **Hexagonal Grids (PDF):** https://www.redblobgames.com/grids/hexagons/hexagons.pdf
  - Printable reference
  
- **Interactive Demos:** https://www.redblobgames.com/grids/hexagons/implementation.html

### Internal Documentation

- `AI_AGENT_GUIDE.md` - Project overview and development guidelines
- `docs/hexagonal/dt.md` - Quick reference and TODO list
- `docs/hexagonal/visual-hierarchy.md` - Rendering class hierarchy

---

## Appendix: Coordinate Tables

### Small Grid Example (5x5)

| Offset | Cube (q,r,s) | Pixel (approx) |
|--------|--------------|----------------|
| 0,0 | 0,0,0 | 0,0 |
| 1,0 | 1,-1,0 | 14,8 |
| 2,0 | 2,-1,-1 | 28,16 |
| 0,1 | 0,1,-1 | 0,16 |
| 1,1 | 1,0,-1 | 14,24 |
| 2,1 | 2,0,-2 | 28,32 |
| 0,2 | 0,2,-2 | 0,32 |
| 1,2 | 1,1,-2 | 14,40 |
| 2,2 | 2,1,-3 | 28,48 |

### Direction Vectors (Cube Space)

| Direction | Offset (even col) | Offset (odd col) | Cube (Δq,Δr,Δs) |
|-----------|-------------------|------------------|-----------------|
| N (top) | 0,-1 | 0,-1 | 0,-1,+1 |
| NE | +1,-1 | +1,0 | +1,-1,0 |
| SE | +1,0 | +1,+1 | +1,0,-1 |
| S (bottom) | 0,+1 | 0,+1 | 0,+1,-1 |
| SW | -1,0 | -1,+1 | -1,+1,0 |
| NW | -1,-1 | -1,0 | -1,0,+1 |

---

*Last updated: 2025*
*For implementation questions, see: `HexMath.java` in SPD-classes module*