package com.robotgame.gameengine.Robot.Nodes;

import com.robotgame.gameengine.Robot.MatchContext;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Oskar
 * Date: 2013-10-21
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */

//Main class for handling and updating all nodes
//The Update() method returns a list of action objects that the nodes have triggered.
public class NodeSystem
{
    private Node[] _nodes;
    private NodeConnection[] _connections;
    private int _numNodes, _numConnections;
    private MatchContext _currentMatchContext;
    private LinkedList<NodeAction> _actions;

    public NodeSystem(Node[] nodes,  NodeConnection[] connections)
    {
        _nodes = nodes;
        _connections = connections;
        _numNodes = _nodes.length;
        _numConnections = _connections.length;
        _actions = new LinkedList<NodeAction>();
    }

    public LinkedList<NodeAction> Update(MatchContext context)
    {
        _actions.clear();
        _currentMatchContext = context;


        int n;

        for (n = 0; n < _numNodes; n++)
        {
            _nodes[n].Reset();
        }

        for (n = 0; n < _numNodes; n++)
        {
            if (!_nodes[n].IsUpdated()) GetOutputOfNode(n);
        }

        return _actions;
    }

    /**
     * Recursive function to update and evaluate nodes.
     *
     * @param  nodeIndex  the index of the node
     * @return      the output of the specified node
     */
    private boolean GetOutputOfNode(int nodeIndex)
    {
        if (_nodes[nodeIndex].IsUpdated())
        {
            return _nodes[nodeIndex].GetOutput();
        }
        else
        {
            if (_nodes[nodeIndex].GetNumInput() == 0)
            {
                boolean[] temp = { false };
                _nodes[nodeIndex].Update(_currentMatchContext, _actions, temp);
                return _nodes[nodeIndex].GetOutput();
            }
            else
            {
                int numInputs = _nodes[nodeIndex].GetNumInput();
                boolean[] temp = new boolean[numInputs];
                int inputConnection;

                for (int n = 0; n < numInputs; n++)
                {
                    inputConnection = _nodes[nodeIndex].GetInputConnection(n);
                    temp[n] = GetOutputOfNode(_connections[inputConnection].FromNode());

                }
                _nodes[nodeIndex].Update(_currentMatchContext, _actions, temp);
                return _nodes[nodeIndex].GetOutput();
            }


        }

    }


    //Returns an array of the indexes of the currently hot connections
    public boolean[] GetHotConnections()
    {
        boolean[] out = new boolean[_numConnections];
        for (int n = 0; n < _connections.length; n++)
        {
            out[n] = _nodes[_connections[n].FromNode()].GetOutput();
        }

        return out;
    }





}
