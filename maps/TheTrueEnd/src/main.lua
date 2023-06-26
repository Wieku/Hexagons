-- Patterns for the The True End map
-- Common patterns from Open Hexagon by Vittorio Romeo
-- Fluxtuation patterns by Quoz/RNBW
-- Extremlination patterns by SkyMidnight
-- Sensationality patterns by Morxemplum
-- Press, Dwindling, Opal VIP Tunnel patterns from Quartz by Kaylaxie
-- Remix pack 1.92, Polygonal Cubics, and Last Stand patterns by Mandrake
-- exschBackAndForth and flipper patterns by Exschwasion
-- hxdsTknsDoubleTunnel patterns by Hexadorsip
-- Synapse Activation patterns by Magik6k
-- Super Hexagon's 321 and Bat pattens by Terry, modification by me (Marchionne)

local THICKNESS = 40

local function setSides(mShapes) return game.setProperty("sides.start", mShapes) end
local function getSides() return game.getProperty("sides.start") end
local function getDifficultyMult() return game.getProperty("difficulty.difficulty") end

local delayOverride = 1

local function custWait(mDelay)
    return (patterns.getPerfectDelay(THICKNESS) * (mDelay * delayOverride) * (patterns.getBaseSpeed()) / 2)
end

--[[ if your confused on why its based on delay, thats because this is specifically used in tunnel patterns!
because of the large wall needing to have  a calculation for its size it cant be based on getPerfectDelay()

instead it uses regular THICKNESS (depends actually, ill update this if that changes) ]]
local function custThickness(mDelay)
    return ((THICKNESS * game.getProperty("difficulty.delayMultiplier")) * (mDelay * delayOverride) * (patterns.getBaseSpeed()) / 2)
end

local function wall(mSide, mThickness)
    timeline.addWall{ side = mSide, thickness = mThickness or THICKNESS, speed = patterns.getBaseSpeed() }
end

local function accelerationWall(side, thickness, speed, acceleration, speedMin, speedMax)
    timeline.addWall{
        side = side,
        thickness = thickness or THICKNESS,
        speed = speed,
        acceleration = acceleration,
        speedMin = speedMin,
        speedMax = speedMax
    }
end

function cAltBarrage(mSide, mStep, mThickness)
    for i = 0, getSides(), mStep do
        wall(mSide + i, mThickness)
    end
end

function cBarrageNT(mSide, mNeighbors, mThickness)
    for i = 1, getSides() - 1 - mNeighbors, 1 do
        wall(mSide + i + 1, mThickness)
    end
end

function cDoubleBarrage(mSide, mThickness)
    for i = 1, getSides() - 3 do
        wall(i + mSide + 1, mThickness)
    end
    wall(mSide)
end

function cWallGrow(mSide, mExtend, mThickness)
    for i = mExtend, mExtend, 1 do
        wall(mSide + i, mThickness)
    end
end

function cWallExM(mSide, mExtra, mMult, mThickness)
    for i = 0, mExtra do
        wall(mSide + i * mMult, mThickness)
    end
end

function cDrawWall(mSide, mMin, mMax, mThickness)
    for i = mMin, mMax do
        wall(mSide + i, mThickness)
    end
end

-- pAltBarrage: spawns a series of cAltBarrage
function pAltBarrage()
    setSides(game.randomParam(5, 6))
    local delay = patterns.getPerfectDelay(THICKNESS) * 5.6

    for i = 0, game.randomParam(2, 4) do
        cAltBarrage(i, 2)
        timeline.wait(delay)
    end

    timeline.wait(delay)
end

