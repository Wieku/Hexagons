# Event system

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
