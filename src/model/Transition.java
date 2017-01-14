package model;

/**
 * Created by fadibotros on 2016-12-02.
 */
public class Transition {
    private String intention;
    private String stateId;
    private State state;
    private boolean returningUser; //can only transition to this state if it's a returning user

    public Transition(String intention, String stateId, boolean returningUser) {
        this.intention = intention;
        this.stateId = stateId;
        this.returningUser = returningUser;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public boolean isReturningUser() {
        return returningUser;
    }

    public void setReturningUser(boolean returningUser) {
        this.returningUser = returningUser;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
