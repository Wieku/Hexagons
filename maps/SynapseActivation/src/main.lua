local BARRAGE_THICKNESS = 25

local function synapticBarrageSpiral(times, delayMult, step)
    return function()
        local delay = patterns.getPerfectDelay(BARRAGE_THICKNESS) * 5.6 * delayMult
        local startSide = game.randomSide()
        local loopDir = step * game.randomDir()
        local j = 0

        for i = 0, times do
            patterns.barrage(startSide + j, 0, BARRAGE_THICKNESS)
            j = j + loopDir
            timeline.wait(delay * patterns.getPerfectDelay(BARRAGE_THICKNESS) * 0.6)
        end
        timeline.wait(patterns.getPerfectDelay(BARRAGE_THICKNESS) * 6.1)
    end
end

local function synapticTunnelFastAlt(ftimes)
    return function()
        local delay = patterns.getPerfectDelay(patterns.getPerfectThickness(BARRAGE_THICKNESS)) * 2
        local startSide = game.randomSide()
        local loopDir = game.randomDir()
        local times = ftimes()

        for i = 0, times do
            timeline.addWall{side = startSide, thickness = BARRAGE_THICKNESS + 5 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }

            patterns.barrage(startSide + loopDir * game.getHalfSides(), 0, BARRAGE_THICKNESS)
            timeline.wait(delay)
            loopDir = loopDir * -1
        end
        timeline.wait(delay)
    end
end

local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = synapticBarrageSpiral(10, 0.5, 1) },
    { weight = 3, pattern = synapticTunnelFastAlt(game.randomParam(4, 10)) }
}

local dirChangeTime = 1.666666

function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
    dirChangeTime = 1.666666
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
        dirChangeTime = 300 / 60
    end
end
