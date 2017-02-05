local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 4), step = 2 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(3, 6), extra = 0 } },

    { weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1, step = 1 } },
    { weight = 1, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 2), delayMult = 1, step = 2 } },
    { weight = 1, pattern = standardPattern.barrageSpiral{ times = 2, delayMult = 0.7, step = 1 } },

    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },

    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },

    { weight = 2, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 0 }} }

function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
end

function initColors()
    game.loadProperties("colors.hocon")
end

function nextPattern()
    patternQueue:addNext()
end
