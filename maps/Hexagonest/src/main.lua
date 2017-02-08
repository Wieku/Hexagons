local function superhexTunnel(times, delayMult)
    return function()
        local thickness = patterns.getPerfectThickness(patterns.THICKNESS)
        local delay = patterns.getPerfectDelay(thickness) * 5.5
        local startSide = game.randomSide()

        for i = 1, times() do
            timeline.addWall{side = startSide, thickness = thickness + 9 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }
            timeline.addWall{side = startSide + 1, thickness = thickness + 9 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }
            timeline.addWall{side = startSide + 2, thickness = thickness + 9 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }
        end

        patterns.barrage(startSide + 3, patterns.THICKNESS)
        timeline.wait(delay * 0.65 * delayMult)
        patterns.barrage(startSide + 5, patterns.THICKNESS)
        timeline.wait(delay * 0.65 * delayMult)

    end
end

local function tripleWall(times)
    return function()
        local thickness = patterns.getPerfectThickness(patterns.THICKNESS)
        local delay = patterns.getPerfectDelay(thickness) * 5.5
        local startSide = game.randomSide()

        for i = 1, times() do
            game.timeline.addWall{side = startSide, thickness = thickness + 9 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }
            game.timeline.addWall{side = startSide + 1, thickness = thickness + 9 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }
            game.timeline.addWall{side = startSide + 2, thickness = thickness + 9 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }
        end

        game.timeline.wait(delay)
    end
end

local patternQueue = game.newPatternQueue{
    --{ weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 4), step = 2 } },
    --{ weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(3, 6), extra = 0 } },
    --{ weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1, step = 1 } },
    --{ weight = 1, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 2), delayMult = 1.2, step = 2 } },
    --{ weight = 1, pattern = standardPattern.barrageSpiral{ times = 2, delayMult = 0.7, step = 1 } },
    --{ weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    --{ weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
    --{ weight = 2, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 0 } },
    --{ weight = 1, pattern = standardPattern.vortex{ times = 0, step = 1, 1 } },
    { weight = 1, pattern = superhexTunnel(game.randomParam(1, 1), 1.02) }

}

local dirChangeTime = 3

function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
    dirChangeTime = 3
end

function initColors()
    game.loadProperties("colors.hocon")
end

function nextPattern()
    patternQueue:addNext()
end

function update(delta)
    dirChangeTime = dirChangeTime - delta
    if dirChangeTime < 0 and not game.getProperty("rotation.rapidSpin") then
        game.setProperty("rotation.speed", game.getProperty("rotation.speed") * -1)
        dirChangeTime = 7
    end
end
