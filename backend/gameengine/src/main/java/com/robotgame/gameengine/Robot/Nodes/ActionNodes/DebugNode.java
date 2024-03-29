package com.robotgame.gameengine.Robot.Nodes.ActionNodes;

import com.robotgame.gameengine.Robot.MatchContext;
import com.robotgame.gameengine.Robot.Nodes.Actions.DebugAction;
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
 * Only for testing purposes.
 * This node prints the value of its single input using System.out.println().
 * @see Node
 * @see DebugAction
 */
public class DebugNode extends Node
{
    /*
    Members of parent class Node to be defined in constructor:
    _maxInputs = ?;
    _connectionToInput = new int[_maxInputs];  //If _maxInputs > 0
    _category = NodeCategory.?;
    _type = NodeType.?;
    _ownerIndex = ownerIndex;
    */

    public DebugNode(int ownerIndex)
    {
        _maxInputs = 1;
        _connectionToInput = new int[_maxInputs];
        _category = NodeCategory.Logic;
        _type = NodeType.True;
        _ownerIndex = ownerIndex;

    }


    public void Update(MatchContext context, LinkedList<NodeAction> actions,  boolean[] input)
    {
        String out = String.valueOf(input[0]);
        actions.add(new DebugAction("Value of connection " + _connectionToInput[0] + ": " + out));
        _isUpdated = true;
    }
}
