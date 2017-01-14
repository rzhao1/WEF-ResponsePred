package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

/**
 * Created by fadibotros on 2016-12-04.
 */
public class TaskReasoner {
    private State currentState;
    private HashMap<String,State> states;
    private boolean returningUser = true;  //whether the current user is a returning user or not
    private String startingStateId = "start_greeting";
//    private String startingStateId = "feedback_goal_elicitation";

    public TaskReasoner(String jsonFile){
        FSMReader fsmr = new FSMReader();
        this.states = fsmr.getStates(jsonFile);

        // this is the starting state
        this.currentState = states.get(startingStateId);
    }

    public void setCurrentState(String stateId){
        currentState = states.get(stateId);
    }

    public State getCurrentState(){
        return this.currentState;
    }

    /**
     * Returns the non internal children of a given state
     * @param currentState
     * @return
     */
    public   ArrayList<State> getNonInternalChildren(State currentState){
        ArrayList<State> children = new ArrayList<State>();

        boolean returningUserChoice = returningUserCheck(currentState);

        for(Transition tran : currentState.getNextTransitionsStates()){

            // returning user check
            if(returningUserChoice == true && this.returningUser != tran.isReturningUser())
                continue;

            if(tran.getState().getInternalValidation() == false){
                tran.getState().setScore(0.0);
                children.add(tran.getState());
            }
            else{
                children.addAll(  getNonInternalChildren(tran.getState())   );
            }
        }

        return children;
    }

    private ArrayList<State> getNonInternalChildren(Transition tran){
        ArrayList<State> children = new ArrayList<State>();

        if(tran.getState().getInternalValidation() == false){
            tran.getState().setScore(0.0);
            children.add(tran.getState());
        }
        else{
            children = getNonInternalChildren(tran.getState());
        }

        return children;
    }

    public ArrayList<State> getNextPotentialIntents() throws Exception{
        if (currentState == null){
            throw new  Exception("currentState is not set!");
        }

        return getNonInternalChildren(currentState);
    }


    private ArrayList<State> getNextPotentialIntents(ArrayList<NLUintent> nluIntents, ArrayList<Transition> transitions) throws Exception{
        ArrayList<State> states = new ArrayList<State>();

        boolean returningUserChoice = returningUserCheck(currentState);

        for(Transition t : transitions){
            ArrayList<State> tranStates = getNonInternalChildren(t);

            for(State st : tranStates) {

                // clone state so not to affect the original model
                State state = (State) st.clone();
                String fsmUserIntent = t.getIntention();

                // returning user check
                if (returningUserChoice == true && this.returningUser != t.isReturningUser())
                    continue;

                //initialize score to 0.0
                state.setScore(0.0);

                for (NLUintent nluIntent : nluIntents) {
                    if (intentMatch(nluIntent.getUserIntent(), fsmUserIntent) == true) {
                        //assign score based on the nluIntent that matches and has the highest confidence score
                        state.setScore(Math.max(state.getScore(), nluIntent.getScore()));
                    }
                }
                states.add(state);
            }
        }

        //sort states by score
        Collections.sort(states, new Comparator<State>() {
            @Override
            public int compare(State s1, State  s2) {
                if(s1.getScore() < s2.getScore())
                    return  1;
                else
                    return -1;
            }
        });

        return states;
    }

    /**
     * Most of the users intents in the FSM are the same as the ones that the NLU outputs,
     * however there are some "internal" intents ("1_goal", "match_any") that don't
     * @param nluUserIntent
     * @param fsmUserIntent
     * @return
     */
    private boolean intentMatch(String nluUserIntent, String fsmUserIntent){
        if(fsmUserIntent.compareTo(nluUserIntent) == 0 || fsmUserIntent.compareTo("match_any_intent") == 0){
            return true;
        }

        if(fsmUserIntent.compareTo("1_goal") == 0 && (
                nluUserIntent.compareTo("request_session_recommendation") == 0 ||
                nluUserIntent.compareTo("request_person_recommendation") == 0 ||
                nluUserIntent.compareTo("request_food_recommendation") == 0)){
            return true;
        }

        return false;
    }

    /**
     *  Returns a ranked list of next possible system intents given the output of NLU
     * @param nluIntents
     * @return
     * @throws Exception
     */
    public ArrayList<State> userInput(ArrayList<NLUintent> nluIntents) throws Exception{
        if (this.currentState == null){
            throw new  Exception("currentState is not set!");
        }

        //if the type of next intent is a system intent then we can't handle user input
        //simply return next potential intents
        if(this.currentState.getTypeNextIntent().compareTo("system_intent") == 0 ){
            return getNextPotentialIntents();
        }

        ArrayList<Transition> transitions = this.currentState.getNextTransitionsStates();

        return getNextPotentialIntents(nluIntents,transitions);
    }

    public boolean isReturningUser() {
        return returningUser;
    }

    public void setReturningUser(boolean returningUser) {
        this.returningUser = returningUser;
    }

    public void reset(){
        this.currentState = states.get(startingStateId);
    }

    /**
     * Returns true if the given state has a transition that is for returning users
     * @param st
     * @return
     */
    public boolean returningUserCheck(State st){

        for(Transition t : st.getNextTransitionsStates()){
            if(t.isReturningUser() == true)
                return true;
        }
        return false;
    }



    public static void main(String[] args) throws Exception {
        TaskReasoner tr = new TaskReasoner("fsm_2016Dec3_YM.json");
        tr.setCurrentState("feedback_goal_elicitation_1_goal");
        ArrayList<State> s = tr.getNextPotentialIntents();
        tr.getNextPotentialIntents();
    }

}