-- pSpiral: spawns a spiral of cWallEx
function pSpiral()
    setSides(game.randomParam(5, 6))
    local delay = patterns.getPerfectDelay(patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier")) /  game.getProperty("difficulty.delayMultiplier") * .9
    local startSide = game.randomSide()
    local j = 0
    local dir = game.randomDir()

    for _ = 0, getSides() * game.random(2) do
        patterns.wallExtra(startSide + j, 0, patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier"))
        j = j + dir
        timeline.wait(delay)
    end

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 6.5)
end

-- pMirrorSpiral: spawns a spiral of patterns.wallExtraMirrored
function pMirrorSpiral()
    setSides(game.randomParam(5, 6))
    local delay = patterns.getPerfectDelay(patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier")) / game.getProperty("difficulty.delayMultiplier") * .9
    local startSide = game.randomSide()
    local loopDir = game.randomDir()
    j = 0

    for _ = 0, game.randomParam(2, 5) do
        patterns.wallExtraMirrored(startSide + j, 0, patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier"))
        j = j + loopDir
        timeline.wait(delay)
    end

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 6.5)
end

-- pMirrorSpiralDouble: spawns a spiral of patterns.wallExtraMirrored where you need to change direction
function pMirrorSpiralDouble()
    setSides(game.randomParam(20, 28))
    local delay = patterns.getPerfectDelay(patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier")) / game.getProperty("difficulty.delayMultiplier") * .9
    local startSide = game.randomSide()
    local loopDir = game.randomDir()
    local j = 0
    local times = game.randomParam(1, 2)

    for _ = 0, times do
        patterns.wallExtraMirrored(startSide + j, 4, patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier"))
        j = j + loopDir
        timeline.wait(delay)
    end

    patterns.wallExtraMirrored(startSide + j, 4, patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier"))
    timeline.wait(delay * 0.9)

    for _ = 0, times + 1 do
        patterns.wallExtraMirrored(startSide + j, 4, patterns.getPerfectThickness(THICKNESS) * game.getProperty("difficulty.delayMultiplier"))
        j = j - loopDir
        timeline.wait(delay)
    end

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 7.5)
end

-- pBarrageSpiral: spawns a spiral of patterns.barrage
function pBarrageSpiral()
    setSides(game.randomParam(5, 6))
    local delay = patterns.getPerfectDelay(THICKNESS) * 5.6
    local startSide = game.randomSide()
    local loopDir = game.randomDir()
    local j = 0

    for i = 0, game.randomParam(0, 3) do
        patterns.barrage(startSide + j, THICKNESS)
        j = j + loopDir
        timeline.wait(delay)
        if (getSides() < 6) then timeline.wait(delay * 0.6) end
    end

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 6.1)
end

-- pInverseBarrage: spawns two barrages who force you to turn 180 degrees
function pInverseBarrage()
    setSides(game.randomParam(5, 6))
    local delay = patterns.getPerfectDelay(THICKNESS) * 9.9
    local startSide = game.randomSide()

    patterns.barrage(startSide, THICKNESS)
    timeline.wait(delay)
    if (getSides() < 6) then timeline.wait(delay * 0.8) end
    patterns.barrage(startSide + game.getHalfSides(), THICKNESS)
    timeline.wait(delay)

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 2.5)
end

-- pMirrorWallStrip: spawns rWalls close to one another on the same side
function pMirrorWallStrip()
    setSides(game.randomParam(20, 28))
    local delay = patterns.getPerfectDelay(THICKNESS) * 3.65
    local startSide = game.randomSide()

    for _ = 0, 1 do
        patterns.wallExtraMirrored(startSide, 2, THICKNESS)
        timeline.wait(delay)
    end

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.00)
end

-- pTunnel: forces you to circle around a very thick wall
function pTunnel()
    setSides(game.randomParam(5, 6))
    local myThickness = patterns.getPerfectThickness(THICKNESS)
    local delay = patterns.getPerfectDelay(myThickness) * 5
    local startSide = game.randomSide()
    local loopDir = game.randomDir()
    local times = game.randomParam(1, 3)

    for i = 0, times do
        if i < times then
            wall(startSide, myThickness + 5 * patterns.getBaseSpeed() * delay)
        end

        patterns.barrage(startSide + loopDir, myThickness)
        timeline.wait(delay)

        loopDir = loopDir * -1
    end
end


-- FLUXTUATION


-- pSwitchTunnel: half an alt barrage is covered up by a thick wall
function pSwitchTunnel(mTimes)
    return function()
        setSides(game.randomParam(3, 7))
        local myThickness = 40
        local delay = patterns.getPerfectDelay(myThickness) * 6
        local startSide = game.randomSide()
        local loopDir = startSide + 1

        for i = 0, mTimes do
            if i < mTimes then
                wall(startSide - 1, myThickness + 5 * patterns.getBaseSpeed() * delay)
                wall(startSide, myThickness + 5 * patterns.getBaseSpeed() * delay)
                wall(startSide + 1, myThickness + 5 * patterns.getBaseSpeed() * delay)
            end

            cAltBarrage(startSide + loopDir, 2)
            timeline.wait(delay)

            loopDir = loopDir + 1
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS * 4))
    end
end

-- pSideTunnel: tunnel on every side
function pSideTunnel()
    return function()
        setSides(game.randomParam(4, 7))
        local myThickness = THICKNESS
        local delay = patterns.getPerfectDelay(myThickness) * 6
        local startSide = game.randomSide()
        local loopDir = startSide + getSides() - 1

        for i = 0, getSides() - 2 do
            if i < getSides() then
                wall(startSide, myThickness + 5 * patterns.getBaseSpeed() * delay)
            end

            patterns.barrage(loopDir, 0, THICKNESS)
            timeline.wait(delay)

            loopDir = loopDir - 1
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS * 4))
    end
end

-- pSmallTunnel: little less walling in this one
function pSmallTunnel(mTimes)
    return function()
        setSides(game.randomParam(5, 7))
        local myThickness = THICKNESS
        local delay = patterns.getPerfectDelay(myThickness) * 6
        local startSide = game.randomSide()
        local loopDir = game.randomDir()

        for i = 0, mTimes do
            if i < mTimes then
                wall(startSide, myThickness + 5 * patterns.getBaseSpeed() * delay)
            end

            patterns.wallExtra(startSide, 3 * loopDir, THICKNESS)
            timeline.wait(delay)

            loopDir = loopDir * -1
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS * 4))
    end
end



-- EXTREMLINATION



function blocker (mDir)
    return function()
        setSides(6)
        if mDir == 0 then
            wall(1, THICKNESS) 
            wall(2, THICKNESS)
            wall(3, THICKNESS)
            timeline.wait(patterns.getPerfectDelay(THICKNESS * 3))
            wall(4, THICKNESS) 
            wall(5, THICKNESS)
            wall(6, THICKNESS)
        elseif mDir == 1 then
            wall(6, THICKNESS) 
            wall(5, THICKNESS)
            wall(4, THICKNESS)
            timeline.wait(patterns.getPerfectDelay(THICKNESS * 3))
            wall(3, THICKNESS) 
            wall(2, THICKNESS)
            wall(1, THICKNESS)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS * 5.6))
    end
end

function sRamb ()
    setSides(6)
    wall (1, THICKNESS)
    wall (2, THICKNESS)
    wall (4, THICKNESS)
    wall (5, THICKNESS)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (2, THICKNESS)
    wall (3, THICKNESS)
    wall (5, THICKNESS)
    wall (6, THICKNESS)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (1, THICKNESS)
    wall (2, THICKNESS)
    wall (4, THICKNESS)
    wall (5, THICKNESS)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (2, THICKNESS)
    wall (3, THICKNESS)
    wall (5, THICKNESS)
    wall (6, THICKNESS)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (1, THICKNESS)
    wall (2, THICKNESS)
    wall (4, THICKNESS)
    wall (5, THICKNESS)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (2, THICKNESS)
    wall (3, THICKNESS)
    wall (5, THICKNESS)
    wall (6, THICKNESS)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 5.6))
end

function rPrison ()
    wall (1, THICKNESS * 2)
    wall (2, THICKNESS * 2)
    wall (3, THICKNESS * 2)
    wall (4, THICKNESS * 2)
    wall (5, THICKNESS * 2)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 5.6))
    wall (1, THICKNESS * 2)
    wall (2, THICKNESS * 2)
    wall (4, THICKNESS * 2)
    wall (5, THICKNESS * 2)
    wall (6, THICKNESS * 2)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 5.6))
