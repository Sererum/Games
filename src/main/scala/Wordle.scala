import Program.{IO, getString, pure, putString, putStringWithoutLineBreak, randomInt, unsafeRun}

import scala.annotation.tailrec
import scala.language.postfixOps

object Wordle extends App{

  val maxQuantityGuesses: Int = 6

  val gameDescriptionText: String = "Guess the five-letter word.\n" +
    "Each guess must be a valid 5 letter word. After each guess, the " +
    "signs under the letters show how close your guess was to the word.\n" +
    "You have " + maxQuantityGuesses.toString + " guesses"
  val enterWordsText: String = "Enter the words:"
  val alphabet: String = "abcdefghijklmnopqrstuvwxyz"

  val guessedSymbol: String = "+"
  val inWordSymbol: String = "-"
  val notInWordSymbol: String = "x"

  val guessedSymbolText: String = guessedSymbol + "  -  letter is in right place"
  val symbolInWordText: String = inWordSymbol + "  -  letter in word, but is in wrong place"
  val notInWordText: String = notInWordSymbol + "  -  letter is not in word"

  val enterWordText: String = ": "
  val notFiveLetterWordText: String = "This word not consist 5 letters"
  val alreadyEnteredText: String = "This word already entered"
  val notContainsInWord: String = " was not found in the dictionary"

  val wordIsGuessedText: String = "You guessed"
  val wordIsNotGuessedText: String = "You don't guessed, word is "

