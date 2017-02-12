# Creating Maps

## `map.json` setup

Start by creating new directory for the map.
Convert level soundtrack to .ogg format, you can use
[http://audio.online-convert.com/convert-to-ogg](http://audio.online-convert.com/convert-to-ogg) for that.
Put the soundtrack to the directory. Next, open some text editor(SublimeText, Atom, vim, notepad)
and create new file called `map.json`. Paste following:

```json
{
  "audioFileName":"[soundName].ogg",
  "name":"[Level name]",
  "author":"[Level name]",
  "description":"[Level description]",
  "songName":"[Song name]",
  "songAuthor":"[Song author]",
  "tags":"[Level tags]",
  "pack":"[Level pack name]",
  "startTimes":[10, 20, 30], --List of second offsets at which music starts in level
  "previewTime": 40, --Second at which level music starts in menu
  "uuid": "[Level UUID]"
}
```

To get UUID use [UUID generator](https://www.uuidgenerator.net/version4)

## Color setup

Next step is to create `main.lua`, with following content:
```lua
function initColors()
    game.loadProperties("colors.hocon")
end
```

We create global function `initColors`(a callback) which is called each time
level starts or is selected in menu. In we call `loadProperties` function
which loads properties from supplied file.
Now we need to create that file(`colors.hocon`), and put color related
properties in [HOCON format](https://github.com/typesafehub/config/blob/master/HOCON.md#syntax).
You can use JSON if you want as it's valid hocon too.
```hocon
color {
  background: [
    { r: 1, g: 0, b: 1, a: 1 }
    { r: 0, g: 0, b: 0, a: 1, dynamicDarkness: 3.5, hue: { min: 0, max: 360, increment: 0.7, pingPong: false } }
  ]

  walls: { r: 1, g: 0, b: 0, a: 1, main: true, pulse: {r: -0.3, g: 0.2, b: 0.2, a: 0} }

  pulseMax: 1.2
  pulseIncrement: 0.03
  switch: 1
}
```
You can read more on properties on the [Properties](properties.md) page.
If you were to put the level to a zip file and launch the game, you should
see background colors in configuration you specified, and hear soundtrack.

## Basic patterns

Next step is to generate some patterns. We will use built-in utility
to shuffle some standard patterns, put it on top of your code:
```lua
local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 4), step = 2 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(2, 5), extra = function() return game.getHalfSides() - 3 end } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1, step = 1 } },
    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
}
```
The `game.newPatternQueue` function creates gets an array of pattern-weight
definitions. `weight` decides how many times the pattern will occur for one
queue pass, pattern is a FUNCTION generating the pattern. This means that
`standardPattern.inverseBarrage{ times = 0 }` returns a function that can
be called without arguments, that generates pattern with these parameters.

After that, we need to shuffle the queue, we do it in `init` callback.
```lua
function init()
    patternQueue:shuffle()
end
```

And put some patterns onto game timeline when game requests it via
`nextPattern` callback.
```lua
function nextPattern()
    patternQueue:addNext()
end
```
You can put the level into .zip file and try it in game.
If you want more patterns, read the [Standard Patterns](standardPatterns.md)

## Level properties
The level is using default properties for everything except colors.
We can override some of them using `game.loadProperties` function which
loads properties from a file or `game.setProperty` function which sets
a property directly from passed argument.

We want to load our properties once, in the `init` function, change it to:
```lua
function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
end
```

Next, we create the file(`properties.hocon`)
```hocon
rotation {
  speed: 0.1166
  maxSpeed: 0.6666
  increment: 0.0666
  rapidSpinSpeed: 1
}

difficulty {
  delayMultiplier: 1.0
  delayMultiplierIncrement: -0.01
  difficulty: 1
  levelIncrement: 15
  speed: 1.55
  speedIncrement: 0.125
}

beatPulse {
  delay: 0.25
}

pulse {
  min: 75
  max: 91
  speed: 1.2
  speedReverse: 1
  delayMax: 23.9
}

sides {
  start: 6
  min: 5
  max: 6
}

view {
  layers: 8
  depth: 1.5
  skew: 0
  minSkew: 0
  maxSkew: 1
  skewTime: 5
}
```
These are properties from the `Pointless` level, change them as you wish.
All the values are described on the [Properties](properties.md) page.

