# Built-in patterns

The game provides few basic patterns. All those patterns are in the
`standardPatterns` table. All the patterns are functions which get one array
argument which holds parameters for pattern generation and return a [closure](https://en.wikipedia.org/wiki/Closure_(computer_programming))
which upon calling puts the pattern on the wall timeline. All pattern parameters
can be a simple value, or a function returning that value. If parameter is
a function it will be invoked each time the pattern is generated.

Image  | Code name                 | Notes
-------|---------------------------|--------
[TODO] | `alternatingBarrage`      | 
[TODO] | `mirrorSpiral`            |
[TODO] | `doubleMirrorSpiral`      |
[TODO] | `barrageSpiral`           |
[TODO] | `inverseBarrage`          |
[TODO] | `tunnel`                  |
[TODO] | `mirroredWallStrip`       |
[TODO] | `vortex`                  |
[TODO] | `fixedDelayBarrageSpiral` |
[TODO] | `randomBarrage`           |

## `alternatingBarrage`
Parameters:

* `times` - Number of times the pattern repeats
* `step` - TODO

## `mirrorSpiral`
Parameters:

* `times` - Number of times the pattern repeats
* `extra` - TODO

## `doubleMirrorSpiral`
Parameters:

* `times` - Number of times the pattern repeats
* `extra` - TODO

## `barrageSpiral`
Parameters:

* `times` - Number of times the pattern repeats
* `delayMult` - Delay multiplier
* `step` - TODO

## `inverseBarrage`
Parameters:

* `times` - Number of times the pattern repeats

## `tunnel`
Parameters:

* `times` - Number of times the pattern repeats

## `mirroredWallStrip`
Parameters:

* `times` - Number of times the pattern repeats
* `extra` - TODO

## `vortex`
Parameters:

* `times` - Number of times the pattern repeats
* `extra` - TODO
* `step` - TODO

## `fixedDelayBarrageSpiral`
Parameters:

* `times` - Number of times the pattern repeats
* `delayMult` - Delay multiplier
* `step` - TODO

## `randomBarrage`
Parameters:

* `times` - Number of times the pattern repeats
* `delayMult` - Delay multiplier