  val words = List(
    "aback", "abase", "abate", "abbey", "abbot", "abets", "abhor", "abide", "abode", "abort", "about", "above", "abuse",
    "abuts", "abyss", "ached", "aches", "acids", "acing", "acorn", "acres", "acrid", "acted", "actor", "acute", "adage",
    "adapt", "added", "adder", "addle", "adept", "adieu", "adios", "adman", "admin", "admit", "adobe", "adopt", "adore",
    "adorn", "adult", "adzes", "aegis", "aeons", "aerie", "affix", "afire", "afoot", "after", "again", "agape", "agate",
    "agave", "agent", "aggro", "agile", "aging", "aglow", "agony", "agora", "agree", "ahead", "aided", "aider", "aides",
    "ailed", "aimed", "aioli", "aired", "aisle", "alarm", "album", "alder", "alert", "algae", "algal", "alias", "alibi",
    "alien", "align", "alike", "alive", "alkyd", "alkyl", "allay", "alley", "allot", "allow", "alloy", "aloes", "aloft",
    "aloha", "alone", "along", "aloof", "aloud", "alpha", "altar", "alter", "altos", "alums", "amass", "amaze", "amber",
    "ambit", "amble", "amend", "amide", "amine", "amino", "amiss", "amity", "among", "amour", "amped", "ample", "amply",
    "amuse", "angel", "anger", "angle", "angry", "angst", "anima", "anime", "anion", "anise", "ankle", "annex", "annoy",
    "annul", "anode", "antic", "antis", "antsy", "anvil", "aorta", "apace", "apart", "aphid", "apnea", "apple", "apply",
    "apron", "apses", "apter", "aptly", "aquas", "arbor", "ardor", "areal", "areas", "arena", "argon", "argot", "argue",
    "argus", "arias", "arise", "armed", "armor", "aroma", "arose", "array", "arrow", "arses", "arson", "artsy", "asana",
    "ascot", "ashen", "ashes", "aside", "asked", "asker", "askew", "aspen", "aspic", "assay", "asses", "asset", "aster",
    "astir", "atlas", "atman", "atoll", "atoms", "atone", "attic", "audio", "audit", "auger", "aught", "augur", "aunts",
    "aunty", "aural", "auras", "autos", "auxin", "avail", "avers", "avert", "avian", "avoid", "avows", "await", "awake",
    "award", "aware", "awash", "awful", "awoke", "axial", "axing", "axiom", "axles", "axons", "azure", "babel", "babes",
    "backs", "bacon", "badge", "badly", "bagel", "baggy", "bails", "bairn", "baits", "baize", "baked", "baker", "bakes",
    "baldy", "baled", "baler", "bales", "balks", "balky", "balls", "balms", "balmy", "balsa", "banal", "bands", "bandy",
    "banes", "bangs", "banjo", "banks", "barbs", "bards", "bared", "barer", "bares", "barge", "barks", "barmy", "barns",
    "baron", "barre", "basal", "based", "baser", "bases", "basic", "basil", "basin", "basis", "basks", "basso", "baste",
    "batch", "bated", "bathe", "baths", "batik", "baton", "batty", "bawdy", "bawls", "bayed", "bayou", "beach", "beads",
    "beady", "beaks", "beams", "beans", "beard", "bears", "beast", "beats", "beaus", "beaut", "beaux", "bebop", "becks",
    "beech", "beefs", "beefy", "beeps", "beers", "beery", "beets", "befit", "began", "beget", "begin", "begun", "beige",
    "being", "belay", "belch", "belie", "belle", "bells", "belly", "below", "belts", "bench", "bends", "bendy", "bento",
    "bents", "beret", "bergs", "berms", "berry", "berth", "beryl", "beset", "bests", "betas", "betel", "betta", "bevel",
    "bezel", "bhaji", "bible", "bicep", "biddy", "bides", "bidet", "bight", "bigot", "bijou", "biked", "biker", "bikes",
    "bilge", "bills", "billy", "bimbo", "binds", "binge", "bingo", "biota", "birch", "birds", "birth", "bison", "bitch",
    "biter", "bites", "bitty", "black", "blade", "blame", "bland", "blank", "blare", "blase", "blast", "blaze", "bleak",
    "bleat", "bleed", "bleep", "blend", "bless", "blimp", "blind", "bling", "blini", "blink", "blips", "bliss", "blitz",
    "bloat", "blobs", "block", "blocs", "blogs", "bloke", "blond", "blood", "bloom", "bloop", "blots", "blown", "blows",
    "blued", "blues", "bluff", "blunt", "blurb", "blurs", "blurt", "blush", "board", "boars", "boast", "boats", "bobby",
    "boded", "bodes", "boffo", "bogey", "boggy", "bogie", "bogus", "boils", "bolas", "boles", "bolls", "bolts", "bolus",
    "bombe", "bombs", "bonds", "boned", "boner", "bones", "bongo", "bongs", "bonks", "bonny", "bonus", "boobs", "booby",
    "booed", "books", "booms", "boons", "boors", "boost", "booth", "boots", "booty", "booze", "boozy", "borax", "bored",
    "borer", "bores", "borne", "boron", "bosom", "boson", "bossy", "bosun", "botch", "bough", "bound", "bouts", "bowed",
    "bowel", "bower", "bowls", "boxed", "boxer", "boxes", "bozos", "brace", "bract", "brads", "brags", "braid", "brain",
    "brake", "brand", "brash", "brass", "brats", "brave", "bravo", "brawl", "brawn", "brays", "braze", "bread", "break",
    "bream", "breed", "brews", "briar", "bribe", "brick", "bride", "brief", "brier", "brigs", "brims", "brine", "bring",
    "brink", "briny", "brisk", "brits", "broad", "broch", "broil", "broke", "bronc", "brood", "brook", "broom", "broth",
    "brown", "brows", "bruin", "bruit", "brunt", "brush", "brute", "bubba", "bucks", "buddy", "budge", "buffs", "buggy",
    "bugle", "build", "built", "bulbs", "bulge", "bulks", "bulky", "bulls", "bully", "bumps", "bumpy", "bunch", "bunks",
    "bunny", "bunts", "buoys", "burbs", "burgs", "burka", "burly", "burns", "burnt", "burps", "burro", "burrs", "bursa",
    "burst", "bused", "buses", "bushy", "busts", "busty", "butch", "butte", "butts", "buxom", "buyer", "bylaw", "byres",
    "bytes", "byway", "cabal", "cabby", "caber", "cabin", "cable", "cacao", "cache", "cacti", "caddy", "cadet", "cadre",
    "cafes", "caged", "cages", "cagey", "cairn", "caked", "cakes", "cake", "calif", "calla", "calls", "calms", "calve",
    "calyx", "camel", "cameo", "campo", "camps", "campy", "canal", "candy", "caned", "canes", "canny", "canoe", "canon",
    "canto", "caped", "caper", "capes", "capon", "capos", "carat", "carbs", "cards", "cared", "carer", "cares", "cargo",
    "carob", "carol", "carom", "carps", "carry", "carte", "carts", "carve", "cased", "cases", "casks", "caste", "casts",
    "catch", "cater", "catty", "caulk", "cause", "caved", "caver", "caves", "cavil", "cease", "cecal", "cecum", "cedar",
    "ceded", "cedes", "celeb", "cello", "cells", "celts", "cents", "chads", "chafe", "chaff", "chain", "chair", "chalk",
    "champ", "chana", "chant", "chaos", "chaps", "chard", "charm", "chars", "chart", "chase", "chasm", "chats", "cheap",
    "cheat", "check", "cheek", "cheep", "cheer", "chefs", "chemo", "chert", "chess", "chest", "chews", "chewy", "chick",
    "chico", "chide", "chief", "child", "chile", "chili", "chill", "chime", "chimp", "china", "chine", "chino", "chins",
    "chips", "chirp", "chits", "chive", "chock", "choir", "choke", "chomp", "chops", "chord", "chore", "chose", "chows",
    "chubs", "chuck", "chuff", "chugs", "chump", "chums", "chunk", "churn", "chute", "cider", "cigar", "cinch", "circa",
    "cisco", "cited", "cites", "civet", "civic", "civil", "clack", "clade", "claim", "clamp", "clams", "clang", "clank",
    "clans", "claps", "clash", "clasp", "class", "clave", "claws", "clays", "clean", "clear", "cleat", "clefs", "cleft",
    "clerk", "click", "cliff", "climb", "clime", "cline", "cling", "clink", "clips", "cloak", "clock", "clods", "clogs",
    "clomp", "clone", "close", "cloth", "clots", "cloud", "clout", "clove", "clown", "clubs", "cluck", "clued", "clues",
    "clump", "clung", "clunk", "coach", "coals", "coast", "coats", "cobra", "cocci", "cocks", "cocky", "cocoa", "codas",
    "codec", "coded", "coder", "codes", "codex", "codon", "coeds", "cohos", "coifs", "coils", "coins", "cokes", "colas",
    "colds", "coles", "colic", "colin", "colon", "color", "colts", "comas", "combo", "combs", "comer", "comes", "comet",
    "comfy", "comic", "comma", "compo", "comps", "comte", "conch", "condo", "coned", "cones", "conga", "congo", "conic",
    "conks", "cooed", "cooks", "cools", "coops", "coped", "copes", "copra", "copse", "coral", "cords", "cored", "corer",
    "cores", "corgi", "corks", "corky", "corms", "corns", "corny", "corps", "costs", "couch", "cough", "could", "count",
    "coupe", "coups", "court", "coven", "cover", "coves", "covet", "covey", "cowed", "cower", "cowls", "coyly", "crabs",
    "crack", "craft", "crags", "cramp", "crams", "crane", "crank", "crape", "craps", "crash", "crass", "crate", "crave",
    "crawl", "craws", "craze", "crazy", "creak", "cream", "credo", "creed", "creek", "creel", "creep", "creme", "crepe",
    "crept", "cress", "crest", "crews", "cribs", "crick", "cried", "crier", "cries", "crime", "crimp", "crisp", "croak",
    "crock", "croft", "crone", "crony", "crook", "croon", "crops", "cross", "croup", "crowd", "crown", "crows", "crude",
    "cruel", "cruet", "crumb", "cruse", "crush", "crust", "crypt", "cubed", "cubes", "cubic", "cubit", "cuffs", "culls",
    "cults", "cumin", "cupid", "cuppa", "curbs", "curds", "cured", "cures", "curia", "curio", "curls", "curly", "curry",
    "curse", "curve", "curvy", "cushy", "cusps", "cuter", "cutie", "cutup", "cycle", "cyclo", "cynic", "cysts", "czars",
    "dacha", "daddy", "daffy", "daily", "dairy", "daisy", "dales", "dames", "damns", "damps", "dance", "dandy", "dared",
    "dares", "darns", "darts", "dated", "dater", "dates", "datum", "daubs", "daunt", "davit", "dawns", "dazed", "deals",
    "dealt", "deans", "dears", "deary", "death", "debit", "debts", "debug", "debut", "decaf", "decal", "decay", "decks",
    "decor", "decoy", "decry", "deeds", "deems", "deeps", "defer", "deify", "deign", "deism", "deist", "deity", "delay",
    "delft", "delis", "dells", "delta", "delve", "demon", "demos", "demur", "denim", "dense", "dents", "depot", "depth",
    "derby", "desks", "deter", "detox", "deuce", "devil", "dewar", "dhikr", "dhows", "dials", "diary", "diced", "dices",
    "dicey", "dicky", "dicta", "diets", "digit", "diked", "dikes", "dills", "dilly", "dimer", "dimes", "dimly", "dinar",
    "dined", "diner", "dines", "dingo", "dings", "dingy", "dinky", "diode", "dippy", "direr", "dirge", "dirty", "disco",
    "discs", "dishy", "disks", "ditch", "ditsy", "ditto", "ditty", "divan", "divas", "dived", "diver", "dives", "divot",
    "divvy", "dizzy", "docks", "dodge", "dodgy", "dodos", "doers", "doffs", "doges", "doggy", "dogma", "doing", "doled",
    "doles", "dolls", "dolly", "dolor", "dolts", "domed", "domes", "dongs", "donna", "donor", "donut", "dooms", "doors",
    "doped", "dopes", "dopey", "dorks", "dorky", "dorms", "dosed", "doses", "doted", "dotes", "dotty", "doubt", "dough",
    "douse", "doves", "dowdy", "dowel", "dower", "downs", "downy", "dowry", "dowse", "doyen", "dozed", "dozen", "dozer",
    "dozes", "drabs", "draft", "drags", "drain", "drake", "drama", "drams", "drank", "drape", "drawl", "drawn", "draws",
    "drays", "dread", "dream", "dregs", "dress", "dried", "drier", "dries", "drift", "drill", "drink", "drips", "drive",
    "droid", "droll", "drone", "drool", "droop", "drops", "dross", "drove", "drown", "drugs", "druid", "drums", "drunk",
    "drupe", "dryad", "dryer", "dryly", "duals", "ducal", "ducat", "duchy", "ducks", "ducky", "ducts", "dudes", "duels",
    "duets", "duffs", "dukes", "dulls", "dully", "dumbo", "dummy", "dumps", "dumpy", "dunce", "dunes", "dunks", "duped",
    "dupes", "durum", "dusky", "dusts", "dusty", "dutch", "duvet", "dwarf", "dweeb", "dwell", "dwelt", "dyers", "dying",
    "dykes", "eager", "eagle", "eared", "earls", "early", "earns", "earth", "eased", "easel", "eases", "eaten", "eater",
    "eaves", "ebbed", "ebony", "ebook", "echos", "eclat", "edema", "edged", "edger", "edges", "edict", "edify", "edits",
    "eerie", "egged", "egret", "eider", "eight", "eject", "eland", "elbow", "elder", "elect", "elegy", "elide", "elite",
    "elope", "elude", "elven", "elves", "email", "embed", "ember", "emcee", "emery", "emirs", "emits", "emote", "empty",
    "enact", "ended", "endow", "enema", "enemy", "enjoy", "ennui", "enrol", "ensue", "enter", "entry", "envoy", "epics",
    "epoch", "epoxy", "equal", "equip", "erase", "erect", "ergot", "erode", "erred", "error", "erupt", "essay", "ether",
    "ethic", "ethos", "ethyl", "etude", "euros", "evade", "evens", "event", "every", "evict", "evils", "evoke", "ewers",
    "exact", "exalt", "exams", "excel", "execs", "exert", "exile", "exist", "exits", "expat", "expel", "expos", "extol",
    "extra", "exude", "exult", "exurb", "eying", "eyrie", "fable", "faced", "faces", "facet", "facts", "faded", "fader",
    "fades", "faery", "fails", "faint", "fairs", "fairy", "faith", "faked", "faker", "fakes", "fakir", "falls", "famed",
    "fancy", "fangs", "fanny", "farce", "fared", "fares", "farms", "farts", "fasts", "fatal", "fated", "fates", "fatso",
    "fatty", "fatwa", "fault", "fauna", "fauns", "faves", "favor", "fawns", "faxed", "faxes", "fazed", "fazes", "fears",
    "feast", "feats", "fecal", "feces", "feeds", "feels", "feign", "feint", "fella", "fells", "felon", "felts", "femme",
    "femur", "fence", "fends", "feral", "ferns", "ferny", "ferry", "fests", "fetal", "fetch", "feted", "fetes", "fetid",
    "fetus", "feuds", "fever", "fewer", "fiats", "fiber", "fibre", "fiche", "ficus", "fiefs", "field", "fiend", "fiery",
    "fifes", "fifth", "fifty", "fight", "filch", "filed", "filer", "files", "filet", "fills", "filly", "films", "filmy",
    "filth", "final", "finch", "finds", "fined", "finer", "fines", "finis", "finks", "fired", "fires", "firms", "first",
    "fishy", "fists", "fitly", "fiver", "fives", "fixed", "fixer", "fixes", "fizzy", "fjord", "flack", "flags", "flail",
    "flair", "flake", "flaky", "flame", "flank", "flans", "flaps", "flare", "flash", "flask", "flats", "flaws", "flays",
    "fleas", "fleck", "flees", "fleet", "flesh", "flick", "flier", "flies", "fling", "float", "flood", "floor", "flour",
    "flown", "flows", "fluid", "flyer", "focal", "focus", "folks", "fonts", "foods", "force", "forms", "forth", "forty",
    "forum", "found", "frame", "fraud", "fresh", "fried", "fries", "front", "frost", "fruit", "fuels", "fully", "funds",
    "funny", "gains", "games", "gamma", "gases", "gates", "gauge", "gears", "genes", "genre", "ghost", "giant", "gifts",
    "girls", "given", "gives", "gland", "glass", "globe", "glory", "gloss", "glove", "glued", "goals", "goats", "going",
    "goods", "grace", "grade", "grain", "grams", "grand", "grant", "grape", "graph", "grasp", "grass", "grave", "great",
    "greek", "green", "greet", "grief", "grill", "grind", "grips", "gross", "group", "grove", "grown", "grows", "guard",
    "guess", "guest", "guide", "guild", "guilt", "habit", "hairs", "halls", "hands", "handy", "hangs", "happy", "harsh",
    "hated", "hates", "haven", "hawks", "heads", "heard", "heart", "heavy", "hedge", "heels", "hello", "helps", "hence",
    "herbs", "highs", "hills", "hints", "hired", "hobby", "holds", "holes", "holly", "homes", "honey", "honor", "hooks",
    "hoped", "hopes", "horns", "horse", "hosts", "hotel", "hours", "house", "hover", "human", "humor", "hurts", "icons",
    "ideal", "ideas", "idiot", "image", "imply", "inbox", "incur", "index", "indie", "inner", "input", "intro", "issue",
    "items", "jeans", "jelly", "jewel", "joins", "joint", "jokes", "judge", "juice", "juicy", "jumps", "keeps", "kicks",
    "kills", "kinda", "kinds", "kings", "knees", "knife", "knock", "knots", "known", "knows", "label", "labor", "lacks",
    "lakes", "lamps", "lands", "lanes", "large", "laser", "lasts", "later", "laugh", "layer", "leads", "leaks", "learn",
    "lease", "least", "leave", "legal", "lemon", "level", "lever", "light", "liked", "likes", "limbs", "limit", "lined",
    "linen", "liner", "lines", "links", "lions", "lists", "lived", "liver", "lives", "loads", "loans", "lobby", "local",
    "locks", "lodge", "logic", "logos", "looks", "loops", "loose", "lords", "loses", "loved", "lover", "loves", "lower",
    "loyal", "lucky", "lunar", "lunch", "lungs", "lying", "macro", "magic", "major", "maker", "makes", "males", "maple",
    "march", "marks", "marry", "masks", "match", "mates", "maths", "matte", "maybe", "mayor", "meals", "means", "meant",
    "meats", "medal", "media", "meets", "melee", "menus", "mercy", "merge", "merit", "merry", "messy", "metal", "meter",
    "metro", "micro", "midst", "might", "miles", "minds", "mines", "minor", "minus", "mixed", "mixer", "mixes", "model",
    "modem", "modes", "moist", "money", "month", "moral", "motor", "mount", "mouse", "mouth", "moved", "moves", "movie",
    "music", "myths", "nails", "naked", "named", "names", "nasal", "nasty", "naval", "needs", "nerve", "never", "newer",
    "newly", "nexus", "nicer", "niche", "night", "ninja", "ninth", "noble", "nodes", "noise", "noisy", "norms", "north",
    "notch", "noted", "notes", "novel", "nurse", "nylon", "oasis", "occur", "ocean", "offer", "often", "older", "olive",
    "omega", "onion", "onset", "opens", "opera", "opted", "optic", "orbit", "order", "organ", "other", "ought", "ounce",
    "outer", "owned", "owner", "oxide", "packs", "pages", "pains", "paint", "pairs", "panel", "panic", "pants", "paper",
    "parks", "parts", "party", "pasta", "paste", "patch", "paths", "patio", "pause", "peace", "peach", "peaks", "pearl",
    "pedal", "peers", "penis", "penny", "perks", "pests", "petty", "phase", "phone", "photo", "piano", "picks", "piece",
    "piles", "pills", "pilot", "pinch", "pipes", "pitch", "pixel", "pizza", "place", "plain", "plane", "plans", "plant",
    "plate", "plays", "plaza", "plots", "plugs", "poems", "point", "poker", "polar", "poles", "polls", "pools", "porch",
    "pores", "ports", "posed", "poses", "posts", "pouch", "pound", "power", "press", "price", "pride", "prime", "print",
    "prior", "prize", "probe", "promo", "prone", "proof", "props", "proud", "prove", "proxy", "psalm", "pulls", "pulse",
    "pumps", "punch", "pupil", "puppy", "purse", "queen", "query", "quest", "queue", "quick", "quiet", "quilt", "quite",
    "quote", "races", "racks", "radar", "radio", "rails", "rainy", "raise", "rally", "ranch", "range", "ranks", "rapid",
    "rated", "rates", "ratio", "razor", "reach", "react", "reads", "ready", "realm", "rebel", "refer", "reign", "relax",
    "relay", "renal", "renew", "reply", "reset", "resin", "retro", "rider", "rides", "ridge", "rifle", "right", "rigid",
    "rings", "rinse", "risen", "rises", "risks", "risky", "rival", "river", "roads", "robot", "rocks", "rocky", "rogue",
    "roles", "rolls", "roman", "rooms", "roots", "ropes", "roses", "rough", "round", "route", "royal", "rugby", "ruins",
    "ruled", "ruler", "rules", "rural", "sadly", "safer", "salad", "sales", "salon", "sandy", "satin", "sauce", "saved",
    "saves", "scale", "scalp", "scans", "scare", "scarf", "scary", "scene", "scent", "scoop", "scope", "score", "scout",
    "scrap", "screw", "seals", "seams", "seats", "seeds", "seeks", "seems", "sells", "sends", "sense", "serum", "serve",
    "setup", "seven", "sewer", "shade", "shaft", "shake", "shall", "shame", "shape", "share", "shark", "sharp", "sheep",
    "sheer", "sheet", "shelf", "shell", "shift", "shine", "shiny", "ships", "shirt", "shock", "shoes", "shook", "shoot",
    "shops", "shore", "short", "shots", "shown", "shows", "sides", "siege", "sight", "sigma", "signs", "silly", "since",
    "sites", "sixth", "sized", "sizes", "skies", "skill", "skins", "skirt", "skull", "slate", "slave", "sleek", "sleep",
    "slept", "slice", "slide", "slope", "slots", "small", "smart", "smell", "smile", "smoke", "snack", "snake", "sneak",
    "socks", "soils", "solar", "solid", "solve", "songs", "sonic", "sorry", "sorts", "souls", "sound", "south", "space",
    "spare", "spark", "speak", "specs", "speed", "spell", "spend", "spent", "sperm", "spice", "spicy", "spike", "spine",
    "spite", "split", "spoke", "spoon", "sport", "spots", "spray", "spurs", "squad", "stack", "staff", "stage", "stain",
    "stake", "stamp", "stand", "stark", "stars", "start", "state", "stats", "stays", "steak", "steal", "steam", "steel",
    "steep", "steer", "stems", "steps", "stick", "stiff", "still", "stock", "stole", "stone", "stood", "stool", "stops",
    "store", "storm", "story", "stove", "strap", "straw", "strip", "stuck", "study", "stuff", "style", "sucks", "sugar",
    "suite", "suits", "sunny", "super", "surge", "sushi", "swear", "sweat", "sweet", "swept", "swift", "swing", "swiss",
    "sword", "syrup", "table", "taken", "takes", "tales", "talks", "tanks", "tapes", "tasks", "taste", "tasty", "taxes",
    "teach", "teams", "tears", "teens", "teeth", "tells", "tempo", "tends", "tenth", "tents", "terms", "tests", "texts",
    "thank", "theft", "their", "theme", "there", "these", "thick", "thief", "thigh", "thing", "think", "third", "those",
    "three", "threw", "throw", "thumb", "tiger", "tight", "tiles", "timer", "times", "tired", "tires", "title", "toast",
    "today", "token", "tones", "tools", "tooth", "topic", "torch", "total", "touch", "tough", "tours", "towel", "tower",
    "towns", "toxic", "trace", "track", "tract", "trade", "trail", "train", "trait", "trans", "traps", "trash", "treat",
    "trees", "trend", "trial", "tribe", "trick", "tried", "tries", "trips", "trout", "truck", "truly", "trump", "trunk",
    "trust", "truth", "tubes", "tumor", "tuned", "tunes", "turbo", "turns", "tutor", "tweet", "twice", "twins", "twist",
    "types", "tyres", "ultra", "uncle", "under", "union", "unite", "units", "unity", "until", "upper", "upset", "urban",
    "urged", "urine", "usage", "users", "using", "usual", "vague", "valid", "value", "valve", "vapor", "vault", "vegan",
    "veins", "vents", "venue", "verse", "video", "views", "villa", "vinyl", "viral", "virus", "visas", "visit", "vital",
    "vivid", "vocal", "vodka", "voice", "volts", "voted", "voter", "votes", "wages", "wagon", "waist", "walks", "walls",
    "wants", "warns", "waste", "watch", "water", "watts", "waves", "wears", "weeds", "weeks", "weigh", "weird", "wells",
    "welsh", "whale", "wheat", "wheel", "where", "which", "while", "white", "whole", "whose", "wider", "widow", "width",
    "winds", "wines", "wings", "wiped", "wired", "wires", "witch", "wives", "woman", "women", "woods", "words", "works",
    "world", "worms", "worry", "worse", "worst", "worth", "would", "wound", "wrath", "wrist", "write", "wrong", "wrote",
    "yacht", "yards", "years", "yeast", "yield", "young", "yours", "youth", "yummy",
  )

