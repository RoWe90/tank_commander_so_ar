package tankcommander.controllerComponent.controllerBaseImpl

import com.google.inject.{Guice, Inject, Injector}
import tankcommander.controllerComponent.ControllerInterface
import tankcommander.controllerComponent.fileIoComponent.FileIOInterface
import gamestate.{GameStatus, TankModel}
import gridComponent.GameFieldInterface
import gridComponent.gridBaseImpl.{Cell, GameFieldFactory}
import net.codingwell.scalaguice.InjectorExtensions._
import playerComponent.Player
import tankcommander.TankCommanderModule
import tankcommander.util.{Observable, UndoManager}

import scala.swing.Publisher

class Controller @Inject()() extends Observable with Publisher with ControllerInterface {
  var matchfield: GameFieldInterface = GameFieldFactory.apply("Map 1")
  var mapChosen: String = ""
  private val undoManager = new UndoManager
  val injector: Injector = Guice.createInjector(new  TankCommanderModule)
  val fileIO: FileIOInterface = injector.instance[FileIOInterface]

  def this(matchfield2: GameFieldInterface) {
    this()
    matchfield = matchfield2
  }

  override def setUpGame(): Unit = {
    print("Welcome to Tank-Commander\n" + "Player 1 please choose your Name" + "\n")
    val player1 = Player(scala.io.StdIn.readLine())
    print("Player 2 please choose your Name" + "\n")
    val player2 = Player(scala.io.StdIn.readLine())
    val tank1 = TankModel()
    val tank2 = TankModel()
    print("Choose your Map: Map 1 or Map 2" + "\n")
    mapChosen = scala.io.StdIn.readLine()
    matchfield = GameFieldFactory.apply(mapChosen)
    fillGameFieldWithTanks((0, 5), tank1, (10, 5), tank2)
    GameStatus.activePlayer = Some(player1)
    GameStatus.passivePlayer = Some(player2)
    notifyObservers()
  }

  def setUpGame(name1: String, name2: String, map: String): Unit = {
    val player1 = Player(name1)
    val player2 = Player(name2)
    mapChosen = map
    matchfield = GameFieldFactory.apply(mapChosen)
    val tank1 = TankModel()
    val tank2 = TankModel()
    fillGameFieldWithTanks((0, 5), tank1, (10, 5), tank2)
    GameStatus.activePlayer = Some(player1)
    GameStatus.passivePlayer = Some(player2)
    notifyObservers()
  }

  override def fillGameFieldWithTanks(pos: (Int, Int), tank: TankModel, pos2: (Int, Int), tank2: TankModel): Unit = {
    GameStatus.activeTank = Some(TankModel(posC = pos))
    GameStatus.passiveTank = Some(TankModel(posC = pos2))
    matchfield = matchfield.update(matchfield.mvector.updated(
      pos._1, matchfield.mvector(pos._1).updated(
        pos._2, Cell(pos, matchfield.mvector(pos._1)(pos._2).cobstacle, Some(tank)))))

    matchfield = matchfield.update(matchfield.mvector.updated(
      pos2._1, matchfield.mvector(pos2._1).updated(
        pos2._2, Cell(pos2, matchfield.mvector(pos2._1)(pos2._2).cobstacle, Some(tank2)))))

  }

  override def endTurnChangeActivePlayer(): Unit = {
    print("Runde beendet Spielerwechsel")
    GameStatus.changeActivePlayer()
    notifyObservers()
  }

  override def createGameStatusBackup: GameStatus = {
    val backup = new GameStatus
    backup
  }

  override def checkIfPlayerHasMovesLeft(): Boolean = {
    if (GameStatus.movesLeft) {
      true
    } else {
      print("no turns left")
      false
    }
  }

  override def matchfieldToString: String = matchfield.displayField()

  /*
 * Undo manager
 */

  override def move(s: String): Unit = {
    undoManager.doStep(new MoveCommand(this, s))
    notifyObservers()
  }

  override def shoot(): Unit = {
    undoManager.doStep(new ShootCommand(this))
    notifyObservers()
  }

  override def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers()
  }

  override def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers()
  }

  override def save(): Unit = {
    fileIO.save()
  }

  override def load(): Unit = {
    matchfield = GameFieldFactory.apply(mapChosen)
    fileIO.load(this)
    notifyObservers()
  }
}


