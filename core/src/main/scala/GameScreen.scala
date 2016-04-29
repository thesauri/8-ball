package com.walter.eightball

import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}
import com.walter.eightball.{Ball, Board, PhysicsHandler, Vector3D}

import scala.collection.mutable.Buffer
import scala.util.Random

/** Takes care of the game */
class GameScreen extends Screen with InputProcessor {

  lazy val scale = Gdx.graphics.getWidth / 3f //Scale factor for rendering
  lazy val camera = new OrthographicCamera(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)
  lazy val shapeRenderer = new ShapeRenderer()
  lazy val gameBoard = new Board()
  lazy val balls = Buffer[Ball]()
  lazy val cueStick = new CueStick(Vector3D(0f, 0f, 0f), 0f, 0f)
  var gameState = GameState.Aiming
  var lastTouchedPoint: Option[Vector3D] = None //The place on the board where the screen was touched the last frame

  override def hide(): Unit = ()

  override def resize(width: Int, height: Int): Unit = ()

  override def dispose(): Unit = ()

  override def pause(): Unit = ()

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    gameState match {
      case GameState.Aiming => {
        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        balls.foreach(_.render(shapeRenderer, scale))
        cueStick.render(shapeRenderer, scale)
        shapeRenderer.end()
      }

      case GameState.Shooting => {
        ???
      }

      case GameState.Rolling => {
        PhysicsHandler.update(balls, delta)

        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        balls.foreach(_.render(shapeRenderer, scale))
        shapeRenderer.end()

        if (PhysicsHandler.areStill(balls)) {
          gameState = GameState.Aiming
        }
      }
    }


  }

  override def show(): Unit = {
    Gdx.input.setInputProcessor(this)

    camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    //Cue ball
    balls += new Ball(0.25f, 0.635f, 0f, 1)
    balls(0).angularVelocity = Vector3D(0f, 0f, 0f)

    balls += new Ball(1.69f, 0.635f, 0f, 1)

    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 1)

    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f, 0f, 1)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 1)

    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 3.03f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 3.03f * balls(0).radius, 0f, 1)

    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 4.04f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 4.04f * balls(0).radius, 0f, 1)

    //Distribute balls a bit randomly
    val random = new Random()
    balls foreach { ball => {
      ball += Vector3D(0.00005f * random.nextInt(100), 0.00005f * random.nextInt(100), 0f)
    }}

    for (i <- 0 until balls.size) {
      for (n <- i + 1 until balls.size) {
        if ((balls(i) - balls(n)).len < balls(0).radius + balls(1).radius) println("Overlapping in the beginning")
      }
    }
  }

  /** Translates screen coordinates to game board coordinates */
  def screenCoordToGame(coords: Vector3D): Vector3D = (1f / scale) * ((camera.unproject(coords)): Vector3D)

  override def resume(): Unit = ()

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def keyTyped(character: Char): Boolean = false

  override def keyDown(keycode: Int): Boolean = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def keyUp(keycode: Int): Boolean = false

  override def scrolled(amount: Int): Boolean = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = gameState match {

    case GameState.Aiming => {
      val curPointOnBoard = screenCoordToGame(Vector3D(screenX, screenY, 0f))
      cueStick.pointAt = balls(0)

      if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
        cueStick.rotationDegrees = (balls(0) - curPointOnBoard).angle2d
      }

      cueStick.distance = (balls(0) - curPointOnBoard).len

      //If it was touched the last frame and the cue stick is very close to the ball, shoot
      if (lastTouchedPoint.isDefined && cueStick.distance < 1.5f * balls(0).radius) {
        val newVelocityLength = (lastTouchedPoint.get - curPointOnBoard).len / Gdx.graphics.getDeltaTime
        balls(0).velocity = Vector3D(newVelocityLength, cueStick.rotationDegrees)
        gameState = GameState.Rolling
      }

      lastTouchedPoint = Some(curPointOnBoard)

      true
    }

    case _ => false
  }
}
