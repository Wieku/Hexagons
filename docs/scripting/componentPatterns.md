# Pattern components

When you are making your own patterns you may want to use some 'parts'
for them. Hexagons have some of these predefined parts.

The difference between these pattern components and other built-in
patterns is that patterns here don't shift the wall timeline.

## List

Code name           | Arguments
--------------------|------------
`patterns.wall`              | `side: Int`, `thickness: Float`
`patterns.wallExtra`         | `side: Int`, `extra: Int`, `thickness: Float`
`patterns.wallOpposite`      | `side: Int`, `thickness: Float`
`patterns.wallOppositeExtra` | `side: Int`, `extra: Int`, `thickness: Float`
`patterns.wallMirrored`      | `side: Int`, `thickness: Float`
`patterns.wallExtraMirrored` | `side: Int`, `extra: Int`, `thickness: Float`
`patterns.barrage`           | `freeSide: Int`, `freeNeighbours: Int`, `thickness: Float`


