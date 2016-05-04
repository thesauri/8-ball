package com.walter.eightball

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.{AlphaAction, DelayAction, SequenceAction}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, ScrollPane, Table}
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MenuScreen extends Screen {

  lazy val stage = new Stage(new ScreenViewport)
  lazy val screenshotTable = new Table
  lazy val scrollPane = new ScrollPane(screenshotTable)
  lazy val fillTable = new Table

  def show(): Unit = {
    Gdx.input.setInputProcessor(stage)

    val screenshots = GameState.savedScreenshots

    for (i <- 0 until screenshots.size) {
      val image = new Image(screenshots(i)._1)
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
