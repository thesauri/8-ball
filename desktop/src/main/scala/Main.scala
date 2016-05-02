package com.walter.eightball

import com.badlogic.gdx.backends.lwjgl._

object Main extends App {
    val cfg = new LwjglApplicationConfiguration
    cfg.title = "eightball"
    cfg.height = 480
    cfg.width = 800
    cfg.forceExit = false
    cfg.useHDPI = true
    new LwjglApplication(new Eightball, cfg)
}
