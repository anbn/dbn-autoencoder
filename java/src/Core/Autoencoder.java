package Core;

import java.util.Iterator;
import java.util.LinkedList;

import Tools.Log;

public class Autoencoder extends DBN{

	private double learningRate = 0.01;
	
	public Autoencoder(DBN dbn) {
		super(dbn.rbmList);
		LinkedList<RBM> a = new LinkedList<RBM>();
		
		unroll(a, new Layer(rbmList.getFirst().vis.getCount()), rbmList.iterator());
		rbmList = a;
	}
	
	
	private Layer unroll(LinkedList<RBM> a, Layer topLayer, Iterator<RBM> itr){
		if(!itr.hasNext()){
			return topLayer;
		}
		RBM rbm = itr.next();
		
		Layer newLayer = new Layer(rbm.hid.getCount());
		
		a.add(rbm.clone(topLayer, newLayer));
		
		topLayer = unroll(a, newLayer, itr);
		newLayer = new Layer(rbm.vis.getCount());
		
		a.add(rbm.cloneReversed(topLayer, newLayer));
		return newLayer;
	}
	
	
	public double getError(double testset[][]){
		double error=0;
		
		for(int n=0; n<testset.length; n++){ // for every testcase
			
			Layer in = getLayer(0);
			for(int i=0; i<in.getCount(); i++)
				in.value[i] = testset[n][i];
		    	  
			reconstructForward();
			
			Layer out = getLayer(size());
			
			for(int i = 0; i<out.getCount(); ++i)
				error += Math.abs(out.value[i]-testset[n][i]);
		}
		return error/testset.length;
	}
	
	private void backpropagate(double output[]){
		
		double delta[][]= new double[size()][]; 
		for(int i = 1; i < size()+1; i++){
			delta[i-1]= new double[getLayer(i).getCount()];
		}
		
	
		Iterator<RBM> itr = rbmList.descendingIterator();
		RBM rbm = itr.next();
		int deltaindex = size()-1;
		
		for(int h = 0; h < rbm.hid.getCount(); h++){
			//error += 0.5*(output[h] - rbm.hid.value[h])*(output[h] - rbm.hid.value[h]);
			delta[deltaindex][h] = (output[h] - rbm.hid.value[h]) * rbm.hid.value[h] * (1 - rbm.hid.value[h]);
		}
		
		
		do{
			
			if(deltaindex == 0)
				break;
			
			for(int v=0; v<rbm.vis.getCount(); v++){
				double sum = 0;
				
				for(int h=0; h<rbm.hid.getCount(); h++){
					sum += delta[deltaindex][h] * rbm.connection[v][h];
					rbm.connection[v][h] += learningRate * rbm.vis.value[v] * delta[deltaindex][h];
				}
				
				if(deltaindex > 0)
					delta[deltaindex-1][v] = sum * rbm.vis.value[v] * (1 - rbm.vis.value[v]);   
			}
			for(int h = 0; h < rbm.hid.getCount(); h++)
		         rbm.connection[rbm.vis.getCount()][h] += learningRate * 1 * delta[deltaindex][h]; // BIAS
				
			deltaindex--;
			
		}while(itr.hasNext() && (rbm = itr.next())!=null);
		
		for(int v = 0; v<rbm.vis.getCount(); v++)
			for(int h = 0; h<rbm.hid.getCount(); h++)
		         rbm.connection[v][h] += output[v] * learningRate * delta[0][h];
		  
		for(int h = 0; h < rbm.hid.getCount(); h++)
			rbm.connection[rbm.vis.getCount()][h] += learningRate * delta[0][h]; // BIAS
	}
	
	public void setLearningRate(double r){
		learningRate = r;
	}
	
	@Override 
	public void train(double input[][], double testset[][]){
		Log.println("Autoencoder:  Training using Backpropagation, learningRate "+learningRate);
		Log.inc(3);
		
		boolean run = trainCallback(0, input, testset);
		for(int e=1; run; e++){
			for(int n=0; n<input.length; n++){
				Layer in = getLayer(0);
				for(int i=0; i<in.getCount(); i++)
					in.value[i] = input[n][i];
			    	  
				reconstructForward();
				backpropagate(input[n]);
			}
			run = trainCallback(e, input, testset);
		}
		Log.dec(3);
	}
	
	public boolean trainCallback(int epoch, double input[][], double testset[][]){
		Log.println("Warning: Autoencoder.trainCallback(int, double[][], double[][]) not overidden.");
		return false;
	}
	
}
