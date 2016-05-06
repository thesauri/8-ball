package com.walter.eightball

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx

/** The main class of the game, instantiated when the game is opened */
class Eightball extends Game {

  /** Run when the game is opened */
  override def create(): Unit = {
    this.setScreen(new MenuScreen(this))
  }

  /** Called every frame to render the game */
  override def render(): Unit = {
    //Render the current screen
    this.getScreen.render(Gdx.graphics.getDeltaTime)
  }

  override def dispose(): Unit = ()
}
