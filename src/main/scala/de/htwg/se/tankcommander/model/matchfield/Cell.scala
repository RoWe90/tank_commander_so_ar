package de.htwg.se.tankcommander.model.matchfield

import de.htwg.se.tankcommander.model.items.Item
import de.htwg.se.tankcommander.model.playerdata.TankModel
import de.htwg.se.tankcommander.model.powerup.PowerUP
import de.htwg.se.tankcommander.model.terrain.Obstacle
import de.htwg.se.tankcommander.model.upgrades.Upgrade

class Cell(x1: Integer, y1: Integer) {
  val x = x1;
  val y = y1;
  var cellterrain: Obstacle = null
  var cellPowerUP: PowerUP = null;
  var cellUpgrade: Upgrade = null;
  var cellItem: Item = null;
  var containsThisTank: TankModel = null

  def setCellContent(x: PowerUP): Unit = {
    cellPowerUP = x;
  }

  def setCellContent(x: Upgrade): Unit = {
    cellUpgrade = x;
  }

  def setCellContent(x: Item): Unit = {
    cellItem = x;
  }

  def setCellContent(x: Obstacle): Unit = {
    cellterrain = x;
  }

  def removeCellContentTerrain(): Unit = {
    cellterrain = null;
  }

  def removeCellContentPUI(): Unit = {
    cellItem = cellUpgrade = cellPowerUP = null;
  }

  def removeTank(x: TankModel): Unit = {
    containsThisTank = null;
  }

}
