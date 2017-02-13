# Lua functions

## `game` functions
`game` namespace contains utility function for use all over
the code

### `game.newPatternQueue(patternList: list)`
Creates a pattern queue. Takes list of pattern definitions:
```lua
local patternQueue = game.newPatternQueue{
    { weight = [pattern1 weight], pattern = [pattern1 generating function] },
    { weight = [pattern2 weight], pattern = [pattern2 generating function] },
}
```

### `game.randomParam`
TODO

### `game.random`
TODO

### `game.randomSide`
TODO

### `game.randomDir`
TODO

### `game.getHalfSides`
TODO

### `game.loadProperties`
TODO

### `game.setProperty`
TODO

### `game.setAll`
TODO

### `game.getProperty`
TODO

### `game.pushEvent`
TODO


## `patterns`
`patterns` namespace contains functions for use when creating custom patterns.

### `patterns.THICKNESS`
TODO

### `patterns.getPerfectThickness`
TODO

### `patterns.getPerfectDelay`
TODO

### `patterns.getBaseSpeed`
TODO

## `timeline`

### `timeline.addWall{}`
Adds wall on the wall timeline. Has 1 array argument which can have following
properties:
```lua
timeline.addWall{
    side = side, --Int value, side on which the wall spawns
    thickness = thickness, --Wall thicknes in weird OH unit
    speed = speed, --Wall speed
    
    --This is optional
    acceleration = acceleration, --Acceleration in weird OH unit
    speedMin = speedMin,
    speedMax = speedMax, --Min, max speed
    pingPong = pingPong, --Reverses acceleration when reaches min or max
    
    --This is even more optional
    curve = {
        speed = speed, --Rotation speed
            
        --This is optional
        acceleration = acceleration, --Acceleration in weird OH unit
        speedMin = speedMin,
        speedMax = speedMax, --Min, max speed
        pingPong = pingPong, --Reverses acceleration when reaches min or max
    }
}
```
