package com.robotgame.gameengine.Robot.Nodes.LogicNodes;

import com.robotgame.gameengine.Robot.MatchContext;
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
 * Node that constantly outputs false.
 * @see Node
 */
public class FalseNode extends Node
{
    /*
    Members of parent class Node to be defined in constructor:
    _maxInputs = ?;
    _connectionToInput = new int[_maxInputs];  //If _maxInputs > 0
    _category = NodeCategory.?;
    _type = NodeType.?;
    _ownerIndex = ownerIndex;
    */

    public FalseNode(int ownerIndex)
    {
        _maxInputs = 0;
        _category = NodeCategory.Logic;
        _type = NodeType.False;
        _ownerIndex = ownerIndex;

        _output = false;
    }

    @Override
    public void Update(MatchContext context, LinkedList<NodeAction> actions,  boolean[] input)
    {
        _isUpdated = true;
    }
}
