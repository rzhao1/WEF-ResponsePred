package model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Created by fadibotros on 2016-12-02.
 */
public class State implements Cloneable{

    private String stateId;
    private String phase;
    private String systemUtterance;
    private String typeNextIntent;
    private Boolean internalValidation;
    private ArrayList<Transition> nextTransitionsStates;

    //score is only used to rank next possible states based on NLU ouput
    private Double score;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean getInternalValidation() {
        return internalValidation;
    }

    public void setInternalValidation(Boolean internalValidation) {
        this.internalValidation = internalValidation;
    }

    public State(String stateId, String phase, String systemUtterance, String typeNextIntent) {
        this.stateId = stateId;
        this.phase = phase;
        this.systemUtterance = systemUtterance;
        this.typeNextIntent = typeNextIntent;
        this.nextTransitionsStates = new ArrayList<Transition>();
        this.score = 0.0;
    }

    public State(String statedId, JSONObject obj) {
        this.stateId = statedId;
        this.nextTransitionsStates = new ArrayList<Transition>();
        try {

            JSONObject statesObj = (JSONObject) obj.get("states");
            this.phase = (String) obj.get("phase");
            this.typeNextIntent = (String) obj.get("typeNextIntent");
            this.internalValidation = (Boolean) obj.getOrDefault("internalValidation", false);

            JSONArray nextTranStates = (JSONArray) obj.get("nextTransitionsStates");
            Iterator<JSONObject> nextTranStatesIter = nextTranStates.iterator();

            while (nextTranStatesIter.hasNext()) {
                JSONObject transitionObj = (JSONObject) nextTranStatesIter.next();
                String stateId = (String) transitionObj.get("state");
                String intention = (String) transitionObj.get("intention");
                boolean returningUser = false;

                if(transitionObj.containsKey("returning")){
                    returningUser = (boolean)transitionObj.get("returning");
                }

                Transition newTransition = new Transition(intention,stateId,returningUser);
                this.nextTransitionsStates.add(newTransition);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getSystemUtterance() {
        return systemUtterance;
    }

    public void setSystemUtterance(String systemUtterance) {
        this.systemUtterance = systemUtterance;
    }

    public String getTypeNextIntent() {
        return typeNextIntent;
    }

    public void setTypeNextIntent(String typeNextIntent) {
        this.typeNextIntent = typeNextIntent;
    }

    public ArrayList<Transition> getNextTransitionsStates() {
        return nextTransitionsStates;
    }

    public void setNextTransitionsStates(ArrayList<Transition> nextTransitionsStates) {
        this.nextTransitionsStates = nextTransitionsStates;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

//    @Override
//    public int compareTo(State compareState) {
//        Double compareage=((State)compareState).getScore();
//        /* For Ascending order*/
////        return  this.score-compareage;
//        return 0;
//
//        /* For Descending order do like this */
//        //return compareage-this.studentage;
//    }
//
//    @Override
//    public String toString() {
//        return "";
//    }

//    public static void main(String[] args){
//        System.out.println("he");
//    }

}
