local patternQueue = game.newPatternQueue{
    { weight = 17, pattern = function()
        patterns.wallExtra(game.random(game.getProperty("sides.start")), math.random(1, 2), patterns.THICKNESS)
        timeline.wait(patterns.getPerfectDelay(patterns.THICKNESS) * 2.5)
    end },
    { weight = 1, pattern = standardPattern.doubleMirrorSpiral{ times = game.randomParam(1, 2), extra = 4 } },
    { weight = 3, pattern = function()
        patterns.wallExtraMirrored(game.random(game.getProperty("sides.start")), math.random(1, 2), patterns.THICKNESS)
        timeline.wait(patterns.getPerfectDelay(patterns.THICKNESS) * 2.8)
    end },
    { weight = 3, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 2 } },
    { weight = 5, pattern = function()
        patterns.wallExtraMirrored(game.random(game.getProperty("sides.start")), 1, patterns.THICKNESS)
        timeline.wait(patterns.getPerfectDelay(patterns.THICKNESS) * 2.3)
    end },
    { weight = 4, pattern = function()
        patterns.wallExtra(game.random(game.getProperty("sides.start")), 7, patterns.THICKNESS)
        timeline.wait(patterns.getPerfectDelay(patterns.THICKNESS) * 2.7)
    end }
}

local dirChangeTime = 2.5

function init()
    patternQueue:shuffle()
    dirChangeTime = 2.5
    game.loadProperties("properties.hocon")
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
        dirChangeTime = 100 / 60
    end
end
