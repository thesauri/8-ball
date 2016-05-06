package com.walter.eightball.ui

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx._
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Pixmap, Texture}
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.{AlphaAction, SequenceAction}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.badlogic.gdx.scenes.scene2d.{Group, InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.{BufferUtils, ScreenUtils}
import com.walter.eightball.libgdx.{SAction, SInputListeners}
import com.walter.eightball.math.Vector3D
import com.walter.eightball.objects.Board
import com.walter.eightball.state.{GameState, GameStateType}
import com.walter.eightball.state.PhysicsHandler

/** Screen that renders and handles input to the game
  *
  * @param game The game instance
  * @param file Optional FileHandle to a saved game state
  */
class GameScreen(game: Game, file: Option[FileHandle]) extends Screen with InputProcessor {

  var scale = 1f //Scale factor for rendering, updated whenever the window size changes in resize()
  lazy val camera = new OrthographicCamera()
  lazy val shapeRenderer = new ShapeRenderer()
  val gameBoard = Board
  var touchedDownPoint: Option[Vector3D] = None //The place on the board where the cue stick was when a second finger was touched
  var lastDistanceFromBall: Option[Float] = None //How far from the ball the cue stick was the last frame

  //Optionally load a saved game state
  var state = file match {
    case Some(file) => GameState.load(file)
    case None => {
      val state = new GameState
      state.placeBallsAtDefaultPositions()
      state
    }
  }

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

  //Add the exit button (cross) to the top left corner
  table.add(cross).expand.top.left.pad(Styles.GameScreenUIPadding).size(Styles.GameScreenButtonSize)

  //Add the table containing the exit button to the stage
  stage.addActor(table)

  //Save and return to the MainScreen when the exit button is pressed
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

    //Change the screen once the overlay has faded in
    val changeScreen = SAction {
      game.setScreen(new MenuScreen(game))
      true
    }

    overlay.addAction(new SequenceAction(fadeIn, changeScreen))
    stage.addActor(overlay)
  })

  //Add a ball to choose where on the ball to hit it
  val balltargetGroup = new Group
  val ball = new Image(new Texture(Gdx.files.internal("textures/ball.png")))
  ball.setSize(Styles.GameScreenButtonSize, Styles.GameScreenButtonSize)
  val target = new Image(new Texture(Gdx.files.internal("textures/target.png")))
  target.setSize(Styles.GameScreenTargetSize, Styles.GameScreenTargetSize)

  //Group these together to put the target on the ball
  balltargetGroup.setSize(ball.getWidth, ball.getHeight)
  balltargetGroup.addActor(ball)
  balltargetGroup.addActor(target)

  target.setPosition((ball.getWidth - target.getWidth) / 2f, (ball.getHeight - target.getHeight) / 2f)

  //Move target when clicked
  ball.addListener(SInputListeners.touchDown { (event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) => {
    target.setPosition(x - target.getWidth / 2f, y - target.getHeight / 2f)
    true
  }})

  //Hide the ball and targets initially and fade them in
  ball.getColor.a = 0f
  target.getColor.a = 0f

  val fadeInBall = new AlphaAction
  fadeInBall.setAlpha(1f)
  fadeInBall.setDuration(1f)

  //For some reason the same action can't be reused for multiple actors..
  val fadeInTarget = new AlphaAction
  fadeInTarget.setAlpha(1f)
  fadeInTarget.setDuration(1f)

  ball.addAction(fadeInBall)
  target.addAction(fadeInTarget)

  //Put the ball and target in the top right corner
  table.add(balltargetGroup).expand.top.right.pad(Styles.GameScreenUIPadding)

  //Give input priority to the UI, otherwise pass it to the game board
  val input = new InputMultiplexer()
  input.addProcessor(this)
  input.addProcessor(stage)
  Gdx.input.setInputProcessor(input)

  /** Called when the screen is opened */
  override def show(): Unit = {

    //Update the camera parameters according to the current window size
    resize(Gdx.graphics.getWidth, Gdx.graphics.getHeight)

  }

  /** Updates the game state and renders the board
    *
    * @param delta Time since the last render (in seconds)
    */
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
    shapeRenderer.setProjectionMatrix(camera.combined)
  }

  override def dispose(): Unit = ()

  override def pause(): Unit = ()

  /** Translates screen coordinates to game board coordinates */
  def screenCoordToGame(coords: Vector3D): Vector3D = (1f / scale) * ((camera.unproject(coords)): Vector3D)

  override def resume(): Unit = ()

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def keyTyped(character: Char): Boolean = false

  override def keyDown(keycode: Int): Boolean = keycode match {

    //Saves where the mouse pointer was when the shift key was pressed (used for locking the rotation)
    case Keys.SHIFT_LEFT => {
      touchedDownPoint = Some(screenCoordToGame(Vector3D(Gdx.input.getX, Gdx.input.getY, 0f)))
      true
    }

    case _ => false
  }

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = state.gameState match {

    case GameStateType.Aiming => {

      //Saves where the cue stick was when a second finger is touched (to lock the rotation)
      if (pointer == 1) {
        touchedDownPoint = Some(screenCoordToGame(Vector3D(Gdx.input.getX, Gdx.input.getY, 0f)))
        true
      } else {
        false
      }
    }

    case GameStateType.Lost => {
      state.placeBallsAtDefaultPositions()
      true
    }

    case _ => false

  }

  override def keyUp(keycode: Int): Boolean = keycode match {

    //Stop locking the rotation
    case Keys.SHIFT_LEFT => {
      touchedDownPoint = None
      true
    }

    case _ => false

  }

  override def scrolled(amount: Int): Boolean = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = pointer match {

    //Stop locking the rotation if the second finger is removed
    case 1 => {
      touchedDownPoint = None
      true
    }

    case _ => false

  }

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = state.gameState match {

    case GameStateType.Aiming if (pointer == 0) => {
      state.cueBall foreach { cueBall => {

        //Determine where on the board the screen was touched
        val curPointOnBoard = screenCoordToGame(Vector3D(screenX, screenY, 0f))

        //Point the cue stick at the ball
        state.cueStick.pointAt = cueBall

        //Don't rotate if the shift key is pressed, or a second touch is applied on the screen
        if (touchedDownPoint.isEmpty) {
          state.cueStick.rotationDegrees = (cueBall - curPointOnBoard).angle2d
        }

        //Update the distance from the cue ball to the tip of the cue stick
        state.cueStick.distance = if (touchedDownPoint.isDefined) {
          (cueBall - touchedDownPoint.get).len - (curPointOnBoard - touchedDownPoint.get).len
        } else {
          (cueBall - curPointOnBoard).len
        }

        //If we've passed the with the cue stick, then shoot!
        if (lastDistanceFromBall.isDefined && state.cueStick.distance <= 0.05f) {
          val cuestickVelocityLength = (lastDistanceFromBall.get - state.cueStick.distance) / Gdx.graphics.getDeltaTime
          val cuestickVelocity = Vector3D(cuestickVelocityLength, state.cueStick.rotationDegrees)

          /* Scaling factor to convert points from the target ball on the screen to points according
             to the radius of the cue ball */
          val c = cueBall.radius * 1f / (ball.getWidth / 2f)

          //Get ∆x,y,z from the middle of the target ball
          val ballPosition = c * Vector3D(target.getX - (ball.getWidth - target.getWidth) / 2f,
            target.getY - (ball.getHeight - target.getWidth) / 2f)

          PhysicsHandler.shoot(cueBall, cuestickVelocity, ballPosition)
          state.gameState = GameStateType.Rolling
        }

        lastDistanceFromBall = Some(state.cueStick.distance)
      }}

      true
    }

    case _ => false
  }
}
