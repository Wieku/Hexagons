local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 3), step = 2 } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = 3, delayMult = 0.6, step = 1 } },
    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
    { weight = 2, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 0 } },
    { weight = 1, pattern = standardPattern.vortex{ times = 0, step = game.randomParam(1, 2), 1 } },
    { weight = 1, pattern = standardPattern.fixedDelayBarrageSpiral{ times = game.randomParam(4, 7), delayMult = 0.4, step = 1 } },
    { weight = 3, pattern = standardPattern.randomBarrage{ times = game.randomParam(2, 5), delayMult = 2.25 } },
    { weight = 2, pattern = standardPattern.doubleMirrorSpiral{ times = game.randomParam(4, 6), extra = 0 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(2, 4), extra = 0 } }
}

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
