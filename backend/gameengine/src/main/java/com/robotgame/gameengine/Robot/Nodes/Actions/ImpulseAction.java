package com.robotgame.gameengine.Robot.Nodes.Actions;

import com.robotgame.gameengine.Robot.Nodes.NodeAction;
import com.robotgame.gameengine.Robot.Robot;
import com.robotgame.gameengine.Util.Vector2;

/**
 * Created with IntelliJ IDEA.
 * User: Oskar
 * Date: 2013-10-25
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */

public class ImpulseAction extends NodeAction
{
    private float _thrust, _relativeAngle;

    public ImpulseAction(float thrust, float angle)
    {
        _relativeAngle = angle;
        _thrust = thrust;
    }



    public void PerformAction(Robot robot)
    {
        robot.AddSpeed(_thrust / robot.GetMass() * robot.GetMaxThrust(), _relativeAngle);
    }


}