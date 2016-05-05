package com.walter.eightball

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.math.{Interpolation, Vector2}
import com.badlogic.gdx.{Game, Gdx, Screen}
import com.badlogic.gdx.scenes.scene2d.{Actor, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, ScrollPane, Table}
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MenuScreen(game: Game) extends Screen {

  lazy val stage = new Stage(new ScreenViewport)
  lazy val screenshotTable = new Table
  lazy val scrollPane = new ScrollPane(screenshotTable)
  lazy val fillTable = new Table

  /** Screenshot associated with a file storing the game state of the screen shot */
  class Screenshot(val texture: Texture, val file: Option[FileHandle]) extends Image(texture)

  def show(): Unit = {
    Gdx.input.setInputProcessor(stage)

    val screenshots = GameState.savedScreenshots

    for (i <- 0 until screenshots.size) {
      val image = new Screenshot(screenshots(i)._1, screenshots(i)._2)
      val newWidth = stage.getWidth / 3f
      val newHeight = (image.getHeight / image.getWidth) * newWidth
      val padding = 0.05f * stage.getWidth

      //Animate the entrance of the screenshots
      image.getColor.a = 0f

      val fadeIn = new AlphaAction()

      fadeIn.setAlpha(1f)
      fadeIn.setDuration(1f)

      val delay = new DelayAction()
      delay.setDuration(0.1f * i)

      val seq = new SequenceAction(delay, fadeIn)

      image.addAction(seq)


      image.addListener(SInputListeners.click((event: InputEvent) => {
        val curActor = event.getListenerActor
        val otherScreenshotActors = screenshotTable.getChildren.toArray.filter( _ != curActor )

        curActor match {

          case screenshot: Screenshot => {
            openGame(screenshot, otherScreenshotActors, screenshot.file)
            true
          }
          case _ => false

        }
      }))

      screenshotTable.add(image).width(newWidth).height(newHeight).space(padding)

      if (i % 2 == 1) {
        screenshotTable.row()
      }
    }

    fillTable.setFillParent(true)
    fillTable.add(scrollPane)

    scrollPane.setWidth(100f)
    scrollPane.invalidate()

    stage.addActor(fillTable)
  }

  /** Zooms in on the desired screenshot, fades out the other screenshots and finally
    * loads the desired file
    *
    * @param desiredScreenshot The screenshot to zoom in on
    * @param screenshots The other screenshots to fade out
    * @param file FileHandle to the game state
    */
  def openGame(desiredScreenshot: Screenshot, screenshots: Seq[Actor], file: Option[FileHandle]): Unit = {

    val location = desiredScreenshot.localToStageCoordinates(new Vector2(0f, 0f))

    //Put a screenshot on top of the desired one, but which is not in the grid (to be able to move and scale it properly)
    val screenshotToScale = new Image(desiredScreenshot.texture)
    screenshotToScale.setSize(desiredScreenshot.getWidth, desiredScreenshot.getHeight)
    screenshotToScale.setPosition(location.x, location.y)

    //screenshotToScale.setOrigin(screenshotToScale.getWidth / 2f, screenshotToScale.getHeight / 2f)

    stage.addActor(screenshotToScale)

    desiredScreenshot.remove()

    screenshots foreach { screenshot => {
      val fadeOut = new AlphaAction
      fadeOut.setAlpha(0f)
      fadeOut.setDuration(0.5f)

      val hide = new VisibleAction

      screenshot.addAction(new SequenceAction(fadeOut, hide))
    }}

    val delay = new DelayAction()
    delay.setDuration(0.5f)

    val move = new MoveToAction
    move.setPosition(0f, 0f)
    move.setInterpolation(Interpolation.linear)
    move.setDuration(1f)

    val scale = new ScaleToAction
    scale.setScale(Gdx.graphics.getHeight / desiredScreenshot.getHeight)
    move.setInterpolation(Interpolation.linear)
    scale.setDuration(1f)

    val changeScreen = SAction {
      game.setScreen(new GameScreen(game, file))
      true
    }

    screenshotToScale.addAction(new SequenceAction(delay, new ParallelAction(move, scale), changeScreen))
  }

  def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta)
    stage.draw()
  }

  def resize(width: Int, height: Int): Unit = {
    stage.getViewport.update(width, height, true)
  }

  def dispose(): Unit = {
    stage.dispose()
  }

  def hide(): Unit = ()

  def pause(): Unit = ()

  def resume(): Unit = ()
}
