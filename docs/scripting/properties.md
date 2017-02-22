# Properties
Hexagons! game has a property system which as name suggests allows to
set multiple game-related variables using unified interface.
There are 2 functions to set properties.

## Property-related functions
* `game.setProperty(propertyName: String, propertyValue: Value)` - Set 
property directly from code.
* `game.loadProperties(file: String)` - Load properties from a HOCON file.
HOCON is json-like format with more free form syntax, [Examples](https://github.com/typesafehub/config/blob/master/README.md#examples-of-hocon).

## Property list

Property path                          | Type     | Description
---------------------------------------|----------|-------
`rotation.useRadians                 ` | Bool     | Use OH units for rotation
`rotation.speed                      ` | Float    | Rotation speed(rotations/s)
`rotation.maxSpeed                   ` | Float    | Max rotation speed
`rotation.increment                  ` | Float    | Rotation speed
`rotation.rapidSpinSpeed             ` | Float    | Level up spin speed
`rotation.rapidSpin                  ` | Bool(RO) | Whether rapid spin is in progress
`difficulty.difficulty               ` | Float    | Game difficulty. Will be set by game, don't override
`difficulty.delayMultiplier          ` | Float    | Used for patterns.getPerfectDelay
`difficulty.delayMultiplierIncrement ` | Float    | Increment value for `delayMultiplier`
`difficulty.levelIncrement           ` | Float    | Time in seconds to increment level
`difficulty.speed                    ` | Float    | Used for patterns.getBaseSpeed
`difficulty.speedIncrement           ` | Float    | Speed increment
`sides.start                         ` | Int      | Number of sides to start with. Also current side number
`sides.min                           ` | Int      | Minimum number of sides
`sides.max                           ` | Int      | Maximum number of sides
`beatPulse.min                       ` | Float    |  
`beatPulse.max                       ` | Float    | 
`beatPulse.delay                     ` | Float    | Beat frequency, in seconds, pulsing ?
`pulse.min                           ` | Float    | 
`pulse.max                           ` | Float    | 
`pulse.speed                         ` | Float    | 
`pulse.speedReverse                  ` | Float    | 
`pulse.delayMax                      ` | Float    | 
`color.background                    ` | Color[]  | Array of background colors
`color.pulseMin                      ` | Float    | 
`color.pulseMax                      ` | Float    |
`color.pulseIncrement                ` | Float    |
`color.offset                        ` | Int      |
`color.switch                        ` | Float    | How often(seconds) to switch background colors.
`color.walls                         ` | Color    | Wall colors
`color.pingPongForward               ` | Int      | 
`color.pingPongReverse               ` | Int      | 
`color.shadow                        ` | SColor   | Shadow color. Note that it's simple color, so only rgba can be set
`view.layers                         ` | Int      | Shadow layer count
`view.depth                          ` | Float    | Shadow depth
`view.skew                           ` | Float    | Camera skew
`view.minSkew                        ` | Float    | Minimum camera skew
`view.maxSkew                        ` | Float    | Maximum camera skew
`view.skewTime                       ` | Float    | How long one skew cycle takes, in seconds
`view.wallSkewLeft                   ` | Float    | 
`view.wallSkewRight                  ` | Float    |
`view.alphaMultiplier                ` | Float    | 
`view.alphaFalloff                   ` | Float    | Shadow alpha falloff.

## Color type
Color has multiple sub-paths that can be set. Example color definitions:

```hocon
{ r: 0, g: 0, b: 0, a: 1, dynamicDarkness: 8.7, pulse: {r: 0.1764, g: 0.098, b: 0.0039, a: 0}, hue: { min: 0, max: 360, increment: 0.5, pingPong: false } }
{ r: 0, g: 0, b: 0, a: 1, dynamicDarkness: 8.7 }
{ r: 0, g: 0, b: 0, a: 1, hue: { min: 0, max: 360, increment: 0.5, pingPong: false } }
```
As with most properties, you can skip parts you don't need.

### Color properties:
Path               | Range   | Description
-------------------|---------|------------
`.r`               | 0 - 1   | Red channel
`.g`               | 0 - 1   | Green channel
`.b`               | 0 - 1   | Blue channel
`.a`               | 0 - 1   | Alpha channel
`.pulse.r`         | 0 - 1   | Red pulse channel(can be negative)
`.pulse.g`         | 0 - 1   | Green pulse channel(can be negative)
`.pulse.b`         | 0 - 1   | Blue pulse channel(can be negative)
`.pulse.a`         | 0 - 1   | Alpha pulse channel(can be negative)
`.dynamicDarkness` | 0 - ?   | 
`.hue.min`         | 0 - 360 | Minimum hue value
`.hue.max`         | 0 - 360 | Maximum hue value
`.hue.increment`   | 0 - ?   | Hue increment
`.hue.pingPong`    | Boolean | Whether hue increment should bounce when min/max is reached
`.main`            | Boolean | Whether the color is main wall color.

You may need to set `.main` to true property when you use hue/pulse/dynamicDarkness for wall color.
To set color values use setProperty, to set Color array use setAll:
```lua
game.setProperty("color.walls.r", 0.5)
game.setProperty("color.walls.hue.increment", 20)

--No way to set values for each color >yet<
--As workaround for that, you can use game.loadProperties to
--override current background. Take a look at how Dragon Mahyem does that
game.setAll("color.background", "hue.increment", 10)
```