end

function tWall (mStep)
    if mStep == 0 then
        wall (1, THICKNESS * 5)
        wall (3, THICKNESS * 5)
        wall (4, THICKNESS)
        wall (5, THICKNESS)
        wall (6, THICKNESS)
    elseif mStep == 1 then
        wall (1, THICKNESS)
        wall (2, THICKNESS * 5)
        wall (4, THICKNESS * 5)
        wall (5, THICKNESS)
        wall (6, THICKNESS)
    elseif mStep == 2 then
        wall (1, THICKNESS)
        wall (2, THICKNESS)
        wall (3, THICKNESS * 5)
        wall (5, THICKNESS * 5)
        wall (6, THICKNESS)
    elseif mStep == 3 then
        wall (1, THICKNESS)
        wall (2, THICKNESS)
        wall (3, THICKNESS)
        wall (4, THICKNESS * 5)
        wall (6, THICKNESS * 5)
    elseif mStep == 4 then
        wall (1, THICKNESS * 5)
        wall (2, THICKNESS)
        wall (3, THICKNESS)
        wall (4, THICKNESS)
        wall (5, THICKNESS * 5)
    end
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 10.5))
end

function tObstacles ()
    setSides(6)
    wall (2, 750)
    wall (5, 750)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (3, 50)
    wall (6, 50)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (4, 50)
    wall (1, 50)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (3, 50)
    wall (6, 50)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (4, 50)
    wall (1, 50)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (3, 50)
    wall (6, 50)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 3))
    wall (4, 50)
    wall (1, 50)
    timeline.wait (patterns.getPerfectDelay(THICKNESS * 7))
end

function hWall (mStep)
    return function()
        setSides(6)
        if mStep == 1 then
            wall (1, THICKNESS)
        elseif mStep == 2 then
            wall (1, THICKNESS)
            wall (2, THICKNESS)
        elseif mStep == 3 then
            wall (1, THICKNESS)
            wall (2, THICKNESS)
            wall (3, THICKNESS)
        elseif mStep == 4 then
            wall (1, THICKNESS)
            wall (2, THICKNESS)
            wall (3, THICKNESS)
            wall (4, THICKNESS)
        elseif mStep == 5 then
            wall (1, THICKNESS)
            wall (2, THICKNESS)
            wall (3, THICKNESS)
            wall (4, THICKNESS)
            wall (5, THICKNESS)
        end
        timeline.wait (patterns.getPerfectDelay(THICKNESS * 5.6))
    end
end

-- SENSATIONALITY
-- mazeBarrage: Specified for the Nimble Navigator Level
function mazeBarrageRight(mTimes, mStep)
    oldThickness = THICKNESS
    THICKNESS = patterns.getPerfectThickness(THICKNESS)
    delay = patterns.getPerfectDelay(THICKNESS * 6.6)
    begin = game.randomSide()
    cBarrageNT(begin, 1)
    THICKNESS = THICKNESS * -6.6
    patterns.wall(begin + 2)
    THICKNESS = oldThickness
    timeline.wait(delay)
end

function mazeBarrageLeft(mTimes, mStep)
    oldThickness = THICKNESS
    THICKNESS = patterns.getPerfectThickness(THICKNESS)
    delay = patterns.getPerfectDelay(THICKNESS * 6.6)
    begin = game.randomSide()
    cBarrageNT(begin, 1)
    THICKNESS = THICKNESS * -6.6
    patterns.wall(begin - 1)
    THICKNESS = oldThickness
    timeline.wait(delay)
end

function pHelix(mSide)
    oldThickness = THICKNESS

    gap1 = mSide
    gap2 = mSide
    delay = (patterns.getPerfectDelay(THICKNESS) * 5.42) * (0.27 / (getDifficultyMult() ^ 0.24)) * (patterns.getBaseSpeed() ^ 0.16)
    patterns.barrage(mSide, 0, THICKNESS)
    timeline.wait(delay)
    for _ = 1, game.getHalfSides() - 1 do
        gap1 = gap1 - 1
        gap2 = gap2 + 1
        if gap2 == -1 then
            gap2 = gap2 + getSides()
        elseif gap2 == getSides() then
            gap2 = gap2 - getSides() 
        end
        if gap1 == -1 then
            gap1 = gap1 + getSides()
        elseif gap1 == getSides() then
            gap1 = gap1 - getSides() 
        end
        for w = 0, getSides() - 1, 1 do
            if w == gap1 or w == gap2 then
            else
                patterns.wall(w, THICKNESS)
            end
        end
        timeline.wait(delay)
    end
    patterns.barrage(mSide + game.getHalfSides(), 0, THICKNESS)
    timeline.wait(delay)
    THICKNESS = oldThickness
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.6)
end

-- LockBarrageOut: Spawns a Barrage that has rising walls that end at the opposite side of the gap
function LockBarrageOut (mSide)
    return function()
        setSides(6)
        oldThickness = THICKNESS
        gap1 = mSide
        gap2 = mSide 
        delay = patterns.getPerfectDelay(THICKNESS)
        THICKNESS = THICKNESS * game.getHalfSides()
        patterns.wall(mSide, THICKNESS)
        THICKNESS = oldThickness
        timeline.wait(delay * game.getHalfSides())
        for i = 1, game.getHalfSides() - 1 do
            gap1 = gap1 - 1
            gap2 = gap2 + 1
            if gap2 == -1 then
            gap2 = gap2 + getSides()
            elseif gap2 == getSides() then
            gap2 = gap2 - getSides() 
            end
            if gap1 == -1 then
            gap1 = gap1 + getSides()
            elseif gap1 == getSides() then
            gap1 = gap1 - getSides() 
            end
            for w = 0, getSides() - 1, 1 do
                if w == gap1 or w == gap2 then
                THICKNESS = THICKNESS * (i - game.getHalfSides())
                patterns.wall(w, THICKNESS)
                THICKNESS = oldThickness
                end
            end
        end
        THICKNESS = oldThickness
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 6)
    end
