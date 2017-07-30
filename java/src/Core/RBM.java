package Core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.Math;

import Tools.Log;


public class RBM {

	public double[][] connection;
	public Layer vis,hid;
	
	final double  epsilon = 0.3;
	final double weightcost = 0;
	
	
	
	public RBM(Layer vl, Layer hl) {
		vis = vl;
		hid = hl;
		
		connection = new double[vis.getCount()+1][];
		for(int i=0; i<vis.getCount()+1; i++){
			connection[i] = new double[hid.getCount()+1];
			for(int j=0; j<hid.getCount(); j++)
				connection[i][j] = 0.1 * Math.random() - 0.05;
		}
		
		for(int v=0; v<vis.getCount(); v++)	// Set Bias, wird später neu gesetzt in initializeTraining()
			connection[v][hid.getCount()] = 0;
		
		for(int h=0; h<hid.getCount(); h++)
			connection[vis.getCount()][h] = 0;
		
	}
	
	
	private void updateHidden(){			// new hidden state is a probability
		double sum;
		for(int h=0; h<hid.getCount(); h++){
			sum = 0;
			for(int v=0; v<vis.getCount()+1; v++)
				sum += vis.value[v] * connection[v][h];
			hid.value[h] = 1 / (1 + Math.exp(-sum));
		}
	}
		
	private void updateVisible(){			// new hidden state is a probability
		double sum;
		for(int v=0; v<vis.getCount(); v++){
			sum = 0;
			for(int h=0; h<hid.getCount()+1; h++)
				sum += hid.value[h] * connection[v][h];
			vis.value[v] = 1 / (1 + Math.exp(-sum));
		}
	}
	
	private void updateVisible(double sample[]){			// new hidden state is a probability
		double sum;
		for(int v=0; v<vis.getCount(); v++){
			sum = 0;
			for(int h=0; h<hid.getCount()+1; h++)
				sum += sample[h] * connection[v][h];
			vis.value[v] = 1 / (1 + Math.exp(-sum));
		}
	}
	
	/* Setzt die initialen Werte für Bias nach Hinton PGTTRB p.9
	 * 
	 * */
	public void initializeTraining(double input[][], double threshold){
		Log.println("RBM.initializeTraining(): threshold "+threshold);
		
		double p,proportion[] = new double[input[0].length];
		
		for(int n=0; n<input.length; n++)
			for(int i=0; i<input[n].length; i++)
				proportion[i] += (input[n][i] >= threshold? 1 : input[n][i]);
		
		
		for(int v=0; v<vis.getCount(); v++){
			p = proportion[v]/input.length;
			connection[v][hid.getCount()] = Math.log(p/(1-p));
		}
		
		for(int h=0; h<hid.getCount(); h++)
			connection[vis.getCount()][h] = 0;
	}
	
	private void randomizeData(double input[][]){
		int x,y;
		double temp[];
		
		for(int i=0; i<input.length; i++){	// Randomize data
			x = (int) Math.random()*input.length;
			y = (int) Math.random()*input.length;
			
			temp = input[x];
			input[x] = input[y];
			input[y] = temp;
		}
	}
	
	private void epoch(double input[][], double delta[][], double momentum){
		double posprod[][] = new double[vis.getCount()+1][hid.getCount()+1];
		double negprod[][] = new double[vis.getCount()+1][hid.getCount()+1];
		
		for(int n=0; n<input.length; n++){
			
			for(int v=0; v<vis.getCount()+1; v++)	// reset statt reallocate
				for(int h=0; h<hid.getCount()+1; h++){
					posprod[v][h]=0;
					negprod[v][h]=0;
				}
			
			for(int i=0; i<vis.getCount(); i++) // Set Input values
				vis.value[i] = input[n][i];
			
			updateHidden();
			for(int v=0; v<vis.getCount()+1; v++)
				for(int h=0; h<hid.getCount()+1; h++)
					posprod[v][h] += vis.value[v]*hid.value[h];
			
			
			//for(int i=0; i<5; i++){ // Gibbs sampling
			updateVisible(hid.getSample());
			updateHidden();
			//}
			
			for(int v=0; v<vis.getCount()+1; v++)
				for(int h=0; h<hid.getCount()+1; h++)
					negprod[v][h] += vis.value[v]*hid.value[h];
			
			for(int v=0; v<vis.getCount()+1; v++)
				for(int h=0; h<hid.getCount()+1; h++){
					connection[v][h] += epsilon * (posprod[v][h] - negprod[v][h]);
				}
		}
	}
	
