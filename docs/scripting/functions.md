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

### `game.randomParam(start: Int, end: Int): Function():Int`
Returns a [closure](https://en.wikipedia.org/wiki/Closure_(computer_programming))
(function) which upon calling returns integer value within specified range.

### `game.random(max): Int`
Returns a random integer from 0 to specified max. `max` has to be positive.

### `game.randomSide(): Int`
Returns a integer from 1 up to current wall count

### `game.randomDir(): Int`
Returns either -1 or 1 randomly

### `game.getHalfSides(): Int`
Returns current wall count divided by 2 and rounded down

### `game.loadProperties(file: String)`
Loads properties from a file located inside map directory. File is file path
relative to map root directory. See [properties](properties.md) page.

### `game.setProperty(property: String, value)`
Sets a single level property under given path.
See [Properties](properties.md) page.

### `game.setAll(basePath: String, subPath: String, value)`
Sets a value for all elements in a property list. `basePath` is path of the list,
`subPath` is path inside list elements to be set. See [properties](properties.md) page.

### `game.getProperty(path: String): Value`
Returns value of a property under specified path. See [properties](properties.md) page.a

### `game.pushEvent(timeOffset: Float, event: String, args...)`
Pushes event onto game event timeline. See [events](events.md) page.

## `patterns`
`patterns` namespace contains functions for use when creating custom patterns.

### `patterns.THICKNESS`
Base wall thickness.

### `patterns.getPerfectThickness(thickness: Float): Float`
returns a good thickness value in relation to human reflexes

### `patterns.getPerfectDelay(delay: Float): Float`
TODO

### `patterns.getBaseSpeed(): Float`
TODO

### `patterns.getSideDistance(from: Int, to: Int): Int`
Returns shortest distance from a side to another

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

### `timeline.wait(time: Float)`
Shifts wall timeline 'base' by specified amount of time. The unit is 1/60 of a
second.