end

function LockBarrageIn (mSide)
    return function()
        setSides(game.randomParam(6, 8))
        oldThickness = THICKNESS
        delay = patterns.getPerfectDelay(THICKNESS)
        for i = 1, getSides() - 1, 1 do
            if i == game.getHalfSides() then
                THICKNESS = THICKNESS * game.getHalfSides()
                patterns.wall(mSide + 1, THICKNESS)
            else
                THICKNESS = THICKNESS * math.abs((game.getHalfSides() - i))
                patterns.wall(mSide + 1 + i, THICKNESS)
            end
            
            THICKNESS = oldThickness
        end
        timeline.wait(delay * 10)
    end
end

function surroundBarrage(mSides)
    setSides(game.randomParam(4, 8))
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 25)
    for i = 1, getSides() - 1 do
        accelerationWall(mSides + i - 1, 2000, 5, -0.105, -2, 4.57)
    end
    accelerationWall(mSides - 1, 2000, 5, -0.120, -2, 4.57)
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 10)
end

function surroundSwap(mSide)
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 20)
    for i = 1,game.getHalfSides() - 1 do
        accelerationWall(mSide + i - 1, 200, 5, -0.12, 0.5, 4.56)
    end
    accelerationWall(mSide + game.getHalfSides() - 1, 200, 5, -0.17, 0.8, 4.56)
    for i = 1 + game.getHalfSides(), getSides() - 1 do
        accelerationWall(mSide + i - 1, 200, 5, -0.12, 0.5, 4.56)
    end
    accelerationWall(mSide - 1, 40, 5, -0.12, 0.5, 4.56)
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 6)
end

function surroundSpiral(mSide, mRepetitions)
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 25)
    for i = 1, getSides() * mRepetitions do
        accelerationWall(mSide + i, 2000, 5, -0.1052, -2, 4.56)
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 2)
    end
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 10)
end

--binaryRandom(): Inspired off of Binary in Exschwasion. Generates random swapping patterns with 2 temporary walls
function binaryRandom()
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 25)
    for i = 1, game.getHalfSides() - 1 do
        accelerationWall(i - 1, 550, 5, -0.11, 0.5, 4.56)
    end
    for i = 1 + game.getHalfSides(), getSides() - 1 do
        accelerationWall(i - 1, 550, 5, -0.11, 0.5, 4.56)
    end
    for _ = 1, 5 do
        side = game.randomParam(0, 1)
        buffer = game.randomParam(10, 20)
        if side == 0 then
            accelerationWall(game.getHalfSides() - 1, 40, 5, 0.05, 0.5, 1.5)
        else
            accelerationWall(getSides() - 1, 40, 5, 0.05, 0.5, 1.5)
        end
        timeline.wait(patterns.getPerfectDelay(buffer) * 12)
    end
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 10)
end

--hookBarrage(): A full sided barrage that makes slight delays between each patterns.wall, making a sideways gap for the player to go through
function hookBarrage(mSide, mIncrement, mNeighbors, mDirection)
    incrim = mIncrement
    oldThickness = THICKNESS
    if mDirection == 0 then
        for i = 1, getSides() - mNeighbors, 1 do
            patterns.wall(mSide + i, THICKNESS)
            timeline.wait((i * incrim * 2)/getSides())
            incrim = incrim + mIncrement
            THICKNESS = oldThickness + (oldThickness * incrim * 0.5)
        end
    else
        for i = getSides() - mNeighbors, 1, -1 do
            patterns.wall(mSide + i, THICKNESS)
            timeline.wait(((getSides() - mNeighbors - i) * incrim * 2)/getSides())
            incrim = incrim + mIncrement
            THICKNESS = oldThickness + (oldThickness * incrim * 0.5)
        end
    end
    THICKNESS = oldThickness
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.6)
end

--snailBarrage(): A hook barrage that spawns an additional barrage at the end so the player has to spiral around the hook to survive
function snailBarrage(mSide, mIncrement, mGap, mDirection)
    incrim = mIncrement
    oldThickness = THICKNESS
    if mDirection == 0 then
        for i = 1, getSides(), 1 do
            patterns.wall(mSide + i, THICKNESS)
            timeline.wait((i * incrim * 2)/getSides())
            incrim = incrim + mIncrement
            THICKNESS = oldThickness + (oldThickness * incrim * 0.5)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS)/4)
        for i = mGap, getSides() - 2 - mGap, 1 do
            patterns.wall(mSide + i - mGap, THICKNESS)
        end
    else
        for i = getSides(), 1, -1 do
            patterns.wall(mSide + i, THICKNESS)
            timeline.wait(((getSides() - i) * incrim * 2)/getSides())
            incrim = incrim + mIncrement
            THICKNESS = oldThickness + (oldThickness * incrim * 0.5)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS)/getSides())
        for i = mGap, getSides() - 2 - mGap, 1  do
            patterns.wall(mSide + 4 + i, THICKNESS)
        end
    end
    THICKNESS = oldThickness
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.6)
end

-- QUARTZ

function kikiQuartzCircleTunnel()
    setSides(36)

    local function psQuartz36SidedBarrageThick(mSide)
        for x = 1, 30 do
            patterns.wall(x + mSide * 6, patterns.getPerfectThickness(THICKNESS) / 1.47)
        end
    end

    local random = game.randomSide()
    
    for x = 1, 6 do
        patterns.wall(x + random * 6, patterns.getPerfectThickness(THICKNESS) * 18.5)
    end
    psQuartz36SidedBarrageThick(3+random)
    timeline.wait(20.0)
    psQuartz36SidedBarrageThick(5+random)
    timeline.wait(20.0)
    psQuartz36SidedBarrageThick(1+random)
    timeline.wait(35.0)
    psQuartz36SidedBarrageThick(3+random)
    timeline.wait(35.0)
    psQuartz36SidedBarrageThick(1+random)
    timeline.wait(35.0)
    psQuartz36SidedBarrageThick(3+random)
    timeline.wait(32.0)
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.6)
end

