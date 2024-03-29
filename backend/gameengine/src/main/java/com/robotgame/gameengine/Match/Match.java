package com.robotgame.gameengine.Match;

import com.robotgame.gameengine.Network.MatchState;
import com.robotgame.gameengine.Network.NetworkInterface;
import com.robotgame.gameengine.Network.MatchHandler;
import com.robotgame.gameengine.Projectile.ProjectileSystem;
import com.robotgame.gameengine.Robot.Builder.RobotBlueprint;
import com.robotgame.gameengine.Robot.Builder.RobotFactory;
import com.robotgame.gameengine.Robot.MatchContext;
import com.robotgame.gameengine.Robot.Nodes.NodeAction;
import com.robotgame.gameengine.Robot.Robot;
import com.robotgame.gameengine.Util.Vector2;


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
    public static final float DT = 0.04f;
    public static final int DT_MS = (int)(DT*1000);

    private Robot[] _robots;
    private int _numRobots;
    private boolean _running;
    private boolean[] _Ainputs;
    private boolean[] _Binputs;
    private MatchContext _context;
    private int _clock;
    private IMatchHandler _matchHandler;
    private MatchResult _matchResult;
    private MatchState _matchState;
    private int _matchId;
    private ProjectileSystem _projectileSystem;
    private int _timeLimit;


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
        _projectileSystem = new ProjectileSystem(10);
        _timeLimit = 500;
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
            
            _robots[n].SetStartPos(new Vector2(-1 + 2 * n, 0));
            _robots[n].SetStartDir((float)Math.PI*n);
        }
        
        _context = new MatchContext(_numRobots);
        _Ainputs = new boolean[_numRobots];
        _Binputs = new boolean[_numRobots];
        _matchState = new MatchState(_matchId, _numRobots);
        _matchResult = new MatchResult(_numRobots);
        _matchHandler.SendFirstMatchState(_matchState);
        return true;
    }

    public void SetMatchLength(int seconds)
    {
        _timeLimit = (int)(seconds/DT);
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

        long clock, elapsedTime;

        while(_running)
        {
            clock = System.currentTimeMillis();
            Update();
            System.out.println(_clock * DT);
            elapsedTime = System.currentTimeMillis() - clock;

            if (DT_MS - elapsedTime > 0)
            {
                try { Thread.sleep(DT_MS - elapsedTime); //Keeps a constant update freq.
                } catch(InterruptedException e) {};
            }
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

            //Set fire = false to avoid strings of bullets.
            _robots[n].GetCurrentState().fire = false;
        }


//      3. Perform the actions created by the nodes of each robot.
        for (int n = 0; n < _numRobots; n++)
        {
            LinkedList<NodeAction> actions = _robots[n].GetActions();
            for (NodeAction a : actions)
                a.PerformAction(_robots[n], _projectileSystem);

            //System.out.println("Hot connections: " + _robots[n].GetHotConnections());
        }


//      4.1 Physics
        CalcCollisions();


//      4. Update the states of every robot, applying the impulses generated by the actions in the previous step.
        for (int n = 0; n < _numRobots; n++)
        {

            _robots[n].UpdateState();
        }






//      4.2 Update projectiles
        _projectileSystem.Update(_robots, _numRobots);


//      5. Create a MatchState object containing all information needed by the clients.
        CreateMatchState();


//      6. Send the match state to the clients via the match handler.
        _matchHandler.SendMatchState(_matchState);


//      7. Check if match has ended, if so call the MatchEnded() in match handler.
        float minHealth = _robots[0].GetCurrentState().health;
        int minHealthRobot = 0;
        for (int n = 1; n < _numRobots; n++)
            if (_robots[n].GetCurrentState().health < minHealth)
            {
                minHealth = _robots[n].GetCurrentState().health;
                minHealthRobot = n;
            }

        if (_clock > _timeLimit || minHealth <= 0)
        {
            if (_numRobots > 0)
            {
                if (_robots[0].GetCurrentState().health == _robots[1].GetCurrentState().health) _matchResult.winningTeam = 0; //Draw
                else if (minHealthRobot == 0) _matchResult.winningTeam = 2;
                else _matchResult.winningTeam = 1;
            }
            else _matchResult.winningTeam = 1;
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

            _context.A[n] = getInputA(n);
            _context.B[n] = getInputB(n);
        }


        //ToDo: Lägga till lista över projektiler?
    }


    private void CalcCollisions()
    {
        float sumOfRadi, dx, dy, distSq;
        Vector2 pos;

        for (int n = 0; n < _numRobots; n++)
        {

            //Collision with walls
            pos = _robots[n].GetCurrentState().pos;
            if (pos.x >  3) _robots[n].GetCurrentState().pos.x =  3;
            if (pos.x < -3) _robots[n].GetCurrentState().pos.x = -3;
            if (pos.y >  3) _robots[n].GetCurrentState().pos.y =  3;
            if (pos.y < -3) _robots[n].GetCurrentState().pos.y = -3;

            //Collision with other robots
            for (int m = n + 1; m < _numRobots; m++)
            {
                sumOfRadi = _robots[n].GetRadius() + _robots[m].GetRadius();
                dx = _robots[n].GetCurrentState().pos.x - _robots[m].GetCurrentState().pos.x;
                dy = _robots[n].GetCurrentState().pos.y - _robots[m].GetCurrentState().pos.y;

                distSq = dx * dx + dy * dy;

                if (distSq < sumOfRadi * sumOfRadi)
                {
                    //Add separating impulses.
                    float dist = (float)Math.sqrt((double)distSq);
                    dx /= dist;
                    dy /= dist;

                    _robots[n].GetCurrentState().pos.x += dx * 0.51f *(dist - sumOfRadi);
                    _robots[n].GetCurrentState().pos.y += dy * 0.51f *(dist - sumOfRadi);
                    _robots[m].GetCurrentState().pos.x -= dx * 0.51f *(dist - sumOfRadi);
                    _robots[m].GetCurrentState().pos.y -= dy * 0.51f *(dist - sumOfRadi);

                }
            }

        }

    }


    private boolean getInputB(int n) {
		if(_Binputs[n]){
			_Binputs[n]=false;
			return true;
		}
		return false;
	}

	private boolean getInputA(int n) {
		if(_Ainputs[n]){
			_Ainputs[n]=false;
			return true;
		}
		return false;
	}

	//Creates a MatchState object that contains the information that is to be sent to the players
    private void CreateMatchState()
    {

        for (int n = 0; n < _numRobots; n++)
            _matchState.robotStates[n] = _robots[n].GetCurrentState();
    }

	public void setA(int index) {
		_Ainputs[index]=true;	
	}
	public void setB(int index) {
		_Binputs[index]=true;	
	}
	public MatchState getMatchState(){
		CreateMatchState();
		return _matchState;
	}

}
