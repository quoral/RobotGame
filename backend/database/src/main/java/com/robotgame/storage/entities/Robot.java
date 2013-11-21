package com.robotgame.storage.entities;


import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: KarlJohan
 * Date: 10/24/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "Robots")
@XmlRootElement
public class Robot implements EntityInterface{
    private String robotName;
    private User user;
    private int id;
    private String robotDesign;
    public Robot(String robotName, User user, String robotDesign){
        this.robotName = robotName;
        this.user = user;
        this.robotDesign = robotDesign;
    }

    public Robot() {
    }

    public Robot(Robot robot) {
        robotName = robot.getRobotName();
        user = robot.getUser();
        robotDesign = robot.getRobotDesign();
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(unique = true)
    @GenericGenerator(name="increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public void setId(int robotId) {
        this.id = robotId;
    }

    public String getRobotDesign() {
        return robotDesign;
    }

    public void setRobotDesign(String robotDesign) {
        this.robotDesign = robotDesign;
    }

    @Override
    public String toString() {
        return "Robot{" +
                "robotName='" + robotName + '\'' +
                ", user=" + user +
                ", robotId=" + id +
                ", robotDesign=" + robotDesign +
                '}';
    }

    @Override
    public Robot merge(JSONObject obj) throws JSONException {
        Robot merge = new Robot(this);
        if(obj.has("robotDesign")){
            merge.setRobotDesign(obj.getString("robotDesign"));
        }
        if(obj.has("robotName")){
            merge.setRobotName(obj.getString("robotName"));
        }
        return merge;  //To change body of implemented methods use File | Settings | File Templates.
    }
}