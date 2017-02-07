local function acceleradiantWall(side, speed, acceleration, speedMin, speedMax)
    timeline.addWall{
        side = side,
        thickness = patterns.THICKNESS,
        speed = speed * patterns.getBaseSpeed(),
        acceleration = acceleration,
        speedMin = speedMin * patterns.getBaseSpeed(),
        speedMax = speedMax * patterns.getBaseSpeed()
    }
end

local function pACBarrage()
    local difficulty = game.getProperty("difficulty.difficulty")
    local sides = game.getProperty("sides.start")
    local delay = patterns.getPerfectDelay(patterns.THICKNESS) * 3.7
    local startSide = game.random(11)
    for i = 0, sides - 2 do
        acceleradiantWall(startSide + i, (9 + game.random(2)), -1.1 * difficulty, 1, 12)
    end
    timeline.wait(delay * 2.5)
end

local function pACBarrageMulti()
    local difficulty = game.getProperty("difficulty.difficulty")
    local sides = game.getProperty("sides.start")
    local delay = patterns.getPerfectDelay(patterns.THICKNESS) * 3.7
    local startSide = game.random(11)

    for i = 0, sides - 2 do
        acceleradiantWall(startSide + i, 10, -1.09 * difficulty, 0.31, 10)
        acceleradiantWall(startSide + i, 0, 0.05 * difficulty, 0, 4)
        acceleradiantWall(startSide + i, 0, 0.09 * difficulty, 0, 4)
        acceleradiantWall(startSide + i, 0, 0.12 * difficulty, 0, 4)
    end
    timeline.wait(delay * 8)
end

local function pACBarrageMultiAltDir()
    local difficulty = game.getProperty("difficulty.difficulty")
    local sides = game.getProperty("sides.start")
    local delay = patterns.getPerfectDelay(patterns.THICKNESS) * 3.7
    local mdiff = 1 + math.abs(1 - difficulty)
    local startSide = game.random(11)
    local loopDir = game.randomDir()

    for i = 0, sides + game.getHalfSides() do
        local side = startSide + i * loopDir
        acceleradiantWall(side, 10, -1.095 * difficulty, 0.40, 10)
        timeline.wait((delay / 2.21) * (mdiff * 1.29))
        acceleradiantWall(side + (game.getHalfSides() * loopDir), 0, 0.128 * difficulty, 0, 1.4)
    end
    timeline.wait(delay * 8)
end


local patternQueue = game.newPatternQueue{
    { weight = 7, pattern = pACBarrage },
    { weight = 2, pattern = pACBarrageMulti },
    { weight = 2, pattern = pACBarrageMultiAltDir }
}

local dirChangeTime = 6.666666
local hueIncMin = 0
local hueIncMax = 22.0
local hueIncStep = 0.0065
local hueInc = 0.5

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

function update(delta)
    dirChangeTime = dirChangeTime - delta
    if dirChangeTime < 0 and not game.getProperty("rotation.rapidSpin") then
        game.setProperty("rotation.speed", game.getProperty("rotation.speed") * -1)
        dirChangeTime = 400 / 60
    end

    hueInc = hueInc + hueIncStep
    game.setProperty("color.walls.hue.increment", hueInc)
    game.setAll("color.background", "hue.increment", hueInc)

    if hueInc > hueIncMax or hueInc < hueIncMin then
        hueIncStep = hueIncStep * -1
    end
end
