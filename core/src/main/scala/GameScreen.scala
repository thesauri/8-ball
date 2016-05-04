package com.walter.eightball

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Pixmap}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.{Align, BufferUtils, ScreenUtils}
import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}

import scala.collection.mutable.Buffer
import scala.util.Random

/** Takes care of the game */
class GameScreen(file: FileHandle) extends Screen with InputProcessor {

  var scale = 1f //Scale factor for rendering, updated whenever the window size changes in resize()
  lazy val camera = new OrthographicCamera()
  lazy val shapeRenderer = new ShapeRenderer()
  val gameBoard = new Board()
  var lastTouchedPoint: Option[Vector3D] = None //The place on the board where the screen was touched the last frame
  var state = GameState.load(file)

  override def show(): Unit = {
    Gdx.input.setInputProcessor(this)

    resize(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
    shapeRenderer.setProjectionMatrix(camera.combined)

    //state.placeBallsAtDefaultPositions()

    for (i <- 0 until state.balls.size) {
      for (n <- i + 1 until state.balls.size) {
        if ((state.balls(i) - state.balls(n)).len < state.balls(0).radius + state.balls(1).radius) println("Overlapping in the beginning")
      }
    }
  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    //Draw the game board
    state.gameState match {
      case GameStateType.Aiming => {
        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        state.balls.foreach(ball => ball.render(shapeRenderer, scale, state.shouldBeShot(ball)))
        state.cueStick.render(shapeRenderer, scale)
        shapeRenderer.end()
      }

      case GameStateType.Rolling => {
        PhysicsHandler.update(state, delta)

        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        state.balls.foreach(ball => ball.render(shapeRenderer, scale, state.shouldBeShot(ball)))
        shapeRenderer.end()

        if (PhysicsHandler.areStill(state.balls)) {
          state.nextRound()
        }
      }

      case GameStateType.Lost => {
        Gdx.graphics.getGL20.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.begin(ShapeType.Filled)

        gameBoard.render(shapeRenderer, scale)
        state.balls.foreach(ball => ball.render(shapeRenderer, scale, state.shouldBeShot(ball)))

        //Draw a dark overlay over the screen when the game is over
        shapeRenderer.setColor(Styles.GameOverOverlay)
        shapeRenderer.rect(camera.position.x - camera.viewportWidth / 2f,
          camera.position.y - camera.viewportHeight / 2f,
          camera.viewportWidth,
          camera.viewportHeight)

        shapeRenderer.end()
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

  override def keyDown(keycode: Int): Boolean = keycode match {

    case Keys.S => {
      val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth, Gdx.graphics.getBackBufferHeight, true)
      val pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth, Gdx.graphics.getBackBufferHeight, Pixmap.Format.RGBA8888)
      BufferUtils.copy(pixels, 0, pixmap.getPixels, pixels.length)

      GameState.save(state, pixmap)

      true
    }

    case Keys.L => {
      val files = GameState.savedGames.sortBy( _.name )
      state = GameState.load(files.last)
      true
    }

    case _ => false
  }

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = state.gameState match {

    case GameStateType.Lost => {
      state.placeBallsAtDefaultPositions()
      true
    }

    case _ => false

  }

  override def keyUp(keycode: Int): Boolean = false

  override def scrolled(amount: Int): Boolean = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = state.gameState match {

    case GameStateType.Aiming if (pointer == 0) => {
      state.cueBall foreach { cueBall => {
        val curPointOnBoard = screenCoordToGame(Vector3D(screenX, screenY, 0f))
        state.cueStick.pointAt = cueBall

        //Don't rotate if the shift key is pressed, or a second touch is applied on the screen
        if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isTouched(1)) {
          state.cueStick.rotationDegrees = (cueBall - curPointOnBoard).angle2d
        }

        state.cueStick.distance = (cueBall - curPointOnBoard).len

        //If it was touched the last frame and the cue stick is very close to the ball, shoot
        if (lastTouchedPoint.isDefined && state.cueStick.distance < 1.5f * cueBall.radius) {
          val newVelocityLength = (lastTouchedPoint.get - curPointOnBoard).len / Gdx.graphics.getDeltaTime
          cueBall.velocity = Vector3D(newVelocityLength, state.cueStick.rotationDegrees)
          state.gameState = GameStateType.Rolling
        }

        lastTouchedPoint = Some(curPointOnBoard)
      }}



      true
    }

    case _ => false
  }
}
