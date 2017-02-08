local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 4), step = 2 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(3, 6), extra = 0 } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1, step = 1 } },
    { weight = 1, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 2), delayMult = 1.2, step = 2 } },
    { weight = 1, pattern = standardPattern.barrageSpiral{ times = 2, delayMult = 0.7, step = 1 } },
    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
    { weight = 2, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 0 } }
}

function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
end

function initEvents()
    game.pushEvent(65.4, function()
        game.loadProperties("colors2.hocon")
    end)
    game.pushEvent(0, "push_text", "Try this now!", 150 / 60)

    game.pushEvent(85, function()
        game.loadProperties("colors3.hocon")
    end)
    game.pushEvent(0, "push_text", "How you can do this? Impossible!", 150 / 60)
end

function initColors()
    game.loadProperties("colors.hocon")
end

function nextPattern()
    patternQueue:addNext()
end
