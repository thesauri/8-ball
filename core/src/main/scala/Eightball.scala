package com.walter.eightball

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics.GL20
import scala.collection.mutable.Buffer

class Eightball extends Game {

    lazy val camera = new OrthographicCamera(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)
    lazy val shapeRenderer = new ShapeRenderer()
    lazy val gameBoard = new Board()
    lazy val balls = Buffer[Ball]()

    override def create(): Unit = {
      camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
      camera.update()
      shapeRenderer.setProjectionMatrix(camera.combined)
    }

    override def render(): Unit = {
      Gdx.gl.glClearColor(1, 1, 1, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

      shapeRenderer.begin(ShapeType.Filled)
      gameBoard.render(shapeRenderer, Gdx.graphics.getWidth / 3f) //Divided by the approx. board width in meters
      shapeRenderer.end()
    }
}
