local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 4), step = 2 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(2, 5), extra = function() return game.getHalfSides() - 3 end } },
    { weight = 2, pattern = standardPattern.barrageSpiral{ times = game.randomParam(0, 3), delayMult = 1, step = 1 } },
    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
}

function init()
    patternQueue:shuffle()
    map.loadProperties("properties.hocon")
end

function initColors()
    map.loadProperties("colors.hocon")
end

function nextPattern()
    patternQueue:addNext()
end
