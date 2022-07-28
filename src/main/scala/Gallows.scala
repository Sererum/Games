import Program.{IO, Value, delay, pure, randomInt, unsafeRun}

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object Gallows extends App{

  trait GameState
  case object NotOneLetter extends GameState
  case object AlreadyChosen extends GameState
  case object AllRight extends GameState

  case class WordState(word: String, letterSet: List[String], guessedLetter: List[String], quantityErrors: Int)

  val endGameString: String = "You hanged"
  val wonString: String = "You guessed"
  val guessedString: String = "Hidden word is "
  val notOneLetter: String = "You didn't enter a letter"
  val alreadyChosen: String = "This letter already chosen"

  def checkWord(remainingLetters: List[String], enterLetter: String, quantityErrors: Int): (List[String], Int) = {
    if (remainingLetters.contains(enterLetter)) (remainingLetters.filter(_ != enterLetter), quantityErrors)
    else (remainingLetters, quantityErrors + 1)
  }

  def getStringGallows(
                        quantityErrors: Int, word: String, remainingLetters: List[String], state: GameState
                      ): String = {

    @tailrec
    def gallowsWord(word: String, finalString: String = ""): String =
      if (word.isEmpty) finalString else
        if (remainingLetters.contains(word.head.toString)) gallowsWord(word.tail, finalString + "_") else
          gallowsWord(word.tail, finalString + word.head)

    state match {
      case NotOneLetter => notOneLetter
      case AlreadyChosen => alreadyChosen
      case AllRight => {
        if (quantityErrors > 9) endGameString else
          if (remainingLetters.isEmpty) wonString else {
            val gallows = quantityErrors match {
              case 1 => "  |"
              case 2 => "/ |"
              case 3 => "  |\n/ |"
              case 4 => "  |\n  |\n/ |"
              case 5 => "  |\n  |\n  |\n/ |"
              case 6 => "  | - - |\n  |\n  |\n/ |"
              case 7 => "  | - - |\n  |     O\n  |\n/ |"
              case 8 => "  | - - |\n  |     O\n  |    -{}-\n/ |"
              case 9 => "  | - - |\n  |     O\n  |    -{}-\n/ |     ||"
              case _ => ""
            }
            if (gallows == "") "\n" + gallowsWord(word) + "\n" else
              "\n" + gallowsWord(word) + "\n\n" + gallows + "\n"
          }
      }
    }
  }

  def getIOLetterRequest: IO[String] =
    delay {
      println("Enter the letter:")
      readLine()
    }

  def putIOString(string: String): IO[Unit] = delay { println(string) }

  def getRandomWord(element: Int): IO[String] = {
    val words = List("about", "act", "actually", "add", "after", "again", "against", "age", "ago", "air", "all", "also",
      "always", "among", "and", "animal", "another", "answer", "appear", "are", "area", "ask", "back", "ball", "base",
      "beauty", "because", "become", "bed", "been", "before", "began", "begin", "behind", "best", "better", "better",
      "between", "big", "bird", "black", "blue", "boat", "body", "book", "both", "bottom", "box", "boy", "bring",
      "brought", "build", "built", "busy", "but", "call", "came", "can", "car", "care", "carefully", "carry", "centre",
      "certain", "change", "check", "child", "children", "city", "class", "clear", "close", "cold", "colour", "come",
      "common", "community", "complete", "contain", "could", "country", "course", "create", "cried", "cross", "cry",
      "cut", "dark", "day", "decide", "decided", "deep", "develop", "did", "different", "does", "dog", "door", "down",
      "draw", "dream", "drive", "dry", "during", "each", "early", "earth", "east", "easy", "eat", "effort", "enough",
      "every", "example", "experience", "explain", "eye", "face", "fact", "false", "family", "far", "farm", "fast",
      "father", "feel", "feet", "few", "field", "find", "fire", "first", "fish", "five", "fly", "follow", "food",
      "form", "found", "four", "friend", "from", "front", "full", "game", "gave", "get", "girl", "give", "gold", "good",
      "got", "government", "great", "green", "ground", "group", "grow", "guy", "had", "half", "hand", "happen",
      "happened", "hard", "has", "have", "hear", "heat", "heavy", "help", "her", "here", "high", "his", "hold", "home",
      "horse", "hot", "hour", "house", "hundred", "idea", "important", "inch", "include", "into", "island", "just",
      "keep", "kind", "king", "knew", "know", "known", "land", "language", "large", "last", "late", "later", "laugh",
      "lead", "learn", "leave", "left", "less", "less", "let", "letter", "life", "light", "like", "line", "list",
      "listen", "little", "live", "long", "look", "love", "low", "machine", "made", "make", "man", "many", "map",
      "mark", "may", "mean", "measure", "men", "might", "mile", "million", "mind", "minute", "miss", "money", "month",
      "moon", "more", "more", "morning", "most", "mother", "mountain", "move", "much", "music", "must", "name",
      "nation", "near", "need", "never", "new", "next", "night", "north", "note", "notice", "noun", "now", "number",
      "object", "off", "office", "often", "oil", "old", "once", "one", "only", "open", "order", "other", "our", "out",
      "over", "page", "pair", "part", "pass", "passed", "people", "perhaps", "person", "picture", "place", "plan",
      "plane", "plant", "play", "point", "power", "probably", "problem", "product", "provide", "pull", "put",
      "question", "quick", "rain", "ran", "reach", "read", "ready", "real", "receive", "record", "red", "relationship",
      "remember", "right", "river", "road", "rock", "room", "round", "rule", "run", "said", "same", "saw", "say",
      "school", "science", "sea", "season", "second", "see", "seem", "self", "sentence", "serve", "set", "several",
      "shape", "she", "ship", "short", "should", "show", "shown", "side", "simple", "since", "sing", "sit", "six",
      "size", "sleep", "slow", "small", "snow", "some", "something", "song", "soon", "sound", "south", "space",
      "special", "spell", "spring", "stand", "star", "start", "stay", "step", "stood", "stop", "story", "street",
      "strong", "study", "such", "summer", "sun", "system", "table", "take", "talk", "teach", "tell", "ten", "test",
      "than", "that", "the", "their", "them", "then", "there", "these", "they", "thing", "think", "this", "those",
      "though", "thought", "thousand", "three", "through", "time", "together", "told", "too", "took", "top", "toward",
      "town", "travel", "tree", "try", "true", "turn", "two", "under", "understand", "until", "upon", "use", "usual",
      "very", "voice", "vowel", "wait", "walk", "want", "war", "warm", "was", "watch", "water", "wave", "way", "week",
      "weight", "were", "west", "what", "wheel", "where", "which", "white", "who", "why", "will", "wind", "winter",
      "with", "without", "woman", "wonder", "wood", "word", "words", "work", "world", "would", "write", "wrong", "year",
      "yes", "you")
    pure(words(element % words.length))
  }

  def getGameState(guessedLetter: List[String], enterLetter: String): GameState =
    if (enterLetter.length != 1) NotOneLetter else
      if (guessedLetter.contains(enterLetter)) AlreadyChosen else
        AllRight

  def mainProgram: IO[Unit] = {

    def loop(wordState: WordState): IO[Unit] =
      for {
        enterLetter <- getIOLetterRequest
        (newSet, newErrors) = checkWord(wordState.letterSet, enterLetter, wordState.quantityErrors)
        gameState = getGameState(wordState.guessedLetter, enterLetter)
        stringGallows = getStringGallows(newErrors, wordState.word, newSet, gameState)
        _ <- putIOString(stringGallows)
        _ <- gameState match {
          case NotOneLetter => loop(wordState)
          case AlreadyChosen => loop(wordState)
          case AllRight => for {
            _ <- if (stringGallows == wonString) putIOString(guessedString + wordState.word) else
              if (stringGallows != endGameString)
                loop(WordState(wordState.word, newSet, enterLetter +: wordState.guessedLetter, newErrors))
              else pure(())
          } yield ()
        }
      } yield ()

    for {
      randomInteger <- randomInt
      word <- getRandomWord(randomInteger)
      letterSet = word.toSet.toList.map(_.toString)
      gallowsString = getStringGallows(0, word, letterSet, AllRight)
      _ <- putIOString(gallowsString)
      _ <- loop(WordState(word, letterSet, List.empty, 0))
    } yield ()
  }

  unsafeRun(mainProgram)
}
