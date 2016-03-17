package com.walter.eightball

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Gdx

class Eightball extends Game {
  
    val camera = new OrthographicCamera(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)
    val shapeRenderer = new ShapeRenderer()
    shapeRenderer.setProjectionMatrix(camera.combined)
    
    val gameBoard = new Board()
  
    override def create() {}
    
    override def render() {
      
    }
}
