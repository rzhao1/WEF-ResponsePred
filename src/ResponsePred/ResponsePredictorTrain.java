package ResponsePred;

/**
 * @author RanZhao
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

import javax.xml.ws.Response;

import model.FSMReader;
import model.State;
import model.TaskReasoner;
import model.Transition;

public class ResponsePredictorTrain {

	public static HashMap<String, Integer> ngrams(int n, String str) {
		List<String> ngrams = new ArrayList<String>();
		HashMap<String, Integer> gramList = new HashMap<>();
		String[] words = str.split(" ");
		for (int i = 0; i < words.length - n + 1; i++) {
			String gram = concat(words, i, i + n);
			if (gramList.containsKey(gram)) {
				int count = gramList.get(gram);
				count++;
				gramList.put(gram, count);
			} else {
				gramList.put(gram, 1);
			}
			ngrams.add(concat(words, i, i + n));
		}
		return gramList;
	}

	public static String concat(String[] words, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++)
			sb.append((i > start ? " " : "") + words[i]);
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {

		int threshold = 1;

		FSMReader fr = new FSMReader();
		TaskReasoner TR= new TaskReasoner("central_model_fsm_davos.json");
		HashMap<String, State> stateInfo = new HashMap<>();
		stateInfo = fr.getStates("central_model_fsm_davos.json");
		HashMap<String, ArrayList<String>> nbestList = new HashMap<>();
		HashMap<String, Integer> turntaking= new HashMap<>();
		for (String stateName : stateInfo.keySet()) {
			String nextTypeIntent=stateInfo.get(stateName).getTypeNextIntent();
			if(nextTypeIntent.equals("user_intent")){
				turntaking.put(stateName, 0);
			}
			else{
				turntaking.put(stateName, 1);
			}
			ArrayList<String> listState = new ArrayList<>();
			ArrayList<State> pureStates=new ArrayList<>();
			ArrayList<Transition> transitions = stateInfo.get(stateName)
					.getNextTransitionsStates();
			for (int i = 0; i < transitions.size(); i++) {
				if(transitions.get(i).getStateId().equals("any")){
					continue;
				}
				pureStates.addAll(TR.getNonInternalChildren(transitions.get(i).getState()));
			}
			//Convert states to string
			for(int i=0;i<pureStates.size();i++){
				listState.add(pureStates.get(i).getStateId());
			}
			nbestList.put(stateName, listState);
		}

		String reponse = readFile("data.txt");
		for (int n = 2; n < 3; n++) {
			System.out.println("###################" + n + "-gram Result"
					+ "###################");
			HashMap<String, Integer> reponseGen = ngrams(n, reponse);
			for (String state : reponseGen.keySet()) {

				int times = reponseGen.get(state);
				if (times >= threshold) {
					String AggState[] = state.split(" ");
				
					if(!turntaking.containsKey(AggState[0])){
						turntaking.put(AggState[0], 2);
					}
					if (nbestList.containsKey(AggState[0])) {
						// Add the item to the list
						ArrayList<String> currentList = nbestList
								.get(AggState[0]);
						for (int i = 1; i < AggState.length; i++) {
							if (!listContain(currentList, AggState[i].trim())) {

							currentList.add(AggState[i].trim());
							}
						}
						nbestList.put(AggState[0], currentList);
					}
					
					else{
						ArrayList<String> currentList = new ArrayList<>();
						for (int i = 1; i < AggState.length; i++) {
							if (!listContain(currentList, AggState[i].trim())) {
								currentList.add(AggState[i].trim());
							}
							else{
								System.exit(1);
							}
						}
						nbestList.put(AggState[0], currentList);
					}

				}
			}
		}
		
		//Print result
		for(String state: nbestList.keySet()){
			System.out.println(state+" : "+nbestList.get(state));
		}
		
		FileOutputStream fos =
                new FileOutputStream("SysIntPredictor.ser");
             ObjectOutputStream oos = new ObjectOutputStream(fos);
             oos.writeObject(nbestList);
             oos.close();
             fos.close();
             System.out.printf("Serialized HashMap data is saved in SysIntPredictor.ser\n");
             
             FileOutputStream fos1 =
                     new FileOutputStream("Turntaking.ser");
                  ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
                  oos1.writeObject(turntaking);
                  oos1.close();
                  fos1.close();
                  System.out.printf("Serialized HashMap data is saved in Turntaking.ser");
                  

	}

	
	public static boolean listContain(ArrayList<String> list,String item){
		for(int i=0;i<list.size();i++){
			if(item.equals("finish_selfie")){
				System.out.println(list);
			}
			if(item.equals(list.get(i))){
				return true;
			}
		}
		return false;
	}
	public static String readFile(String filename) throws IOException {
		String csvFile = filename;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\t";
		String reponse = "";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] attribute = line.split(cvsSplitBy);
				if(attribute[1].equals("SARA")){
					reponse += attribute[3] + " ";
				}
			
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return reponse;

	}

}