	public double getError(double testset[][]){
		double error=0;
		
		for(int n=0; n<testset.length; n++){ // for every testcase
			
			for(int i=0; i<vis.getCount(); i++) // Set Input values
				vis.value[i] = testset[n][i];
			
			updateHidden();
			updateVisible();
			
			for(int i = 0; i<vis.getCount(); ++i)
				error += Math.abs(vis.value[i]-testset[n][i]);
		}
		return error/testset.length;
	}
	
	public void train(double input[][], double testset[][]){
		Log.println("RBM: Training RBM("+vis.getCount()+","+hid.getCount()+")...");
		Log.inc(3);
		
		if(testset==null || input==testset){
			Log.println("Warning: no testdata, using trainingdata.");
			testset = input;
		}
		
		Log.println("Training: "+input.length+", Testset: "+testset.length);
		
		if(input[0].length != vis.getCount() || testset[0].length != vis.getCount()){
			Log.println("Error: invalid dimensions of data, cancel.");
			Log.dec(3);
			return;
		}
		
		//initializeTraining(input, 0.25); To be done in trainCallback()
		boolean run = trainCallback(0, input, testset);
		
		for(int e=1; run; e++){
			randomizeData(input);
			
			epoch(input, null, 0);
			
			run = trainCallback(e, input, testset);
		}
		Log.dec(3);
	}
	
	public void reconstructVisible(){
		updateHidden();
		updateVisible(hid.getSample()); 
	}
	
	public void reconstructHidden(){
		updateVisible();
		updateHidden();
	}
	
	public void updateFeatures(){
		updateHidden();
	}
	
	public void updateInput(){
		updateVisible(hid.getSample());
	}
	
	
	
	public boolean save(BufferedWriter out){
		try {
		    out.write(vis.getCount()+"\n");
		    out.write(hid.getCount()+"\n");
		   
		    for(int i=0; i<vis.getCount()+1; i++)
				for(int j=0; j<hid.getCount()+(i==vis.getCount()? 0 : 1); j++)
					out.write(connection[i][j]+"\n");
					//out.write(connection[i][j]+"\t"+i+"\t"+j+"\n");
		
		    return true;
		} catch (Exception e) {
			Log.println("RBM.Save(BufferedWriter): Fehler: " + e.getMessage());
			return false;
		}
	}
	
	public boolean save(String name){
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(name));
		    save(out);
		    out.close();
		    return true;
		} catch (Exception e) {
			Log.println("RBM.Save(String) Fehler: " + e.getMessage());
			return false;
		}
	}
	
	
	public boolean load(BufferedReader in){
		try {
			String s;
			
        	vis = new Layer(Integer.parseInt(in.readLine()));
        	hid = new Layer(Integer.parseInt(in.readLine()));
        	
        	connection = new double[vis.getCount()+1][];
    		for(int i=0; i<vis.getCount()+1; i++){
    			connection[i] = new double[hid.getCount()+1];
    			for(int j=0; j<hid.getCount()+(i==vis.getCount()? 0 : 1); j++){
    				s = in.readLine();
    				connection[i][j] = Double.parseDouble(s);
    				//connection[i][j] = Double.parseDouble(s.substring(0, s.indexOf("\t")));
    			}				
    		}
    		return true;
		 }catch (Exception e){
			 Log.println("RBM.load(BufferedReader): Fehler: " + e.getMessage());
			 return false;
		 }
	}
	
	public boolean load(String name){
		try {
        	BufferedReader in = new BufferedReader(new FileReader(name));
            load(in);
            in.close();
    		return true;
		 }catch (Exception e){
			 Log.println("RBM.load(String): Fehler: " + e.getMessage());
			 return false;
		 }
	}
	
	public RBM clone(Layer vl, Layer hl){
		RBM res = new RBM(	vl!=null? vl : new Layer(vis.getCount()),
							hl!=null? hl : new Layer(hid.getCount()));
		
		for(int i=0; i<vis.getCount()+1; i++){
			for(int j=0; j<hid.getCount()+1; j++)
				res.connection[i][j] = connection[i][j];
		}
		return res;
	}
	
	public RBM cloneReversed(Layer vl, Layer hl){
		RBM res = new RBM(	vl!=null? vl : new Layer(hid.getCount()),
							hl!=null? hl : new Layer(vis.getCount()));

		for(int i=0; i<vis.getCount()+1; i++){
			for(int j=0; j<hid.getCount()+1; j++)
				res.connection[j][i] = connection[i][j];
		}
		return res;
	}
	
	
	public boolean trainCallback(int epoch, double input[][], double testset[][]){
		Log.println("Warning: RBM.trainCallback(int, double[][], double[][]) not overidden.");
		return false;
	}
}
