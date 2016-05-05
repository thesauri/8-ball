package com.walter.eightball

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Pixmap, Texture}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.{Interpolation, Vector2}
import com.badlogic.gdx.scenes.scene2d.{Group, Stage}
import com.badlogic.gdx.scenes.scene2d.actions.{AlphaAction, SequenceAction}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.{Align, BufferUtils, ScreenUtils}
import com.badlogic.gdx._

import scala.collection.mutable.Buffer
import scala.util.Random

/** Takes care of the game */
class GameScreen(game: Game, file: FileHandle) extends Screen with InputProcessor {

  var scale = 1f //Scale factor for rendering, updated whenever the window size changes in resize()
  lazy val camera = new OrthographicCamera()
  lazy val shapeRenderer = new ShapeRenderer()
  val gameBoard = new Board()
  var lastTouchedPoint: Option[Vector3D] = None //The place on the board where the screen was touched the last frame
  var state = GameState.load(file)

  //Stage for drawing the in-game UI (..the exit button)
  val stage = new Stage(new ScreenViewport)
  val table = new Table
  val cross = new Image(new Texture(Gdx.files.internal("textures/cross.png")))

  cross.getColor.a = 0f

  val fadeIn = new AlphaAction
  fadeIn.setAlpha(1f)
  fadeIn.setDuration(1f)

  cross.addAction(fadeIn)

  table.setBounds(0, 0, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  table.add(cross).expand.top.left.pad(Styles.GameScreenUIPadding).size(100f * Gdx.graphics.getDensity)
  stage.addActor(table)

  cross.addListener(SInputListeners.click {
    //Redraw the screen without the UI
    renderWithoutUI()

    //Take a screenshot
    val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth, Gdx.graphics.getBackBufferHeight, true)
    val pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth, Gdx.graphics.getBackBufferHeight, Pixmap.Format.RGBA8888)
    BufferUtils.copy(pixels, 0, pixmap.getPixels, pixels.length)

    //Save the game state and the screenshot, but don't save finished games
    if (state.gameState != GameStateType.Lost) {
      GameState.save(state, pixmap)
    }

    //Create a white overlay to fade-out the game
    val overlay = new Image(new Texture(Gdx.files.internal("textures/white.png")))
    overlay.setBounds(0f, 0f, Gdx.graphics.getWidth, Gdx.graphics.getHeight)

    overlay.getColor.a = 0f

    val fadeIn = new AlphaAction
    fadeIn.setAlpha(1f)
    fadeIn.setDuration(1f)
    fadeIn.setInterpolation(Interpolation.pow2In)

    val changeScreen = SAction {
      game.setScreen(new MenuScreen(game))
      true
    }

    overlay.addAction(new SequenceAction(fadeIn, changeScreen))
    stage.addActor(overlay)
    ()
  })

  //Add a ball to choose where on the ball to hit it
  val balltargetGroup = new Group
  val ball = new Image(new Texture(Gdx.files.internal("textures/ball.png")))
  ball.setSize(150f * Gdx.graphics.getDensity, 150f * Gdx.graphics.getDensity)
  val target = new Image(new Texture(Gdx.files.internal("textures/target.png")))
  target.setSize(32f * Gdx.graphics.getDensity, 32f * Gdx.graphics.getDensity)

  balltargetGroup.setSize(ball.getWidth, ball.getHeight)
  balltargetGroup.addActor(ball)
  balltargetGroup.addActor(target)

  target.setPosition((ball.getWidth - target.getWidth) / 2f, (ball.getHeight - target.getHeight) / 2f)

  table.add(balltargetGroup).expand.top.right.pad(Styles.GameScreenUIPadding)
  
  //Give input priority to the UI, otherwise pass it to the game board
  val input = new InputMultiplexer()
  input.addProcessor(stage)
  input.addProcessor(this)
  Gdx.input.setInputProcessor(input)


  override def show(): Unit = {

    resize(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
    shapeRenderer.setProjectionMatrix(camera.combined)

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

    //Render the UI
    stage.act(Gdx.graphics.getDeltaTime)
    stage.draw()
  }

  /** Redraws the screen without UI, done before taking a screenshot */
  def renderWithoutUI(): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    shapeRenderer.begin(ShapeType.Filled)
    gameBoard.render(shapeRenderer, scale)
    state.balls.foreach(ball => ball.render(shapeRenderer, scale, state.shouldBeShot(ball)))
    shapeRenderer.end()
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
