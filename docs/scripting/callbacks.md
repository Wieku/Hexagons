# Callbacks
Callbacks are global functions that are called by the game when
certain things occur.

## `init`
Called when game wants to initialize a level for new game. This is where
you will usually shuffle patterns and load properties

```lua
function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
end
```

## `initColors`
Called when game needs information on colors the level uses. Called after `init`
and when map is selected in menu.
```lua
function initColors()
    game.loadProperties("colors.hocon")
end
```

## `initEvents`
Called when game wants to initialize event timeline. See [Events](events.md)
```lua
function initEvents()
    game.pushEvent(1, "push_text", "Hello there!", 2)
end
```

## `nextLevel`
Called when level is incremented. Level number is passed as first argument

## `nextPattern`
Called when wall there are no waiting to be spawned walls on the wall
timeline. This is where you generate patterns.

```lua
function nextPattern()
    patternQueue:addNext()
end
```

## `update`
Called each frame. Note that code in this function should be as fast as
possible as it may have big impact on frame rate. First argument is time delta
between frames in seconds.
