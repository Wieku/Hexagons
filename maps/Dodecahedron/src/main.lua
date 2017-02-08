local patternQueue = game.newPatternQueue{
    { weight = 2, pattern = standardPattern.alternatingBarrage{ times = game.randomParam(2, 3), step = 2 } },
    { weight = 4, pattern = standardPattern.barrageSpiral{ times = 3, delayMult = 0.6, step = 1 } },
    { weight = 2, pattern = standardPattern.inverseBarrage{ times = 0 } },
    { weight = 1, pattern = standardPattern.tunnel{ times = game.randomParam(1, 3) } },
    { weight = 2, pattern = standardPattern.mirroredWallStrip{ times = 1, extra = 0 } },
    { weight = 1, pattern = standardPattern.vortex{ times = 0, step = game.randomParam(1, 2), 1 } },
    { weight = 1, pattern = standardPattern.fixedDelayBarrageSpiral{ times = game.randomParam(4, 7), delayMult = 0.4, step = 1 } },
    { weight = 3, pattern = standardPattern.randomBarrage{ times = game.randomParam(2, 5), delayMult = 2.25 } },
    { weight = 1, pattern = standardPattern.doubleMirrorSpiral{ times = game.randomParam(4, 6), extra = 0 } },
    { weight = 2, pattern = standardPattern.mirrorSpiral{ times = game.randomParam(2, 4), extra = 0 } }
}

local dirChangeTime = 2.666666

function init()
    patternQueue:shuffle()
    game.loadProperties("properties.hocon")
    dirChangeTime = 46.1
end

local function lyric(text, wait, duration)
    game.pushEvent(wait, "push_text", text, duration)
end

function initEvents()
    lyric("Are you ready?", 2, 0.5)

    for i = 1, 52 do
        lyric("EH", 0.4 + (i == 1 and 0.5 or 0), 0.2)
        game.pushEvent(0, "change_direction")
    end

    for i = 15, 0, -1 do
        game.pushEvent(0.09, "rotation.speed", 0.65 * (i / 15))
    end

    lyric("Are you ready?", 0.15, 0.5)
    game.pushEvent(0.09, "rotation.speed", 0.65 + 0.053)

    for i = 1, 52 do
        lyric("EH", 0.4 + (i == 1 and 0.5 or 0), 0.2)
        game.pushEvent(0, "change_direction")
    end

    lyric("Adrenaline is pumping", 2, 1.5)
    lyric("Adrenaline is pumping", 2, 1.5)
    lyric("Generator", 2.5, 1)
    lyric("Automatic lover", 2, 1.5)
    lyric("Atomic", 4, 0.5)
    lyric("Atomic", 1.5, 0.5)
    lyric("Overdrive", 1.5, 1.0)
    lyric("Blockbuster", 2.0, 1)
    lyric("Brainpower", 3, 1)

    lyric("Call me leader", 3, 1)
    lyric("Cocaine", 2, 1)
    lyric("Don't you try it", 3.5, 1.5)
    lyric("Don't you try it", 2.5, 1.5)

    lyric("Innovator", 3, 1)
    lyric("Killing machine", 2, 1.5)
    lyric("There's no fate", 3, 1.5)
    lyric("Take control", 3, 1.5)
    lyric("BRAINPOWER", 2.5, 1.5)

    lyric("LET THE BASE KICK!", 2.0, 1.5)
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
