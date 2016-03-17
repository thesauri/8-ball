package com.walter.eightball

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

class Eightball extends Game {
  
    lazy val camera = new OrthographicCamera(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)
    lazy val shapeRenderer = new ShapeRenderer()
    
    lazy val gameBoard = new Board()
  
    override def create() {
      shapeRenderer.setProjectionMatrix(camera.combined)
    }
    
    override def render() {
      shapeRenderer.begin(ShapeType.Filled)
      gameBoard.render(shapeRenderer)
      shapeRenderer.end()
    }
}
