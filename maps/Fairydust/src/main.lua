local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 4), step = 2 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(3, 6), extra = 0 } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1, step = 1 } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1.2, step = 2 } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = 2, delayMult = 0.7, step = 1 } },
    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
    { weight = 2, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 0 } },
    { weight = 2, pattern = standardPattern.vortex{ times = 0, step = 1, 1 } },
    { weight = 1, pattern = standardPattern.fixedDelayBarrageSpiral{ times = game.randomParam(4, 7), delayMult = 0.4, step = 1 } },
    { weight = 3, pattern = standardPattern.randomBarrage{ times = game.randomParam(2, 4), delayMult = 2.25 } }
}

function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
end

function initEvents()
    game.pushEvent(10.9, "rotation.speed", 0.25)
    game.pushEvent(32.6-10.4, "rotation.speed", 0.75)
    game.pushEvent(65.5-32.6, "rotation.speed", 0.75)
    game.pushEvent(87.4-65.5, "rotation.speed", 0.5)
    game.pushEvent(121-87.4, "rotation.speed", 1.25)
    game.pushEvent(175.8-121.0, "rotation.speed", 0.5)
    game.pushEvent(192-175.8, "kill_player")
end

function initColors()
    game.loadProperties("colors.hocon")
end

function nextPattern()
    patternQueue:addNext()
end
