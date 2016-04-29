package com.walter.eightball

import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.{Gdx, Screen}
import com.walter.eightball.{Ball, Board, PhysicsHandler, Vector3D}

import scala.collection.mutable.Buffer
import scala.util.Random

/** Takes care of the game */
class GameScreen extends Screen {

  lazy val scale = Gdx.graphics.getWidth / 3f //Scale factor for rendering
  lazy val camera = new OrthographicCamera(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)
  lazy val shapeRenderer = new ShapeRenderer()
  lazy val gameBoard = new Board()
  lazy val balls = Buffer[Ball]()
  lazy val cueStick = new CueStick(Vector3D(0f, 0f, 0f), 0f, 0f)
  var gameState = GameState.Aiming

  override def hide(): Unit = ()

  override def resize(width: Int, height: Int): Unit = ()

  override def dispose(): Unit = ()

  override def pause(): Unit = ()

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    if (Gdx.input.isTouched()) {
      val random = new Random()
      balls(0).velocity = Vector3D(random.nextFloat() * 5f, random.nextFloat() * 5f, 0f)
    }

    gameState match {
      case GameState.Aiming => {
        cueStick.pointAt = balls(0)
        cueStick.distance = 0.1f

        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        balls.foreach(_.render(shapeRenderer, scale))
        cueStick.render(shapeRenderer, scale)
        shapeRenderer.end()
      }

      case GameState.Shooting => {
        ???
      }

      case GameState.Rolling => {
        PhysicsHandler.update(balls, delta)

        shapeRenderer.begin(ShapeType.Filled)
        gameBoard.render(shapeRenderer, scale)
        balls.foreach(_.render(shapeRenderer, scale))
        shapeRenderer.end()
      }
    }


  }

  override def show(): Unit = {
    camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    //Cue ball
    balls += new Ball(0.25f, 0.635f, 0f, 1)
    balls(0).angularVelocity = Vector3D(0f, 0f, 0f)

    balls += new Ball(1.69f, 0.635f, 0f, 1)

    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 1)

    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f, 0f, 1)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 1)

    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 3.03f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 3.03f * balls(0).radius, 0f, 1)

    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 4.04f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 1)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 4.04f * balls(0).radius, 0f, 1)

    //Distribute balls a bit randomly
    val random = new Random()
    balls foreach { ball => {
      ball += Vector3D(0.00005f * random.nextInt(100), 0.00005f * random.nextInt(100), 0f)
    }}

    for (i <- 0 until balls.size) {
      for (n <- i + 1 until balls.size) {
        if ((balls(i) - balls(n)).norm < balls(0).radius + balls(1).radius) println("Overlapping in the beginning")
      }
    }
  }

  override def resume(): Unit = ()

}