-- ZICRONIUM REMIX / REMIX PACK

function osDoubledSpiral(mTimes)
    return function()
        setSides(game.randomParam(6, 7))
        local t = game.randomSide()
        local d = game.randomDir()
        local m = 0

        for _ = 1, mTimes do
            cDoubleBarrage(t + m)
            m = m + d
            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.25)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 7)
    end
end

function osDoubledLRs(mTimes)
    return function()
        setSides(game.randomParam(6, 7))
        local t = game.randomSide()
        local d = game.randomDir()
        local m = 0

        for _ = 1, mTimes do
            cDoubleBarrage(t + m)
            m = m + d
            d = d * -1
            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.25)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 7)
    end
end

function osDoubledInverted(mTimes)
    return function()
        setSides(6)
        local t = game.randomSide()
        local m = 0

        for _ = 1, mTimes do
            cDoubleBarrage(t + m)
            m = m + game.getHalfSides()
            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5.25)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 7)
    end
end

function osGrowingBarrage()
    return function()
        setSides(game.randomParam(5, 8))
        local t = getRandomSide()
        local m = 0
        local s = 2

        for _ = 1, getHalfSides()-1 do
            for i = 1, s do
                patterns.wall(i + t + m)
            end
            s = s + 2
            m = m - 1
            timeline.wait(patterns.getPerfectDelay(THICKNESS) / 1.2)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 7)
    end
end

function osZigZagSpiral(mTimes)
    return function()
        setSides(game.randomParam(6, 8))
        local t = game.randomSide()
        local m = 0
        local d = game.randomDir()

        for a = 0, mTimes do
            patterns.wall(t + m)
            patterns.wall(t + m + 1)
            patterns.wall(t + m + 2)
            if a < mTimes then
                patterns.wall(t + m + 1 + d, THICKNESS * 6)
            end
            m = m + (d * 2)
            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 3)
    end
end

-- POLYGONAL CUBICS

-- unlike the odd alt barrage, this one is 180 based, instead of every 3 sides
function babaEvenAltBarrage(mTimes)
    return function()
        setSides(6)
        local t = game.randomSide()
        local m = 0
        local d = 0
        local b = game.randomDir()

        for _ = 1, mTimes do
            for i = 1, getSides() do
                if b > 0 then
                    if d == 0 then
                        patterns.wall(t + m)
                    end
                else
                    if d > 0 then
                        patterns.wall(t + m)
                    end
                end
                m = m + 1
                d = d + 1

                if d > game.getHalfSides() - 1 then
                    d = 0
                end
            end

            m = 0
            d = 0
            b = b * -1
            timeline.wait(custWait(4))
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 8)
    end
end

-- this one is an old one! from an old 1.92 pack i made, a bit different now though
function babaJumble(mTimes, mDelay, mChance)
    local t = game.randomSide()

    for _ = 1, mTimes do
        for i = 1, mChance do
            patterns.wall(game.randomSide(), THICKNESS)
        end
        timeline.wait(custWait(mDelay * 2.75))
    end

    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 8)
end

function babaAltTrapBarrage(mTimes, mGap)
    return function()
        setSides(game.randomParam(4, 7))
        local t = game.randomSide()
        local d = -1
        local m = 0

        for _ = 1, mTimes do
            for i = 1, getSides() do
                if d < 0 then
                    if i > mGap then
                        patterns.wall(i + t)
                    end
                elseif d > 0 then
                    if i <= mGap then
                        patterns.wall(i + t)
                    end
                end
                m = m + 1
            end
            m = 0
            d = d * -1
            timeline.wait(custWait(3.6))
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 8)
    end
end

-- LAST STAND

function babaSwapCage()
    if pattern >= 200 then
        setSides(game.randomParam(5, 7))

        local _side = game.randomSide()
        patterns.wallExtra(_side + 2, getSides() - 4, custThickness(4) + THICKNESS)

        for i = 1, 3 do
            if i % 2 == 1 then
                cWallExM(_side - 1, 2, 2, THICKNESS)
            else
                cDoubleBarrage(_side, THICKNESS)
            end

            if i < 3 then
                timeline.wait(custWait(2))
            end
        end

        patterns.wallExtra(_side + 1, math.floor(getSides() / 2) - 2, custThickness(4) + THICKNESS)
        patterns.wallExtra(_side + 1 - math.floor(getSides() / 2), math.floor(getSides() / 2) - 2, custThickness(4) + THICKNESS)
        timeline.wait(custWait(2))
        patterns.wallExtra(_side + 1 + math.ceil(getSides() / 2), getSides() - 4, -THICKNESS)
        timeline.wait(custWait(2))
        patterns.wallExtra(_side + 2, getSides() - 4, custThickness(4) + THICKNESS)

        for i = 1, 3 do
            if i % 2 == 1 then
                cWallExM(_side - 1, 2, 2, THICKNESS)
            else
                cDoubleBarrage(_side, THICKNESS)
            end

            if i < 3 then
                timeline.wait(custWait(2))
            end
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 11)
    else pattern = pattern - 1
    end
end

function babaSwapTunnel(mFreq)
    return function()
        if pattern >= 200 then
            setSides(6)

            local _side, _dir = game.randomSide(), game.randomDir()
            for a = 0, mFreq do
                if a % 2 == 0 then
                    cDrawWall(_side, 2, getSides(), -THICKNESS)
                else
                    patterns.barrage(_side + (closeValue(_dir, 0, 1) * (getSides() - 4)) + 3, -THICKNESS)
                    _dir = -_dir
                end

                if a < mFreq then
                    patterns.wall(_side,     custThickness(4))
                    patterns.wall(_side + 2, custThickness(4))
                    timeline.wait(custWait(4))
                end
            end

            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 11)
        else pattern = pattern - 1
        end
    end
