package com.robotgame.gameengine.Network;

import java.util.Vector;

import com.robotgame.gameengine.Match.IMatchHandler;
import com.robotgame.gameengine.Match.MatchResult;
import com.robotgame.gameengine.Robot.Builder.*;


public class matchHandler implements IMatchHandler {
	private static final int STANDBY = 0;
	private static final int RUNNING = 1;
	private int _matchState = STANDBY;
	private Vector<String> _expectedClients = new Vector<String>();
	private Vector<RobotBlueprint> _robots = new Vector<RobotBlueprint>();
	private Vector<matchSocket> _connectedClients = new Vector<matchSocket>();
	private String unpackingRoutine;
	private int clientsReady=0;
	public boolean join(matchSocket socket){
		for(String expected: _expectedClients ){
			if(socket.getUser().equals(expected)){
				_connectedClients.add(socket);
				return true;
			}
		}
		return false;
	}
	public void setExpectedParticipants(Vector<matchMakerHandler> match) {
		for(matchMakerHandler client : match){
			_expectedClients.add(client._user);
			_robots.add(client._robot);
		}
		
	}

	public void generateUnpackingRoutine() {
		// TODO Auto-generated method stub
		unpackingRoutine="dostuff";
		
	}
	public void startMatch(){
		sendToAll("Game on!");
	}
	public void sendToAll(String message){
		for(matchSocket s:_connectedClients){
			s._session.getRemote().sendStringByFuture(message);
		}
	}
	public synchronized void setReady(){
		if(_matchState==STANDBY){
			clientsReady++;
			if(clientsReady==_expectedClients.size()){
				boolean go=true;
				for(matchSocket m:_connectedClients){
					if(!m.ready){
						go=false;
					}
				}
				if(go){
					_matchState=RUNNING;
					startMatch();
				}else{
					clientsReady--;
				}
			}
		}
		
	}

    public void SendMatchState(MatchState matchState) {
    }

    public void MatchEnded(MatchResult matchResult) {
    }
}
