package com.robotgame.gameengine.Match;

import com.robotgame.gameengine.Network.MatchState;
import com.robotgame.gameengine.Network.NetworkInterface;
import com.robotgame.gameengine.Network.matchHandler;
import com.robotgame.gameengine.Robot.Builder.RobotBlueprint;
import com.robotgame.gameengine.Robot.Builder.RobotFactory;
import com.robotgame.gameengine.Robot.MatchContext;
import com.robotgame.gameengine.Robot.Nodes.NodeAction;
import com.robotgame.gameengine.Robot.Robot;


import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Oskar
 * Date: 2013-10-14
 * Time: 12:34
 * To change this template use File | Settings | File Templates.
 */

/**
 * Represents one game. Is run by a class implementing IMatchHandler that provides a function to call when the match is over.<p/>
 * <p/>
 * After the match has been created it has to be populated by robots using the method BuildRobots() that accepts a vector of RobotBlueprint objects as argument.
 * When the robots are built the match can be started by calling the method run().
 * When the match is over it will call the method MatchEnded() in match handler.
 * A Match object has the following update cycle: <p/>
 * 1. Create MatchContext object containing all information relevant to sensor nodes.<p/>
 * 2. Update nodes of every robot.<p/>
 * 3. Perform the actions created by the nodes of each robot.<p/>
 * 4. Update the states of every robot, applying the impulses generated by the actions in the previous step.<p/>
 * 5. Create a MatchState object containing all information needed by the clients.<p/>
 * 6. Send the match state to the clients via the match handler.<p/>
 * 7. Check if match has ended, if so call the MatchEnded() in match handler.<p/>
 * 8. Advance the game clock.<p/>
 * <p/>
 * Todo: Add projectile system.
 *
 */
public class Match implements Runnable
{
    private Robot[] _robots;
    private int _numRobots;
    private boolean _running;
    private MatchContext _context;
    private int _clock;
    private IMatchHandler _matchHandler;
    private MatchResult _matchResult;
    private MatchState _matchState;
    private int _matchId;
    //private ProjectileSystem _projectileSystem;



    //Public methods

    /**
     * Match objects should be created in a class implementing IMatchHandler. That object should pass itself as the matchHandler argument.
     * A typical Match object is created by a match handler as: Match match = new Match(this, 0);
     * @param matchHandler a handler capable of communicating network data with players
     * @param matchId a unique identifying number for the game. May be used as unique key in a future statistics database.
     */
    public Match(IMatchHandler matchHandler, int matchId)
    {
        _matchId = matchId;
        _clock = 0;
        _numRobots = 0;
        _running = false;
        _matchHandler = matchHandler;
    }

    /**
     * Needs to be called before run() is called.
     * @param blueprints The blueprints of the robots to participate in the game.
     * @return whether the robots were successfully built.
     */
    public boolean BuildRobots(Vector<RobotBlueprint> blueprints)
    {

        _numRobots = blueprints.size();
        _robots = new Robot[_numRobots];

        for (int n = 0; n < _numRobots; n++)
        {
            _robots[n] = RobotFactory.CreateRobot(blueprints.get(n), n);
            if (_robots[n] == null) return false;
        }

        _context = new MatchContext(_numRobots);
        _matchState = new MatchState(_matchId, _numRobots);
        _matchResult = new MatchResult(_numRobots);
        return true;
    }


    //Obsolete
    public void SetRunning(boolean v)
    {
        _running = v;
    }

    //Obsolete
    public void start()
    {
        _running = true;
    }


    /**
     * Starts and runs the match. It will not work unless BuildRobots() has been called successfully.
     * _matchHandler.MatchEnded() will be called when the match is over.
     */
    public void run()
    {
        //Check if robots have been built.
        if (_robots == null) _running = false;
        else _running = true;


        while(_running)
        {
            Update();

            try { Thread.sleep(33); //Max 30 updates per sec
            } catch(InterruptedException e) {};
        }
    }




    //Private methods

    private void Update()
    {

//      1. Create MatchContext object containing all information relevant to sensor nodes.
        CreateMatchContext();


//      2. Update nodes of every robot.
        for (int n = 0; n < _numRobots; n++)
        {
            _robots[n].UpdateNodes(_context);

        }


//      3. Perform the actions created by the nodes of each robot.
        for (int n = 0; n < _numRobots; n++)
        {
            LinkedList<NodeAction> actions = _robots[n].GetActions();
            for (NodeAction a : actions)
                a.PerformAction(_robots[n]);

            //System.out.println("Hot connections: " + _robots[n].GetHotConnections());
        }


//      4. Update the states of every robot, applying the impulses generated by the actions in the previous step.
        for (int n = 0; n < _numRobots; n++)
        {
            _robots[n].UpdateState();
        }


//      5. Create a MatchState object containing all information needed by the clients.
        CreateMatchState();


//      6. Send the match state to the clients via the match handler.
        _matchHandler.SendMatchState(_matchState);


//      7. Check if match has ended, if so call the MatchEnded() in match handler.
        if (_clock > 120)
        {
            _matchResult.winningTeam = 2;
            _running = false;
            _matchHandler.MatchEnded(_matchResult);
        }


//      8. Advance the game clock.
        _clock++;

    }


    //Creates en MatchContext with all the info for the sensor nodes.
    private void CreateMatchContext()
    {
        for (int n = 0; n < _numRobots; n++)
        {
            _context.robotStates[n] = _robots[n].GetCurrentState();
            //_context.A[n] = _networkInterface.GetInputA(n);
            //_context.B[n] = _networkInterface.GetInputB(n);
        }


        //ToDo: Lägga till lista över projektiler?
    }


    //Creates a MatchState object that contains the information that is to be sent to the players
    private void CreateMatchState()
    {
        for (int n = 0; n < _numRobots; n++)
            _matchState.robotStates[n] = _robots[n].GetCurrentState();
    }

}