end

function babaSwapAdvTunnel(mFreq)
    return function()
        if pattern >= 200 then
            setSides(6)

            local _side, _dir = game.randomSide(), game.randomDir()
            for i = 0, mFreq do
                if i < mFreq then
                    cWallExM(_side + _dir, 1, 2, custThickness(4))
                end
                patterns.barrage(_side + _dir + 1, THICKNESS)
                _dir = _dir * -1
                timeline.wait(custWait(4))
            end

            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 8)
        else pattern = pattern - 1
        end
    end
end

-- EXSCHWASION
--spawns the back and forth pattern from super hexagon
function exschBackAndForth(mTimes, mDelayMult)
    return function()
        if pattern >= 20 then
            setSides(game.randomParam(6, 8))
            oldThickness = THICKNESS
            myThickness = patterns.getPerfectThickness(THICKNESS)
            delay = patterns.getPerfectDelay(myThickness) * 5
            startSide = game.randomSide()

            THICKNESS = myThickness

            for i = 0, mTimes do
                if i < mTimes then
                    patterns.wallMirrored(startSide, myThickness + 7.6 * patterns.getBaseSpeed() * delay * mDelayMult)
                    if getSides() % 2 == 1 then
                        patterns.wall(startSide+game.getHalfSides()-1, myThickness + 7.6 * patterns.getBaseSpeed() * delay * mDelayMult)
                    end
                end

                patterns.wallExtraMirrored(startSide+1, math.floor(getSides()/2), oldThickness*2)
                timeline.wait(delay*0.5*mDelayMult)
                patterns.wallExtraMirrored(startSide+2, math.floor(getSides()/2), oldThickness*2)
                timeline.wait(delay*0.5*mDelayMult)
            end

            THICKNESS = oldThickness
            timeline.wait(delay)
        else pattern = pattern - 1
        end
    end
end

function flipper(side, mHalf)
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 5)
    for i = 1, getSides() - 4 do
        accelerationWall(mHalf * 17 - 1 + side + i, THICKNESS * 0.6, 2, -0.06, 0.8, 3.5)
    end

    accelerationWall(mHalf * 17 - 5 + side, THICKNESS * 2.25, 2, -0.06, 0.8, 3.5)
    accelerationWall(mHalf * 17 + side, THICKNESS * 2.25, 2, -0.06, 0.8, 3.5)

    for i = 1, 4 do
        accelerationWall(mHalf * 17 - 5 + side + i, THICKNESS * 0.6, 2, -0.06, 0.74, 3.5)
    end
    timeline.wait(patterns.getPerfectDelay(THICKNESS) * 12)
end

-- HEXADORSIP
-- pTknsTunnelDouble: the tunnel with several thicknesses from Hyper Hexagoner (3-spin) (Made by Hexadorsip)
function hxdsTknsTunnelDouble(mTimes, mDelayMult)
    return function()
        if pattern >= 20 then
            setSides(6)
            oldThickness = THICKNESS
            myThickness = patterns.getPerfectThickness(THICKNESS)
            delay = patterns.getPerfectDelay(myThickness) * 6.5
            startSide = game.randomSide()

            THICKNESS = myThickness

            for i = 0, mTimes do
                if i < mTimes then
                    patterns.wall(startSide, myThickness + 11 * pattern.getBaseSpeed() * delay)
                    patterns.wall(startSide+1, myThickness + 11 * pattern.getBaseSpeed() * delay)
                end

                patterns.wallExtra(startSide + 5, getSides() - 6, THICKNESS*1.5)
                patterns.wallExtra(startSide + 4, getSides() - 6, THICKNESS*1)
                patterns.wallExtra(startSide + 3, getSides() - 6, oldThickness)
                timeline.wait(delay*0.65*mDelayMult)
                patterns.wallExtra(startSide - 4, getSides() - 6, THICKNESS*1.5)
                patterns.wallExtra(startSide - 3, getSides() - 6, THICKNESS*1)
                patterns.wallExtra(startSide - 2, getSides() - 6, oldThickness)
                timeline.wait(delay*0.65*mDelayMult)
            end

            timeline.wait(delay)
            THICKNESS = oldThickness
        else pattern = pattern - 1
        end
    end
end

local function synapticBarrageSpiral(times, delayMult, step)
    return function()
        setSides(5)
        local delay = patterns.getPerfectDelay(THICKNESS) * 5.6 * delayMult
        local startSide = game.randomSide()
        local loopDir = step * game.randomDir()
        local j = 0

        for i = 0, times do
            patterns.barrage(startSide + j, 0, THICKNESS)
            j = j + loopDir
            timeline.wait(delay * patterns.getPerfectDelay(THICKNESS) * 0.6)
        end
        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 6.1)
    end
end

local function synapticTunnelFastAlt(ftimes)
    return function()
        setSides(5)
        local delay = patterns.getPerfectDelay(patterns.getPerfectThickness(THICKNESS)) * 2
        local startSide = game.randomSide()
        local loopDir = game.randomDir()
        local times = ftimes()

        for i = 0, times do
            timeline.addWall{side = startSide, thickness = THICKNESS + 5 * patterns.getBaseSpeed() * delay, speed = patterns.getBaseSpeed() }

            patterns.barrage(startSide + loopDir * game.game.getHalfSides(), 0, THICKNESS)
            timeline.wait(delay)
            loopDir = loopDir * -1
        end
        timeline.wait(delay)
    end
end

