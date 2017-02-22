print "Running Lua bootstrap code"

local gameutil = {}
local patternQueue = {}

function patternQueue:shuffle()
    for i = #self, 2, -1 do
        local r = game.random(i - 1) + 1
        self[i], self[r] = self[r], self[i]
    end
end

function patternQueue:addNext()
    if self.at >= #self then
        self.at = 0
    end
    self.at = self.at + 1
    self[self.at]()
end

function gameutil.newPatternQueue(queue)
    local unpacked = {at = 0}
    local n = 1
    for _, v in ipairs(queue) do
        for i = 1, v.weight do
            unpacked[n] = v.pattern
            n = n + 1
        end
    end

    if #unpacked < 1 then
        error("Pattern queue must contain at least 1 element with weight > 0!")
    end

    return setmetatable(unpacked, {__index = patternQueue})
end

local function flipTable(input)
    local out = {}
    for k, v in pairs(input) do
        out[v] = k
    end
    return out
end

-- Merges add onto orig
local function mergeTables(orig, add)
    for k, v in pairs(add) do
        orig[k] = v
    end
end

function prepareEnv(name, mapZipFile)
    local function mapCall(f)
        return function(...)
            return f(mapZipFile, ...)
        end
    end

    local env = {
        assert = assert,
        error = error,
        getmetatable = function(t)
            if type(t) == "string" then
                return nil
            end
            return getmetatable(t)
        end,
        ipairs = ipairs,
        load = load,
        next = next,
        pairs = pairs,
        pcall = pcall,
        print = function(v)
            print("Map." .. tostring(name) .. ": " .. tostring(v))
        end,
        rawequal = rawequal,
        rawget = rawget,
        rawlen = rawlen,
        rawset = rawset,
        select = select,
        setmetatable = setmetatable,
        tonumber = tonumber,
        tostring = tostring,
        type = type,
        _VERSION = _VERSION,
        xpcall = xpcall,
        coroutine = nil,
        string = {
            byte = string.byte,
            char = string.char,
            dump = string.dump,
            find = string.find,
            format = string.format,
            gmatch = string.gmatch,
            gsub = string.gsub,
            len = string.len,
            lower = string.lower,
            match = string.match,
            rep = string.rep,
            reverse = string.reverse,
            sub = string.sub,
            upper = string.upper
        },
        table = {
            concat = table.concat,
            insert = table.insert,
            pack = table.pack,
            remove = table.remove,
            sort = table.sort,
            unpack = table.unpack
        },
        math = {
            abs = math.abs,
            acos = math.acos,
            asin = math.asin,
            atan = math.atan,
            atan2 = math.atan2,
            ceil = math.ceil,
            cos = math.cos,
            cosh = math.cosh,
            deg = math.deg,
            exp = math.exp,
            floor = math.floor,
            fmod = math.fmod,
            frexp = math.frexp,
            huge = math.huge,
            ldexp = math.ldexp,
            log = math.log,
            max = math.max,
            min = math.min,
            modf = math.modf,
            pi = math.pi,
            pow = math.pow,
            rad = math.rad,
            random = math.random, --TODO: Check compat, seed
            --randomseed = math.randomseed,
            sin = math.sin,
            sinh = math.sinh,
            sqrt = math.sqrt,
            tan = math.tan,
            tanh = math.tanh
        },
        bit32 = {
            arshift = bit32.arshift,
            band = bit32.band,
            bnot = bit32.bnot,
            bor = bit32.bor,
            btest = bit32.btest,
            bxor = bit32.bxor,
            extract = bit32.extract,
            replace = bit32.replace,
            lrotate = bit32.lrotate,
            lshift = bit32.lshift,
            rrotate = bit32.rrotate,
            rshift = bit32.rshift
        },
        os = {
            clock = os.clock,
            date = os.date,
            difftime = os.difftime,
            time = os.time
        },
        debug = debug and {
            getinfo = function(...)
                local result = debug.getinfo(...)
                if result then
                    return {
                        source = result.source,
                        short_src = result.short_src,
                        linedefined = result.linedefined,
                        lastlinedefined = result.lastlinedefined,
                        what = result.what,
                        currentline = result.currentline,
                        nups = result.nups,
                        nparams = result.nparams,
                        isvararg = result.isvararg,
                        name = result.name,
                        namewhat = result.namewhat,
                        istailcall = result.istailcall
                    }
                end
            end,
            traceback = debug.traceback
        },
        buttons = { --LibGDX codes
            LEFT = 0,
            RIGHT = 1,
            MIDDLE = 2,
            BACK = 3,
            FORWARD = 4
        },
        keys = {
            ANY_KEY = -1,
            NUM_0 = 7,
            NUM_1 = 8,
            NUM_2 = 9,
            NUM_3 = 10,
            NUM_4 = 11,
            NUM_5 = 12,
            NUM_6 = 13,
            NUM_7 = 14,
            NUM_8 = 15,
            NUM_9 = 16,
            A = 29,
            ALT_LEFT = 57,
            ALT_RIGHT = 58,
            APOSTROPHE = 75,
            AT = 77,
            B = 30,
            BACK = 4,
            BACKSLASH = 73,
            C = 31,
            CALL = 5,
            CAMERA = 27,
            CLEAR = 28,
            COMMA = 55,
            D = 32,
            DEL = 67,
            BACKSPACE = 67,
            FORWARD_DEL = 112,
            --DPAD_CENTER = 23,
            --DPAD_DOWN = 20,
            --DPAD_LEFT = 21,
            --DPAD_RIGHT = 22,
            DPAD_UP = 19,
            CENTER = 23,
            DOWN = 20,
            LEFT = 21,
            RIGHT = 22,
            UP = 19,
            E = 33,
            ENDCALL = 6,
            ENTER = 66,
            ENVELOPE = 65,
            EQUALS = 70,
            EXPLORER = 64,
            F = 34,
            FOCUS = 80,
            G = 35,
            GRAVE = 68,
            H = 36,
            HEADSETHOOK = 79,
            HOME = 3,
            I = 37,
            J = 38,
            K = 39,
            L = 40,
            LEFT_BRACKET = 71,
            M = 41,
            MEDIA_FAST_FORWARD = 90,
            MEDIA_NEXT = 87,
            MEDIA_PLAY_PAUSE = 85,
            MEDIA_PREVIOUS = 88,
            MEDIA_REWIND = 89,
            MEDIA_STOP = 86,
            MENU = 82,
            MINUS = 69,
            MUTE = 91,
            N = 42,
            NOTIFICATION = 83,
            NUM = 78,
            O = 43,
            P = 44,
            PERIOD = 56,
            PLUS = 81,
            POUND = 18,
            POWER = 26,
            Q = 45,
            R = 46,
            RIGHT_BRACKET = 72,
            S = 47,
            SEARCH = 84,
            SEMICOLON = 74,
            SHIFT_LEFT = 59,
            SHIFT_RIGHT = 60,
            SLASH = 76,
            SOFT_LEFT = 1,
            SOFT_RIGHT = 2,
            SPACE = 62,
            STAR = 17,
            SYM = 63,
            T = 48,
            TAB = 61,
            U = 49,
            UNKNOWN = 0,
            V = 50,
            VOLUME_DOWN = 25,
            VOLUME_UP = 24,
            W = 51,
            X = 52,
            Y = 53,
            Z = 54,
            META_ALT_LEFT_ON = 16,
            META_ALT_ON = 2,
            META_ALT_RIGHT_ON = 32,
            META_SHIFT_LEFT_ON = 64,
            META_SHIFT_ON = 1,
            META_SHIFT_RIGHT_ON = 128,
            META_SYM_ON = 4,
            CONTROL_LEFT = 129,
            CONTROL_RIGHT = 130,
            ESCAPE = 131,
            END = 132,
            INSERT = 133,
            PAGE_UP = 92,
            PAGE_DOWN = 93,
            PICTSYMBOLS = 94,
            SWITCH_CHARSET = 95,
            BUTTON_CIRCLE = 255,
            BUTTON_A = 96,
            BUTTON_B = 97,
            BUTTON_C = 98,
            BUTTON_X = 99,
            BUTTON_Y = 100,
            BUTTON_Z = 101,
            BUTTON_L1 = 102,
            BUTTON_R1 = 103,
            BUTTON_L2 = 104,
            BUTTON_R2 = 105,
            BUTTON_THUMBL = 106,
            BUTTON_THUMBR = 107,
            BUTTON_START = 108,
            BUTTON_SELECT = 109,
            BUTTON_MODE = 110,
            NUMPAD_0 = 144,
            NUMPAD_1 = 145,
            NUMPAD_2 = 146,
            NUMPAD_3 = 147,
            NUMPAD_4 = 148,
            NUMPAD_5 = 149,
            NUMPAD_6 = 150,
            NUMPAD_7 = 151,
            NUMPAD_8 = 152,
            NUMPAD_9 = 153,
            COLON = 243,
            F1 = 244,
            F2 = 245,
            F3 = 246,
            F4 = 247,
            F5 = 248,
            F6 = 249,
            F7 = 250,
            F8 = 251,
            F9 = 252,
            F10 = 253,
            F11 = 254,
            F12 = 255
        },
        game = {
            newPatternQueue = gameutil.newPatternQueue,

            randomParam = game.randomParam,
            random = game.random,
            randomSide = game.randomSide,
            randomDir = game.randomDir,
            getHalfSides = game.getHalfSides,

            loadProperties = mapCall(game.loadProperties),
            setProperty = game.setProperty,
            setAll = game.setAll,
            getProperty = game.getProperty,
            pushEvent = game.pushEvent
        },
        standardPattern = {
            alternatingBarrage = standardPattern.alternatingBarrage,
            mirrorSpiral = standardPattern.mirrorSpiral,
            doubleMirrorSpiral = standardPattern.doubleMirrorSpiral,
            barrageSpiral = standardPattern.barrageSpiral,
            inverseBarrage = standardPattern.inverseBarrage,
            tunnel = standardPattern.tunnel,
            mirroredWallStrip = standardPattern.mirroredWallStrip,
            vortex = standardPattern.vortex,
            fixedDelayBarrageSpiral = standardPattern.fixedDelayBarrageSpiral,
            randomBarrage = standardPattern.randomBarrage
        },
        patterns = {
            THICKNESS = patterns.THICKNESS,
            getSideDistance = patterns.getSideDistance,
            getPerfectThickness = patterns.getPerfectThickness,
            getPerfectDelay = patterns.getPerfectDelay,
            getBaseSpeed = patterns.getBaseSpeed,

            barrage = patterns.barrage,
            wall = patterns.wall,
            wallExtra = patterns.wallExtra,
            wallOpposite = patterns.wallOpposite,
            wallOppositeExtra = patterns.wallOppositeExtra,
            wallMirrored = patterns.wallMirrored,
            wallExtraMirrored = patterns.wallExtraMirrored
        },
        timeline = {
            wait = timeline.wait,
            addWall = timeline.addWall
        }
    }

    mergeTables(env.buttons, flipTable(env.buttons))
    mergeTables(env.keys, flipTable(env.keys))

    local wenv = {event = {}}
    env._G = wenv

    return setmetatable(wenv, {__index = env}), wenv
end

print "Bootstrap code OK"
