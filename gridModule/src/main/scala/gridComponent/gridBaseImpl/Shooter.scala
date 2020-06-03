package gridComponent.gridBaseImpl

import gridComponent.gameState.GameStatus


class Shooter {

  def simShot(): Boolean = {
    val r = new scala.util.Random
    val r1 = r.nextInt(100)
    if (GameStatus.currentHitChance >= r1) {
      val dmg = GameStatus.activeTank.get.tankBaseDamage + 40
      dealDmgTo(dmg)
      print("You did: " + dmg + " dmg\n")
      if (GameStatus.passiveTank.get.hp <= 0) {
        return true
      }
      false
    } else {
      print("Sadly you missed...\n")
      false
    }
  }
  def dealDmgTo(dmg: Int): Unit = {
    GameStatus.passiveTank = GameStatus.passiveTank match {
      case Some(i) => Some(i.copy(hp = i.hp - dmg))
      case None => None
    }
    if(GameStatus.passiveTank.get.hp<=0){
      GameStatus.endGame()
    }
  }
  def shoot(): Unit = {
    if (GameStatus.currentHitChance > 0) {
      if (simShot()) {
        GameStatus.endGame()
      }
      GameStatus.increaseTurns()
    } else {
      print("No Target in sight\n")
    }
  }

}
