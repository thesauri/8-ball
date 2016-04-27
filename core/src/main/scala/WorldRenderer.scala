package com.walter.eightball

import com.badlogic.gdx.graphics.{Camera, Color}
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.{Environment, Material, ModelBatch, ModelInstance}
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder

/** Object used for rendering a world according to ball states */

object WorldRenderer {

  val modelBatch = new ModelBatch()
  val builder = new ModelBuilder()
  val ballInstances = Array.fill(16)(builder.createSphere(Ball.Radius, Ball.Radius, Ball.Radius, 24, 24,
                                     new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                                     Usage.Position | Usage.Normal)).map( new ModelInstance(_) )
  val environment = new Environment()
  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
  environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, -1f));


  def render(camera: Camera, balls: Seq[Ball]): Unit = {

    modelBatch.begin(camera)

    for (i <- 0 until balls.size) {
      val curBall = balls(i)
      ballInstances(i).transform.setToTranslation(curBall.x, curBall.y, curBall.z)
    }

    ballInstances foreach { modelBatch.render(_, environment) }

    //modelBatch.render(instance)

    modelBatch.end()
  }

}
