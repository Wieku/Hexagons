# Event systems

There are 2 events systems exposed for map scripts. First is used to trigger certain actions in the game
by putting events onto event timeline, second is used by the game to notify level scripts of certain
things happening for example key being pressed.

# Event timeline system

The game has simple event system. There is event timeline which is separate
from wall timeline. Events should be initialized in `initEvents` callback.
To put events onto the timeline use `game.pushEvent(timeOffset: float, event: String, args...)`,
where `timeOffset` is offset in seconds from last pushed event, `event` is
either event name, [property](properties.md#property-list) name to be set
or Lua function to execute. `args` depend on what event was set to. If event
was Lua function, then all args are passed to it, `event` is name of a property,
then first value of args is the value to be set.
 
## Event list

Event name         | Arguments     | Description
-------------------|---------------|--------------
`kill_player`      | None          | Kills player
`change_direction` | None          | Reverses rotation direction
`push_text`        | String, Float | Displays text on screen, second argument is duration
`function`         | Function      | Executes lua function

## Example usage

```lua
function initEvents()
    game.pushEvent(1, "push_text", "Hello there!", 2)
    game.pushEvent(5, "color.walls.hue.increment", 20)
    game.pushEvent(20, function() game.loadProperties("cororsBilnding.hocon") end)
end
```

# Event notification system

There is second event system that is used to notify script of certain actions such as key pressed.
Events are delivered using callback functions defined in global `event` table.

```lua
function event.keyDown(keyCode)
    print("Key pressed: " .. keys[keyCode])
end
```

## Event list

Event name         | Arguments
-------------------|------------
keyDown            | `keyCode: Int`
keyUp              | `keyCode: Int`
mouseDown          | `x: Float`, `y: Float`, `pointer: Int`, `button: Int`
mouseUp            | `x: Float`, `y: Float`, `pointer: Int`, `button: Int`
mouseMove          | `x: Float`, `y: Float`, `pointer: Int`

For key events there is `keys` table which contains two-way list of keys. This means that when indexed
by key name, the result will be keyCode and when indexed by keyCode the result will be key name. For
mouse buttons there is `buttons` table which works in the same way. See [List of keys](keys.md).

`pointer` is pointer id, for desktop it is usually 0 and is only used for multi-touch devices.

`x` and `y` is 0-1 value for mouse position in window.