  def randomMainWord(index: Int): String = words(index % words.length)

  def getStateStringWord(allWord: String, enterWord: String): String = {
    def loop(word: String, enterWord: String, finalString: String = ""): String =
      if (word.isEmpty) finalString else
        if (word.head == enterWord.head) loop(word.tail, enterWord.tail, finalString + guessedSymbol) else
          if (allWord.contains(enterWord.head)) loop(word.tail, enterWord.tail, finalString + inWordSymbol) else
              loop(word.tail, enterWord.tail, finalString + notInWordSymbol)

    loop(allWord, enterWord)
  }

  def getUpdateAlphabet(word: String, enterWord: String, alphabet: String): String = {
    def loop(enterWord: String, alphabet: String): String =
      if (enterWord.isEmpty) alphabet else
        if (! word.contains(enterWord.head)) loop(enterWord.tail, alphabet.filter(ch => ch != enterWord.head)) else
          loop(enterWord.tail, alphabet)

    loop(enterWord, alphabet)
  }


  def mainProgram: IO[Unit] = {

    def loop(word: String, alphabet: String, guesses: List[String] = List.empty): IO[Unit] = {
      for {
        _ <- putStringWithoutLineBreak(enterWordText)
        enterWord <- getString
        isAlreadyEntered = guesses.contains(enterWord)
        isNotFiveLetters = enterWord.length != 5
        isNotContainsInWords = ! words.contains(enterWord)
        newAlphabet = getUpdateAlphabet(word, enterWord, alphabet)
        _ <- if (isNotFiveLetters) putString(notFiveLetterWordText) else
              if (isAlreadyEntered) putString(alreadyEnteredText) else
                if (isNotContainsInWords) putString(enterWord + notContainsInWord) else
                  putString("  " + getStateStringWord(word, enterWord) + " - " + newAlphabet)
        _ <- if (isNotFiveLetters || isAlreadyEntered || isNotContainsInWords) loop(word, alphabet, guesses) else
              if (enterWord == word) putString(wordIsGuessedText) else
                if (guesses.length > maxQuantityGuesses - 2) putString(wordIsNotGuessedText + word) else
                    loop(word, newAlphabet, enterWord +: guesses)
      } yield ()
    }

    for {
      _ <- putString(gameDescriptionText)
      _ <- putString(guessedSymbolText + "\n" + symbolInWordText + "\n" + notInWordText)
      _ <- putString(enterWordsText)
      random <- randomInt
      word = randomMainWord(random)
      _ <- loop(word, alphabet)
    } yield ()
  }

  unsafeRun(mainProgram)
}
