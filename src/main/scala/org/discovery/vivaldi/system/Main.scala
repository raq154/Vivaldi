package org.discovery.vivaldi.system

import akka.event.Logging
import akka.actor.{Props, ActorSystem, Actor}
import org.discovery.vivaldi.core.ComputingAlgorithm
import org.discovery.vivaldi.dto.{CloseNodeInfo, RPSInfo, Coordinates, UpdatedCoordinates}
import org.discovery.vivaldi.network.Communication
import scala.math._
import scala.util.Sorting.quickSort

/* ============================================================
 * Discovery Project - AkkaArc
 * http://beyondtheclouds.github.io/
 * ============================================================
 * Copyright 2013 Discovery Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ */

class Main extends Actor{

  val log = Logging(context.system, this)

  //Creating child actors
  val vivaldiCore = context.actorOf(Props(classOf[ComputingAlgorithm], self), "VivaldiCore")
  val network = context.actorOf(Props(classOf[Communication], vivaldiCore), "Network")

  var coordinates: Coordinates = Coordinates(0,0)
  var closeNodes: List[CloseNodeInfo] = List()

  /**
   * Method that handles the oncoming messages
   * @return
   */
  def receive = {
    case UpdatedCoordinates(newCoordinates, rps) => updateCoordinates(newCoordinates, rps)
    case _ => log.info("Message Inconnu")
  }

  /**
   * Method to update the close node list coordinates.
   * @param newCoordinates updated coordinates of the current node just calculates
   * @param rps list of the RPS nodes that will be used to update the close node list
   */
  def updateCoordinates(newCoordinates: Coordinates, rps: List[RPSInfo]) {
    log.debug(s"New coordinated received: $newCoordinates")
    coordinates = newCoordinates

    log.debug("Computing & updating distances")
    //Computing the distances from the RPS table
    val RPSCloseNodes = rps.map((node: RPSInfo) => CloseNodeInfo(node.node,node.coordinates,computeDistanceToSelf(node.coordinates)))

    //TODO Test contain with data and code the method equals for closeNodes
    //Get rid of the nodes already in the list
    val RPSCloseNodesToAdd = RPSCloseNodes.filter((node: CloseNodeInfo) => !closeNodes.contains(node))
    //Retrieve nodes to update
    val RPSCloseNodesUpdates = RPSCloseNodes.filter((node: CloseNodeInfo) => closeNodes.contains(node))

    //Updating nodes
    for (rpsNode <- RPSCloseNodesUpdates){
      closeNodes = closeNodes.map((node: CloseNodeInfo) => if (rpsNode.equals(node)) node.copy(coordinates = rpsNode.coordinates) else node.copy())
    }
    //Computing and updating distances for the existing nodes
    closeNodes = closeNodes.map((node: CloseNodeInfo) => node.copy(distanceFromSelf = computeDistanceToSelf(this.coordinates)))

    //Adding new Nodes
    closeNodes = RPSCloseNodesToAdd ++ closeNodes

    log.debug("Ordering closest node List")
    closeNodes.sorted
    //TODO Troncate the list to only keep the n first element. We first have to define the configuration part
  }

  /**
   * Method to compute the distance between the current node and the node in parameter
   * @param externCoordinates to compute the distance from
   * @return
   */
  def computeDistanceToSelf(externCoordinates: Coordinates): Double = {
    hypot(coordinates.x-externCoordinates.x,coordinates.y-externCoordinates.y)
  }

  def initSystem(){

  }

}
