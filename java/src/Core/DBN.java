package Core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;

import Tools.Log;

public class DBN {
	
	public LinkedList<RBM> rbmList = new LinkedList<RBM>();
	
	public DBN(Layer vis, Layer hid){
		rbmList.add(new RBM(vis, hid){
			@Override
			public boolean trainCallback(int epoch, double input[][], double testset[][]){
				return RBMTrainCallback(this, epoch, input, testset);
			}
		});
	}
	
	protected DBN(LinkedList<RBM> rl){
		rbmList = rl;
	}
	
	public void addLayer(Layer l){
		rbmList.addLast(new RBM(rbmList.getLast().hid, l){
			@Override
			public boolean trainCallback(int epoch, double input[][], double testset[][]){
				return RBMTrainCallback(this, epoch, input, testset);
			}
		});
	}
	
	
	public LinkedList<Layer> getLayerList(){
		LinkedList<Layer> res = new LinkedList<Layer>();
		
		res.add(rbmList.getFirst().vis);
		for(RBM rbm : rbmList)
			res.add(rbm.hid);
		return res;
	}
	
	public void train(double input[][], double testset[][]){
		Log.println("DBN: Training using Contrastive Divergence");
				
		for(RBM rbm : rbmList){
			Log.inc(3);
			rbm.train(input, testset);	// TODO Testset einfügen???
			Log.dec(3);
			
			if(rbm!=rbmList.getLast()){
				Log.println("DBN: Generating data for next RBM...");
				double newInput[][] = new double[input.length][];
				double newTestset[][] = new double[testset.length][];
				
			
				for(int n=0; n<input.length; n++){	// generate next Input for next RBM
					for(int i=0; i<rbm.vis.getCount(); i++)
						rbm.vis.value[i] = input[n][i];
				
					rbm.updateFeatures();
					newInput[n] = rbm.hid.export();
				}
				
				for(int n=0; n<testset.length; n++){	// generate next Input for next RBM
					for(int i=0; i<rbm.vis.getCount(); i++)
						rbm.vis.value[i] = testset[n][i];
				
					rbm.updateFeatures();
					newTestset[n] = rbm.hid.export();
				}
				input = newInput;
				testset = newTestset;
			}
		}
	}
	
	public void feedForward(){
		for(RBM rbm : rbmList)
			rbm.updateFeatures();
	}
	
	public void reconstructForward(){
		for(RBM rbm : rbmList)
			rbm.reconstructVisible();
	}
	
	public void reconstructBackward(){
		Iterator<RBM> itr = rbmList.descendingIterator();
		RBM rbm;
		
		while(itr.hasNext()){
			rbm = itr.next();
			rbm.reconstructHidden();
		}
	}
	
	public boolean save(String name){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(name));
		    out.write(rbmList.size()+"\n");
		   
		    for(RBM rbm : rbmList)
		    	rbm.save(out);
		    
		    out.close();
		    return true;
		} catch (Exception e) {
			Log.println("DBN.save(String): " + e.getMessage());
			return false;
		}
	}
	
	public boolean load(String name){	
		try {
			BufferedReader in = new BufferedReader(new FileReader(name));
        	
			rbmList.clear();
			int depth = Integer.parseInt(in.readLine());
        	
        	for(int i=0; i<depth; i++){
        		RBM rbm = new RBM(new Layer(1), new Layer(1));
        		rbm.load(in);
        		if(i > 0)
        			rbm.vis = rbmList.getLast().hid;
        		rbmList.add(rbm);
        	}	
        	in.close();
    		return true;
		 }catch (Exception e){
			 Log.println("DBN.load(String): " + e.getMessage());
			 return false;
		 }
	}
	
	public RBM getRBM(int n){
		return rbmList.get(n);
	}
	
	//Liefert Layer Nummer n beginnend
	public Layer getLayer(int n){
		return (n==0 ? rbmList.getFirst().vis : rbmList.get(n-1).hid);
	}
	
	//	Liefert Anzahl der RBMs (size()+1 == Anzahl der Layer)
	public int size(){
		return rbmList.size();
	}
	
	// returns Average Features on layer n given input
	public double[] getLayerAverage(int k, double input[][]){
		
		Layer	in = getLayer(0),
				l = getLayer(k);
				
		double avg[] = new double[l.getCount()];
		
		for(int n=0; n<input.length; n++){
			for(int i=0; i<in.getCount(); i++)
				in.value[i] = input[n][i];
		    	  
			reconstructForward();
			
			for(int i=0; i<l.getCount(); i++)
				avg[i]+=l.value[i];
		}
		
		for(int i=0; i<l.getCount(); i++)
			avg[i]/=input.length;
		return avg;
	}
	
	
	
	public boolean RBMTrainCallback(RBM rbm, int epoch, double input[][], double testset[][]){
		Log.println("Warning: DBN.RBMTrainCallback(...) not overidden.");
		return false;
	}
	
}
