import scala.io.StdIn.readLine
import scala.util.Random

object Program extends App{
  
  private val random = new Random()

  trait IO[Type]{
    def flatMap[NewType](func: Type => IO[NewType]): IO[NewType] = FlatMap(this, func)
    def map[NewType](func: Type => NewType): IO[NewType] = flatMap(effect => pure(func(effect)))
  }
  case class Value[Type](value: Type) extends IO[Type]
  case class Effect[Type](run: () => Type) extends IO[Type]

  case class FlatMap[OldType, NewType](effect: IO[OldType], func: OldType => IO[NewType]) extends IO[NewType]

  def pure[Type](value: Type): IO[Type] = Value(value)
  def delay[Type](effect: => Type): IO[Type] = Effect(() => effect)
  
  def randomInt: IO[Int] = delay {math.abs(random.nextInt())}

  def randInt(start: Int, end: Int) = delay {random.nextInt(end - start) + start}

  def unsafeRun[Type](io: IO[Type]): Type =
    io match {
      case Value(value) => value
      case Effect(run) => run()
      case FlatMap(effect, func) => unsafeRun(func(unsafeRun(effect)))
    }

  def getString: IO[String] = delay {readLine()}
  def putString(string: String): IO[Unit] = delay {println(string)}
  def putStringWithoutLineBreak(string: String): IO[Unit] = delay {print(string)}
}
