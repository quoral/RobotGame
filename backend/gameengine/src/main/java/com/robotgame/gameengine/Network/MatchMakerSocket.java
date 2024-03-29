package com.robotgame.gameengine.Network;

import java.io.IOException;

import com.robotgame.gameengine.Robot.testRobot;
import com.robotgame.gameengine.Robot.Builder.RobotBlueprint;
import com.robotgame.storage.entities.RobotEntity;
import com.robotgame.storage.services.RobotService;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
/**
 * Websocket for the lobby system. 
 * @author Rickard Grantzelius
 *
 */
@WebSocket
public class MatchMakerSocket {
	Session _session;
	String _user;
	RobotBlueprint _robot;
	@OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        _session=session;
        try {
            session.getRemote().sendString("Connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles incoming messages from the client. As it is now, the client sends only one type of message. Could be extended to implement a more dynamic lobby
     * @param message
     */
    @OnWebSocketMessage
    public void onMessage(String message) {
    	Gson g=new Gson();
    	System.out.println("Message: "+message);
    	joinRequest req=g.fromJson(message, joinRequest.class);
    	_robot = constructRobot(req.robotId);
    	_user = req.user;
      	MatchMaker.getInstance().queue(this, req.type);
    }
    
    /**
     * Method to fetch data from the database about the chosen robot, and from that construct a robot blueprint. The current implementation creates a dummy robot.
     * @param robotId
     * @return
     */
    private RobotBlueprint constructRobot(int robotId) {
		// koppla till databas och tolka dess JSON
    	RobotService service = new RobotService();
    	RobotEntity rob = service.getRobot(1, robotId);
    	testRobot test= new testRobot();
    	Gson g=new Gson();
    	System.out.println("design"+rob.getRobotDesign());
    	RobotBlueprint robot = g.fromJson(rob.getRobotDesign(), RobotBlueprint.class);
		return robot;
	}

    /**
     * Used for the interpretation of the clients json object.
     * @author Rickard Grantzelius
     *
     */
	private class joinRequest{
    	String user;
    	int type;
    	int robotId;
    	
    }

}
