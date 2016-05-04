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

  //lazy val gameScreen = new GameScreen
  lazy val menuScreen = new MenuScreen(this)

    override def create(): Unit = {
      this.setScreen(menuScreen)
    }

    override def render(): Unit = {
      this.getScreen.render(Gdx.graphics.getDeltaTime)
    }
    
    override def dispose(): Unit = ()
}
