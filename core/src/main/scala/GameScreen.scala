package com.walter.eightball

import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}

import scala.collection.mutable.Buffer
import scala.util.Random

/** Takes care of the game */
class GameScreen extends Screen with InputProcessor {

  var scale = 1f //Scale factor for rendering, updated whenever the window size changes in resize()
  lazy val camera = new OrthographicCamera()
  lazy val shapeRenderer = new ShapeRenderer()
  val gameBoard = new Board()
  var lastTouchedPoint: Option[Vector3D] = None //The place on the board where the screen was touched the last frame
  lazy val state = new GameState()

  override def show(): Unit = {
    Gdx.input.setInputProcessor(this)

    resize(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
    shapeRenderer.setProjectionMatrix(camera.combined)

    //Cue ball
    state.balls += new Ball(0.25f, 0.635f, 0f, 0)

    state.balls += new Ball(1.69f, 0.635f, 0f, 1)

    state.balls += new Ball(1.69f + 2.02f * state.balls(0).radius, 0.635f - 1.02f * state.balls(0).radius, 0f, 15)
    state.balls += new Ball(1.69f + 2.02f * state.balls(0).radius, 0.635f + 1.02f * state.balls(0).radius, 0f, 2)

    state.balls += new Ball(1.69f + 4.04f * state.balls(0).radius, 0.635f - 2.02f * state.balls(0).radius, 0f, 14)
    state.balls += new Ball(1.69f + 4.04f * state.balls(0).radius, 0.635f, 0f, 8)
    state.balls += new Ball(1.69f + 4.04f * state.balls(0).radius, 0.635f + 2.02f * state.balls(0).radius, 0f, 13)

    state.balls += new Ball(1.69f + 6.06f * state.balls(0).radius, 0.635f - 3.03f * state.balls(0).radius, 0f, 4)
    state.balls += new Ball(1.69f + 6.06f * state.balls(0).radius, 0.635f - 1.02f * state.balls(0).radius, 0f, 12)
    state.balls += new Ball(1.69f + 6.06f * state.balls(0).radius, 0.635f + 1.02f * state.balls(0).radius, 0f, 5)
    state.balls += new Ball(1.69f + 6.06f * state.balls(0).radius, 0.635f + 3.03f * state.balls(0).radius, 0f, 11)

    state.balls += new Ball(1.69f + 8.08f * state.balls(0).radius, 0.635f - 4.04f * state.balls(0).radius, 0f, 6)
    state.balls += new Ball(1.69f + 8.08f * state.balls(0).radius, 0.635f - 2.02f * state.balls(0).radius, 0f, 10)
    state.balls += new Ball(1.69f + 8.08f * state.balls(0).radius, 0.635f, 0f, 7)
    state.balls += new Ball(1.69f + 8.08f * state.balls(0).radius, 0.635f + 2.02f * state.balls(0).radius, 0f, 9)
    state.balls += new Ball(1.69f + 8.08f * state.balls(0).radius, 0.635f + 4.04f * state.balls(0).radius, 0f, 3)

    //Distribute gameState.balls a bit randomly
    val random = new Random()
    state.balls foreach { ball => {
      //ball += Vector3D(0.000005f * random.nextInt(100), 0.00005f * random.nextInt(100), 0f)
    }}

    for (i <- 0 until state.balls.size) {
      for (n <- i + 1 until state.balls.size) {
        if ((state.balls(i) - state.balls(n)).len < state.balls(0).radius + state.balls(1).radius) println("Overlapping in the beginning")
      }
    }
  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    /** Returns true if the ball should be shot by the current player */
    def isActive(ball: Ball): Boolean = state.hasSolids match {
      case Some(1) => ball.number <= 8
      case Some(2) => ball.number >= 8
      case None => true
    }

    //Draw the game board
    state.gameState match {
      case GameState.Aiming => {
        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        state.balls.foreach(ball => ball.render(shapeRenderer, scale, isActive(ball)))
        state.cueStick.render(shapeRenderer, scale)
        shapeRenderer.end()
      }

      case GameState.Shooting => {
        ???
      }

      case GameState.Rolling => {
        PhysicsHandler.update(state, delta)

        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        state.balls.foreach(ball => ball.render(shapeRenderer, scale, isActive(ball)))
        shapeRenderer.end()

        if (PhysicsHandler.areStill(state.balls)) {
          state.gameState = GameState.Aiming
        }
      }
    }
  }

  override def hide(): Unit = ()

  /** Updates camera parameters and the scale factor after a window resize */
  override def resize(width: Int, height: Int): Unit = {
    scale = width / 3f
    camera.viewportWidth = width
    camera.viewportHeight = height
    camera.position.set(scale * Board.Width / 2f, scale * Board.Height / 2f, 0f)
    camera.update()
  }

  override def dispose(): Unit = ()

  override def pause(): Unit = ()

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

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = state.gameState match {

    case GameState.Aiming if (pointer == 0) => {
      val curPointOnBoard = screenCoordToGame(Vector3D(screenX, screenY, 0f))
      state.cueStick.pointAt = state.balls(0)

      //Don't rotate if the shift key is pressed, or a second touch is applied on the screen
      if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isTouched(1)) {
        state.cueStick.rotationDegrees = (state.balls(0) - curPointOnBoard).angle2d
      }

      state.cueStick.distance = (state.balls(0) - curPointOnBoard).len

      //If it was touched the last frame and the cue stick is very close to the ball, shoot
      if (lastTouchedPoint.isDefined && state.cueStick.distance < 1.5f * state.balls(0).radius) {
        val newVelocityLength = (lastTouchedPoint.get - curPointOnBoard).len / Gdx.graphics.getDeltaTime
        state.balls(0).velocity = Vector3D(newVelocityLength, state.cueStick.rotationDegrees)
        state.gameState = GameState.Rolling
      }

      lastTouchedPoint = Some(curPointOnBoard)

      true
    }

    case _ => false
  }
}
