package com.robotgame.gameengine.Robot.Nodes.ActionNodes;

import com.robotgame.gameengine.Robot.MatchContext;
import com.robotgame.gameengine.Robot.Nodes.Actions.ImpulseAction;
import com.robotgame.gameengine.Robot.Nodes.Actions.RotationalImpulseAction;
import com.robotgame.gameengine.Robot.Nodes.Node;
import com.robotgame.gameengine.Robot.Nodes.NodeAction;
import com.robotgame.gameengine.Robot.Nodes.NodeCategory;
import com.robotgame.gameengine.Robot.Nodes.NodeType;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Oskar
 * Date: 2013-10-14
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */

/**
 * When activated this node adds rotation to the robot using RotationalImpulseAction.
 * This node accepts 1 input.
 * @see RotationalImpulseAction
 * @see Node
 */
public class TurnLeftNode extends Node
{
    /*
    Members of parent class Node to be defined in constructor:
    _maxInputs = ?;
    _connectionToInput = new int[_maxInputs];  //If _maxInputs > 0
    _category = NodeCategory.?;
    _type = NodeType.?;
    _ownerIndex = ownerIndex;
    */

    public TurnLeftNode(int ownerIndex)
    {
        _maxInputs = 1;
        _connectionToInput = new int[_maxInputs];  //If _maxInputs > 0
        _category = NodeCategory.Action;
        _type = NodeType.TurnLeft;
        _ownerIndex = ownerIndex;
    }


    public void Update(MatchContext context, LinkedList<NodeAction> actions,  boolean[] input)
    {
        actions.add(new RotationalImpulseAction(1));
        _isUpdated = true;
    }
}
