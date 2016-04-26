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

    lazy val scale = Gdx.graphics.getWidth / 3f //Scale factor for rendering
    lazy val camera = new OrthographicCamera(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)
    lazy val shapeRenderer = new ShapeRenderer()
    lazy val gameBoard = new Board()
    lazy val balls = Buffer[Ball]()

    override def create(): Unit = {
      camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
      camera.update()
      shapeRenderer.setProjectionMatrix(camera.combined)

      //Cue ball
      balls += new Ball(0.25f, 0.635f, 0f, 1)
      balls(0).velocity = Vector3D(4f, 0f, 0f)
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
      /*val random = new Random()
      balls foreach { ball => {
        ball += Vector3D(0.00005f * random.nextInt(100), 0.00005f * random.nextInt(100), 0f)
      }}*/

      for (i <- 0 until balls.size) {
        for (n <- i + 1 until balls.size) {
          if ((balls(i) - balls(n)).norm < balls(0).radius + balls(1).radius) println("Overlapping in the beginning")
        }
      }

      ()
    }

    override def render(): Unit = {
      Gdx.gl.glClearColor(1, 1, 1, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      
      PhysicsHandler.update(balls, Gdx.graphics.getDeltaTime)

      shapeRenderer.begin(ShapeType.Filled)
      gameBoard.render(shapeRenderer, scale)
      balls.foreach(_.render(shapeRenderer, scale))
      shapeRenderer.end()
    }
    
    override def dispose(): Unit = {
      shapeRenderer.dispose()
    }
}
