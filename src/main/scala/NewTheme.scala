import Program.{IO, pure, unsafeRun}

object NewTheme extends App{

  trait Monoid[Type] {
    def add(valueOne: Type, valueTwo: Type): Type
    def empty: Type
  }

  trait Show[Type]{
    def show(value: Type): String
  }

  trait Monad[F[_]] {
    def flatMap[OldType, NewType](fOldType: F[OldType], func: OldType => F[NewType]): F[NewType]
    def pure[Type](value: Type): F[Type]
    def map[OldType, NewType](
                               fOldType: F[OldType],
                               func: OldType => NewType
                             ): F[NewType] = flatMap(fOldType, value => pure(func(value)))
  }

  trait Console[F[_]] {
    def getStrLn: F[String]
    def putStrLn(string: String): F[Unit]
  }

  trait Random[F[_]] {
    def randInt(start: Int, end: Int): F[Int]
  }

  given intShow: Show[Int] = (value: Int) => "Integer: " + value.toString

  given strShow: Show[String] = (value: String) => "String: " + value
  
  given pairMonoid[TypeOne, TypeTwo](
                                      using proofOne: Monoid[TypeOne], proofTwo: Monoid[TypeTwo]
                                    ): Monoid[(TypeOne, TypeTwo)] = new Monoid[(TypeOne, TypeTwo)]{

    override def add(valueOne: (TypeOne, TypeTwo), valueTwo: (TypeOne, TypeTwo)): (TypeOne, TypeTwo) = (
      proofOne.add(valueOne._1, valueTwo._1), proofTwo.add(valueOne._2, valueTwo._2)
    )
    override def empty: (TypeOne, TypeTwo) = (proofOne.empty, proofTwo.empty)  
  }

  given Monad[IO] with {
    override def flatMap[OldType, NewType](fOldType: IO[OldType], func: OldType => IO[NewType]): IO[NewType] = {
      fOldType.flatMap(func)
    }
    override def pure[Type](value: Type): IO[Type] = Program.pure(value)
  }

  given Console[IO] with {
    override def putStrLn(string: String): IO[Unit] = Program.putString(string)
    override def getStrLn: IO[String] = Program.getString
  }

  given Random[IO] with {
    override def randInt(start: Int, end: Int): IO[Int] = Program.randInt(start, end)
  }

  extension [F[_], Type](fType: F[Type])(using monad: Monad[F]) {
    def flatMap[NewType](func: Type => F[NewType]): F[NewType] = monad.flatMap(fType, func)

    def map[NewType](func: Type => NewType): F[NewType] = monad.map(fType, func)
  }

  val intAdd: Monoid[Int] = new Monoid[Int] {
    override def add(numberOne: Int, numberTwo: Int): Int = numberOne + numberTwo
    override def empty = 0
  }
  val intMul: Monoid[Int] = new Monoid[Int] {
    override def add(numberOne: Int, numberTwo: Int): Int = numberOne * numberTwo
    override def empty: Int = 1
  }
  val strCon: Monoid[String] = new Monoid[String] {
    override def add(valueOne: String, valueTwo: String): String = valueOne + valueTwo
    override def empty: String = ""
  }

  def putStrLn[F[_]](str: String)(using console: Console[F]): F[Unit] = console.putStrLn(str)

  def getStrLn[F[_]](using console: Console[F]): F[String] = console.getStrLn

  def randInt[F[_]](start:Int, end: Int)(using random: Random[F]): F[Int] =
    random.randInt(start, end)

  def addAll[Type] (list: List[Type])(proof: Monoid[Type]): Type = {
    list.fold(proof.empty)((base, value) => proof.add(base, value))
  }

  def showAll[Type] (list: List[Type])(using proof: Show[Type]): String = {
    list.foldLeft("[ ")((base, value) => base + proof.show(value) + " ") + "]"
  }

  def function[F[_]: Console: Monad: Random](value: Int): F[String] =
    for{
      _ <- putStrLn(value.toString)
      rand <- randInt(10, 15)
      _ <- putStrLn(rand.toString)
      enter <- getStrLn
    } yield enter

}