function march31oTrapAround(_freq)
    return function()
        if pattern >= 20 then
            setSides(game.randomParam(4, 7))
            local _curSide = game.randomSide()

            local deskExtend = getSides() > 5 and 1 or 0
            local overHexDel = getSides() > 6 and 1 or 0
            patterns.barrage(_curSide, 0, custThickness(1))
            timeline.wait(custWait(4));
            if getSides() >= 4 then
                patterns.wall(_curSide, custThickness(1 + (10 * _freq) + (overHexDel * 4)))
            end
            cWallGrow(_curSide, getSides() > 5 and math.floor(getSides() / 4) or 0, custThickness(2))
            timeline.wait(custWait(1));
            for a = 0, _freq - 1 do
                local delExtend = getSides() > 6 and a == _freq - 1 and 4 or 0
                cWallGrow(_curSide, math.floor(getSides() / 2) - 1, custThickness(2))
                timeline.wait(custWait(4));
                if getSides() >= 6 then
                    patterns.wallExtra(_curSide + math.floor(getSides() / 2), (getSides() % 2), custThickness(4 + math.floor(((a + 1) / (_freq)) * 1)))
                end
                timeline.wait(custWait(1));
                for i = -deskExtend, (getSides() % 2) + deskExtend do
                    cWallGrow(_curSide + i + math.floor(getSides() / 2), math.floor(getSides() / 4) - 1, custThickness(2))
                end
                timeline.wait(custWait(4 + delExtend));
            end
            cWallGrow(_curSide, math.floor(getSides() / 4) + overHexDel, custThickness(2))

            timeline.wait(patterns.getPerfectDelay(THICKNESS) * 12)
        else pattern = pattern - 1
        end
    end
end

function march31oBat()
    if pattern >= 20 then
        setSides(game.randomParam(4, 7))
        local _curSide = game.randomSide()

        if getSides() > 5 then
            cWallGrow(_curSide, math.floor(getSides() / 2) - 1, custThickness(1));
            for amount001 = 0, math.floor(getSides() / 2) - 2, 1 do cWallGrow(_curSide, custThickness((4 + (amount001 * 3))), math.floor(getSides() / 2) - (amount001 + 2)); end
            timeline.wait(custWait(math.floor(getSides() / 2) + 2));
            patterns.wallExtra(_curSide + math.floor(getSides() / 2), getSides() % 2, custThickness((math.floor(getSides() / 2) + math.floor(getSides() / 2) + 1 + math.floor(getSides() / 10) + math.floor(getSides() / 4))));
            timeline.wait(custWait(2));
            for amount002 = 0, math.floor(getSides() / 2) - 3, 1 do
                patterns.wall(_curSide + math.floor(getSides() / 2) + (getSides() % 2) + 1 + amount002, custThickness((math.floor(getSides() / 2) + (math.floor(getSides() / 2) + 1) + (math.floor(getSides() / 2) - 3) - (amount002 * 3))));
                patterns.wall(_curSide + math.floor(getSides() / 2) - 1 - amount002, custThickness((math.floor(getSides() / 2) + (math.floor(getSides() / 2) + 1) + (math.floor(getSides() / 2) - 3) - (amount002 * 3))));
                if amount002 < math.floor(getSides() / 2) - 3 then
                    timeline.wait(custWait(2));
                end
            end

            timeline.wait(custWait(3));
            patterns.barrage(_curSide, 0, custThickness(3));

        else
            cWallGrow(_curSide, 1, custThickness(2));
            cWallGrow(_curSide, 0, custThickness(4));
            timeline.wait(custWait(5));
            patterns.wallExtra(_curSide + math.floor(getSides() / 2), getSides() % 2, custThickness(2));
            timeline.wait(custWait(1));
            patterns.wall(_curSide + math.floor(getSides() / 2) - 1, custThickness(2));
            patterns.wall(_curSide + math.ceil(getSides() / 2) + 1, custThickness(2));
        end

        timeline.wait(patterns.getPerfectDelay(THICKNESS) * 12)
    else pattern = pattern - 1
    end
end

