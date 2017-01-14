package ResponsePred;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.usc.ict.vhmsg.Config;
import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import edu.usc.ict.vhmsg.VHMsg;
import edu.usc.ict.vhmsg.main.VhmsgSender;

public class ResponsePredictionTest implements MessageListener {
	public static VHMsg vhmsgSubscriber;
	private VhmsgSender vhSysIntPredSender;
	public HashMap<String, ArrayList<String>> nBestList = null;
	public HashMap<String, Integer> turnTaking = null;
	public PrintWriter writer=null;

	public HashMap<String, ArrayList<String>> initializeNBestList(String filename)
			throws IOException, ClassNotFoundException {
		HashMap<String, ArrayList<String>> map = null;

		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		map = (HashMap) ois.readObject();
		for (String state : map.keySet()) {
			System.out.println(state + " : " + map.get(state));
		}
		ois.close();
		fis.close();
		return map;

	}

	public HashMap<String, Integer> initializeturnTaking(String filename) throws IOException, ClassNotFoundException {
		HashMap<String, Integer> map = null;

		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		map = (HashMap) ois.readObject();
		for (String state : map.keySet()) {
			System.out.println(state + " : " + map.get(state));
		}
		ois.close();
		fis.close();
		return map;

	}

	public void initializeVHMsg() throws IOException {

		String URL_file = "IP.txt";
		BufferedReader IP_br;
		String line;
		IP_br = new BufferedReader(new FileReader(URL_file));
		String[] IP_address = null;
		if ((line = IP_br.readLine()) != null) {

			IP_address = line.split(" ");

		}
		IP_br.close();

		Config.VHMSG_SERVER_URL = IP_address[0];
		System.out.println("IP Address: " + Config.VHMSG_SERVER_URL);
		// Must set system property because no vhsender
		System.setProperty("VHMSG_SERVER", Config.VHMSG_SERVER_URL);
		// initialize subscriber, all types of message
		vhmsgSubscriber = new VHMsg();
		vhmsgSubscriber.openConnection();
		vhmsgSubscriber.enableImmediateMethod();
		vhmsgSubscriber.addMessageListener(this);
		vhmsgSubscriber.subscribeMessage("vrTaskReasoner");
		vhmsgSubscriber.subscribeMessage("vrLogRecording");
		vhSysIntPredSender = new VhmsgSender("vrTaskReasoner");

	}

	@Override
	public void messageAction(MessageEvent e) throws IOException {
		// TODO Auto-generated method stub
		String message = e.toString();

		String[] tokens = message.split(" ");
		System.out.println(e);
		String result = "topintents ";

		if(tokens[0].equals("vrLogRecording")&&tokens[1].equals("start")){
			
		     writer = new PrintWriter("Database/"+(int)new Date().getTime()+".txt", "UTF-8");

		}
		
		if(tokens[0].equals("vrLogRecording")&&tokens[1].equals("stop")){
			System.out.println("Write task reasoner log file to database!");
			writer.close();
		}

		if (tokens[0].equals("vrTaskReasoner") && tokens[1].equals("set")) {
			if(writer!=null){
			    writer.println(tokens[3]);

			}

			if (nBestList.containsKey(tokens[3])) {

				// Output to JSON
				JSONArray sub_list = new JSONArray();
				ArrayList<String> predict_results = nBestList.get(tokens[3]);
				for (int i = 0; i < predict_results.size(); i++) {
					
					if(predict_results.get(i).toLowerCase().equals("any")||predict_results.get(i).contains("internal")||predict_results.get(i).toLowerCase().equals("woz")){
						continue;
					}
					JSONObject sub_obj = new JSONObject();
					sub_obj.put("score", turnTaking.get(tokens[2]));

					sub_obj.put("intent", predict_results.get(i));
					// result=predict_results.get(i)+" ";
					sub_list.add(sub_obj);

				}
				// result+=turnTaking.get(tokens[1]);
				vhSysIntPredSender.sendMessage(result + sub_list);
			}
		}

	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		String pred_serlized_file = "SysIntPredictor.ser";
		String turn_serlized_file = "Turntaking.ser";
		ResponsePredictionTest predictor = new ResponsePredictionTest();
		predictor.nBestList = predictor.initializeNBestList(pred_serlized_file);
		predictor.turnTaking = predictor.initializeturnTaking(turn_serlized_file);
		predictor.initializeVHMsg();
	}

}
