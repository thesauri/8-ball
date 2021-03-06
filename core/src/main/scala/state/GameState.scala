package com.walter.eightball.state

import java.io.{ObjectInputStream, ObjectOutputStream}

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.{Pixmap, PixmapIO, Texture}
import com.badlogic.gdx.utils.TimeUtils
import com.walter.eightball.math.Vector3D
import com.walter.eightball.objects.{Ball, CueStick}
import scala.collection.mutable

/** A mutable class to store and manipulate the current state of the game */
@SerialVersionUID(1L)
class GameState extends Serializable{

  val balls = mutable.Buffer[Ball]()
  var gameState = GameStateType.Aiming
  var hasSolids: Option[Int] = None //The player that shoots solids (fully colored balls), None, 1 or 2
  val cueStick = new CueStick(Vector3D(0f, 0f, 0f), 0f, 0f)
  var isPlayer1sTurn = true //Whether it's player 1's turn or not

  private var hasPocketedCueBall = false
  private var hasPocketedRightBall = false
  private var hasPocketedWrongBall = false
  private var hasPocketedEightBall = false

  /** Optionally returns the cue ball */
  def cueBall: Option[Ball] = balls.find( _.number == 0 )

  /** Advances the game to the next round */
  def nextRound(): Unit = {
    if (hasPocketedEightBall) {
      gameState = GameStateType.Lost
    } else if (hasPocketedCueBall) {
      balls += new Ball(0.25f, 0.635f, 0f, 0)
      isPlayer1sTurn = !isPlayer1sTurn
    } else if (!hasPocketedWrongBall || !hasPocketedRightBall) {
      isPlayer1sTurn = !isPlayer1sTurn
    }

    hasPocketedCueBall = false
    hasPocketedRightBall = false
    hasPocketedWrongBall = false
    hasPocketedEightBall = false

    if (gameState != GameStateType.Lost) {
      gameState = GameStateType.Aiming
    }
  }

  /** Adds balls to the game state at their default positions */
  def placeBallsAtDefaultPositions(): Unit = {
    balls.clear()

    balls += new Ball(0.25f, 0.635f, 0f, 0)

    balls += new Ball(1.69f, 0.635f, 0f, 1)

    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 15)
    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 2)

    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 14)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f, 0f, 8)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 13)

    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 3.03f * balls(0).radius, 0f, 4)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 12)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 5)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 3.03f * balls(0).radius, 0f, 11)

    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 4.04f * balls(0).radius, 0f, 6)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 10)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f, 0f, 7)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 9)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 4.04f * balls(0).radius, 0f, 3)

    gameState = GameStateType.Aiming
    hasPocketedCueBall = false
    hasPocketedRightBall = false
    hasPocketedWrongBall = false
    hasPocketedEightBall = false
    hasSolids = None
    isPlayer1sTurn = true
  }

  /** Returns the number of balls that player 1 has remaining */
  def remainingBallsPlayer1: Int = hasSolids match {
      case Some(1) => balls.count( _.number <= 8 )
      case Some(2) => balls.count( _.number >= 8 )
      case None => 8
  }

  /** Returns the number of balls that player 1 has remaining */
  def remainingBallsPlayer2: Int = hasSolids match {
    case Some(1) => balls.count( _.number >= 8 )
    case Some(2) => balls.count( _.number <= 8 )
    case None => 8
  }

  /** Removes a ball from the state
    *
    * If the cue ball is removed, it will be re-added
    * in the beginning of the next round when the
    * nextRound method is called.
    *
    * This method also makes sure that turns will
    * switch properly the next round depending on
    * what balls were pocketed. */
  def removeBall(ball: Ball): Unit = {
    require(balls.contains(ball), s"The game state does not contain the ball $ball")

    if (ball.number == 0) {
      hasPocketedCueBall = true
    } else if (ball.number == 8) {
      hasPocketedEightBall = true
    } else if (ball.number < 8) {

      //Solid ball pocketed
      hasSolids match {
        case Some(1) => if (isPlayer1sTurn) hasPocketedRightBall = true else hasPocketedWrongBall = true
        case Some(2) => if (!isPlayer1sTurn) hasPocketedRightBall = true else hasPocketedWrongBall = true
        case None => {
          hasSolids = Some(1)
          hasPocketedRightBall = true
        }
      }
    } else {

      //Striped ball pocketed
      hasSolids match {
        case Some(1) => if (isPlayer1sTurn) hasPocketedWrongBall = true else hasPocketedRightBall = true
        case Some(2) => if (!isPlayer1sTurn) hasPocketedWrongBall = true else hasPocketedRightBall = true
        case None => {
          hasSolids = Some(2)
          hasPocketedRightBall = true
        }
      }
    }

    balls.remove(balls.indexOf(ball))
  }

  /** Returns true if the ball should be shot by the current player */
  def shouldBeShot(ball: Ball): Boolean = hasSolids match {
    case Some(1) => ball.number <= 8 && isPlayer1sTurn || ball.number >= 8 && !isPlayer1sTurn || ball.number == 0
    case Some(2) => ball.number <= 8 && !isPlayer1sTurn || ball.number >= 8 && isPlayer1sTurn || ball.number == 0
    case None => true
    case _ => true
  }
}

/** Companion object for loading and saving game states */
object GameState {

  /** Unserializes a game state from a file */
  def load(file: FileHandle): GameState = {
    val in = file.read
    val ois = new ObjectInputStream(in)
    val state = ois.readObject
    ois.close()

    state match {
      case state: GameState => {
        state.gameState = GameStateType.Rolling
        state
      }
      case _ => {
        //If we're unable to unserialize it, let's just create a default game state and avoid a crash..
        val emptyState = new GameState
        emptyState.placeBallsAtDefaultPositions()
        emptyState
      }
    }

  }

  /** Saves the given game state and the associated screenshot to the file system
    *
    * The serialized object is saved as saves/<datetime> and
    * the screenshot as saves/<datetime>.png */
  def save(gameState: GameState, screenshot: Pixmap): Boolean = {
    val datetime = TimeUtils.millis

    val stateFile = Gdx.files.local(s"saves/$datetime.8ball")
    val imageFile= Gdx.files.local(s"saves/${datetime}.png")

    //Save the serialized game state
    val out = stateFile.write(false)
    val oos = new ObjectOutputStream(out)
    oos.writeObject(gameState)
    oos.close()

    //Save the screenshot
    PixmapIO.writePNG(imageFile, screenshot)

    true
  }


  /** Returns a sequence of files storing serialized game states */
  def savedGames: Seq[FileHandle] = Gdx.files.local("saves/").list.filter( _.extension != "png" )

  /** Returns a sequence of tuples containing a screenshot and optionally a file path to the associated serialized game state */
  def savedScreenshots: Seq[(Texture, Option[FileHandle])] = {

    val defaultSave: (Texture, Option[FileHandle]) = (new Texture(Gdx.files.internal("defaultsave/default.png")), None)

    val userSaves = Gdx.files.local("saves/").list.filter( _.extension == "png" )
    val userSaveFiles = userSaves.sortBy( _.name ).reverse
    val woExtensions = userSaveFiles map (fh => Some(Gdx.files.local(fh.pathWithoutExtension + ".8ball")))
    val savedScreenshots = userSaveFiles.map( new Texture(_) )

    defaultSave +: (savedScreenshots zip woExtensions)
  }

}