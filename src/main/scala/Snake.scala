import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle

import scala.concurrent.Future
import scala.util.Random

object Snake extends JFXApp3 {

  val initialSnake: List[(Double, Double)] = List(
    (250, 200),
    (225, 200),
    (200, 200)
  )

  import scala.concurrent.ExecutionContext.Implicits.global

  def gameLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(1000 / 25 * 2)
    }.flatMap(_ => Future(gameLoop(update)))

  case class State(snake: List[(Double, Double)], food: (Double, Double)) {
    def newState(dir: Int): State = {
      val (x, y) = snake.head
      val (newx, newy) = dir match {
        case 1 => (x, y - 25)
        case 2 => (x, y + 25)
        case 3 => (x - 25, y)
        case 4 => (x + 25, y)
        case _ => (x, y)
      }

      val newSnake: List[(Double, Double)] =
        if (newx < 0 || newx >= 600 || newy < 0 || newy >= 600 || snake.tail.contains((newx, newy)))
          initialSnake
        else if (food == (newx, newy))
          food :: snake
        else
          (newx, newy) :: snake.init

      val newFood =
        if (food == (newx, newy))
          randomFood()
        else
          food

      State(newSnake, newFood)
    }

    def rectangles: List[Rectangle] = square(food._1, food._2, Red) :: snake.map {
      case (x, y) => square(x, y, Green)
    }
  }

  def randomFood(): (Double, Double) =
    (Random.nextInt(24) * 25, Random.nextInt(24) * 25)

  def square(xr: Double, yr: Double, color: Color) = new Rectangle {
    x = xr
    y = yr
    width = 25
    height = 25
    fill = color
  }

  override def start(): Unit = {
    val state = ObjectProperty(State(initialSnake, randomFood()))
    val frame = IntegerProperty(0)
    val direction = IntegerProperty(4) // right

    frame.onChange {
      state.update(state.value.newState(direction.value))
    }

    stage = new JFXApp3.PrimaryStage {
      width = 600
      height = 600
      scene = new Scene {
        fill = White
        content = state.value.rectangles
        onKeyPressed = key => key.getText match {
          case "w" => direction.value = 1
          case "s" => direction.value = 2
          case "a" => direction.value = 3
          case "d" => direction.value = 4
        }

        state.onChange(Platform.runLater {
          content = state.value.rectangles
        })
      }
    }

    gameLoop(() => frame.update(frame.value + 1))
  }

}