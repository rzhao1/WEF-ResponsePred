package model;

import java.io.FileReader;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Created by fadibotros on 2016-12-02.
 */
public class FSMReader {

    public HashMap<String,State> getStates(String filename){
        HashMap<String, State> statesMap = new HashMap<String,State>();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject statesObj = (JSONObject) jsonObject.get("states");
            Set<String> keys = statesObj.keySet();
            for(String key: keys){

                JSONObject stateObj = (JSONObject) statesObj.get(key);
                State s = new State(key,stateObj);
                statesMap.put(key,s);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        connectStates(statesMap);

        return statesMap;
    }

    private void connectStates(HashMap<String,State> stateHashMap){
        for(Map.Entry<String,State> entry  : stateHashMap.entrySet()){
            State currState = entry.getValue();

            for(Transition tran : currState.getNextTransitionsStates()){
                tran.setState(stateHashMap.get(tran.getStateId())  );
            }
        }
    }

    public static void main(String[] args) {
        FSMReader fr = new FSMReader();
        fr.getStates("central_model_fsm_davos.json");
    }
}
