package com.walter.eightball

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics.GL20

import scala.collection.mutable.Buffer
import scala.util.Random

class Eightball extends Game {

  lazy val gameScreen = new GameScreen()

    override def create(): Unit = {
      this.setScreen(gameScreen)
    }

    override def render(): Unit = {
      gameScreen.render(Gdx.graphics.getDeltaTime)
    }
    
    override def dispose(): Unit = ()
}