local patternQueue = game.newPatternQueue{
    { weight = 1, pattern = pAltBarrage },
    { weight = 1, pattern = pSpiral },
    { weight = 1, pattern = pMirrorSpiral },
    { weight = 1, pattern = pBarrageSpiral },
    { weight = 1, pattern = pInverseBarrage },
    { weight = 1, pattern = pTunnel },
    { weight = 1, pattern = LockBarrageOut(game.randomParam(2, 4)) },
    { weight = 1, pattern = pMirrorSpiralDouble },
    { weight = 1, pattern = LockBarrageIn(game.randomParam(2, 4)) },
    { weight = 1, pattern = pMirrorWallStrip },
    { weight = 1, pattern = function()
        setSides(game.randomParam(20, 28))
        patterns.wallExtraMirrored(game.random(game.getProperty("sides.start")), 1, patterns.THICKNESS)
        timeline.wait(patterns.getPerfectDelay(patterns.THICKNESS) * 2.3)
    end },
    { weight = 1, pattern = function()
        setSides(game.randomParam(20, 28))
        patterns.wallExtra(game.random(game.getProperty("sides.start")), 7, patterns.THICKNESS)
        timeline.wait(patterns.getPerfectDelay(patterns.THICKNESS) * 2.7)
    end },
    { weight = 1, pattern = pSwitchTunnel(game.randomParam(3, 7)) },
    { weight = 1, pattern = pSideTunnel },
    { weight = 1, pattern = pSmallTunnel(game.randomParam(3, 5)) },
    { weight = 1, pattern = blocker(game.randomParam(0, 1)) },
    { weight = 1, pattern = sRamb },
    { weight = 1, pattern = rPrison },
    { weight = 1, pattern = tWall(game.randomParam(0, 4)) },
    { weight = 1, pattern = hWall(game.randomParam(1, 5)) },
    { weight = 1, pattern = kikiQuartzCircleTunnel },
    { weight = 1, pattern = osDoubledSpiral(game.randomParam(4, 6)) },
    { weight = 1, pattern = osDoubledLRs(game.randomParam(4, 6)) },
    { weight = 1, pattern = osDoubledInverted(game.randomParam(4, 6)) },
    { weight = 1, pattern = osZigZagSpiral(game.randomParam(4, 6)) },
    { weight = 1, pattern = osGrowingBarrage },
    { weight = 1, pattern = babaEvenAltBarrage(game.randomParam(4, 6)) },
    { weight = 1, pattern = babaAltTrapBarrage(game.randomParam(3, 5), 1) },
    { weight = 1, pattern = function()
        setSides(game.randomParam(5, 7))
        babaJumble(game.randomParam(3, 4), 1.4, getSides() - 3) 
    end },
    { weight = 1, pattern = function()
        setSides(game.randomParam(16, 20))
        timeline.wait(patterns.THICKNESS)
        mazeBarrageRight(game.randomParam(1, 6), 2) 
        mazeBarrageLeft(game.randomParam(1, 6), 2) 
        mazeBarrageRight(game.randomParam(1, 6), 2) 
    end },
    { weight = 1, pattern = function()
        setSides(game.randomParam(16, 20))
        timeline.wait(patterns.THICKNESS)
        mazeBarrageLeft(game.randomParam(1, 6), 2) 
        mazeBarrageRight(game.randomParam(1, 6), 2) 
        mazeBarrageLeft(game.randomParam(1, 6), 2) 
    end },
    { weight = 1, pattern = function()
        setSides(game.randomParam(5, 8))
        timeline.wait(patterns.THICKNESS)
        pHelix(game.randomParam(2, 4))
    end },
    { weight = 1, pattern = function()
        setSides(30)
        timeline.wait(patterns.THICKNESS)
        hookBarrage(game.randomParam(2, 20), 0.08, game.randomParam(0, 4), game.randomParam(0, 1))
    end },
    { weight = 1, pattern = function()
        if pattern >= 20 then
            setSides(30)
            timeline.wait(patterns.THICKNESS)
            snailBarrage(game.randomParam(2, 20), 0.08, 1, game.randomParam(0, 1))
        else pattern = pattern - 1
        end
    end },
    { weight = 1, pattern = synapticBarrageSpiral(10, 0.5, 1) },
    { weight = 1, pattern = synapticTunnelFastAlt(game.randomParam(4, 10)) }
    { weight = 1, pattern = exschBackAndForth(3, 1.5) },
    { weight = 1, pattern = hxdsTknsTunnelDouble(game.randomParam(1, 2), 1) },
    { weight = 1, pattern = march31oTrapAround(game.randomParam(0, 1)) },
    { weight = 1, pattern = march31oBat },
----------------------------------------------------------------- END OF NORMAL PATTERNS
    { weight = 1, pattern = function()
        if pattern >= 130 then
            setSides(game.randomParam(4, 8))
            timeline.wait(patterns.THICKNESS)
            surroundBarrage(game.randomParam(2, 4))
        else pattern = pattern - 1
        end
    end },
    { weight = 1, pattern = function()
        if pattern >= 130 then
            setSides(game.randomParam(4, 6))
            timeline.wait(patterns.THICKNESS)
            surroundSpiral(game.randomParam(2, 4), game.randomParam(2, 4))
        else pattern = pattern - 1
        end
    end },
----------------------------------------------------------------- END OF SURROUND PATTERNS
    { weight = 1, pattern = babaSwapCage },
    { weight = 1, pattern = babaSwapTunnel(game.randomParam(1, 3)) },
    { weight = 1, pattern = babaSwapAdvTunnel(game.randomParam(1, 3)) },
    { weight = 1, pattern = function()
        if pattern >= 200 then
            setSides(game.randomParam(4, 8))
            timeline.wait(patterns.THICKNESS)
            surroundSwap(game.randomParam(2, 4))
        else pattern = pattern - 1
        end
    end },
    { weight = 1, pattern = function()
        if pattern >= 200 then
            setSides(game.randomParam(4, 6))
            timeline.wait(patterns.THICKNESS)
            binaryRandom()
        else pattern = pattern - 1
        end
    end },
    { weight = 1, pattern = function()
        if pattern >= 200 then
            setSides(20)
            timeline.wait(patterns.THICKNESS)
            flipper()
        else pattern = pattern - 1
        end
    end }
}

local pattern = 0 -- This variable is required for the special increments to work.
local beatpulse = 10

function init()
    patternQueue:shuffle()
    pattern = 0
    beatpulse = 10
    game.loadProperties("properties.hocon")
end

function initColors()
    game.loadProperties("colors.hocon")
end

function nextPattern()
    patternQueue:addNext()
    pattern = pattern + 1
    if pattern < 10 then
        game.setProperty("difficulty.speed", 0.75)
    end
    if pattern == 10 then
        game.pushEvent(0, "push_text", "Beginning the Wall Speed\nIncrement!", 3) -- This tells the user that the Wall Speed will start incrementing
    elseif pattern == 60 then
        game.pushEvent(0, "push_text", "Beginning the Rotation Speed\nIncrement!", 3) -- This tells the player that the Rotation Speed will start incrementing
    elseif pattern == 110 then
        game.pushEvent(0, "push_text", "Stopping Wall Speed\nIncrement!", 3) -- This tells the player that the Wall Speed will stop increasing
    elseif pattern == 130 then
        game.pushEvent(0, "push_text", "Enabling Surround Patterns!", 3) -- This tells the player that surround patterns can possibly spawn
    elseif pattern == 160 then
        game.pushEvent(0, "push_text", "Stopping Rotation Speed\nIncrement!", 3) -- This tells the player that the Rotation Speed will stop incrementing
    elseif pattern == 200 then
        game.pushEvent(0, "push_text", "Enabling Swap Patterns!", 3) -- You get the idea.
    end
    if pattern > 9 and pattern < 111 then -- This increments the wall speed until speed_multiplier becomes 1.5
        local nextWSpeed = 0.75 + 0.01 * (pattern - 10)
        game.setProperty("difficulty.speed", nextWSpeed)
    end
    if pattern > 59 and pattern < 161 then -- This increments the rotation speed until it becomes 0.5
        game.setProperty("rotation.speed", 0.005 * (pattern - 60))
    end
end

function event.keyDown(keys.SPACE)
    if beatpulse == -70 then
        beatpulse = 70
    else
        beatpulse = -70
    end
    game.setProperty("beatPulse.min", beatpulse)
end