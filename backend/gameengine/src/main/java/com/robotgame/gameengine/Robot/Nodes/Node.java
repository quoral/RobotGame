package com.robotgame.gameengine.Robot.Nodes;

import com.robotgame.gameengine.Robot.MatchContext;
import com.robotgame.gameengine.Robot.Robot.*;
import java.util.*;

/**
 * @(#)Robot.Nodes.Node.java
 *
 *
 * @author Oskar Krantz
 * @version 1.00 2013/10/10
 */


public abstract class Node
{
    protected boolean _isUpdated;
    protected boolean _output;
    protected int _numOutput;    //Obsolete
    protected int[] _connectionToInput;
    protected int _numInput;
    protected int _maxInputs;
    protected NodeCategory _category;
    protected NodeType _type;
    protected int _ownerIndex;

    public Node()
    {
        _numInput = 0;
        _output = false;
        _isUpdated = false;
    }


    public void Reset()
    {
        _isUpdated = false;
    }

    public boolean IsUpdated()
    {
        return _isUpdated;
    }

    public int GetNumInput()
    {
        return _numInput;
    }

    public void SetInput(int channel, int connection) //Obsolete
    {
        if (channel >= 0 && channel < _numInput)
            _connectionToInput[channel] = connection;
    }

    public void AddInput(int connection)
    {
        if (_numInput < _maxInputs)
            _connectionToInput[_numInput++] = connection;
    }

    public int GetInputConnection(int channel)
    {
        if (channel >= 0 && channel < _numInput && _connectionToInput != null)
            return _connectionToInput[channel];
        else return -1;
    }

    public boolean GetOutput()
    {
        return _output;
    }

    //This method is overridden by the sensor subclasses
    public abstract void Update(MatchContext context, LinkedList<NodeAction> actions, boolean[] input);

}