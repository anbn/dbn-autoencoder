package DataRandom;

import Tools.Log;

public class Generator {
	
	static double dataset[][] = {
		{1,0,0,1,
		 0,1,0,0,
		 0,0,1,0,
		 1,0,0,1},
		
		{0,1,0,0,
		 1,0,1,0,
		 0,1,0,1,
		 0,0,1,0},

		{0,1,0,0,
		 0,0,1,0,
		 0,1,1,0,
		 1,0,0,1},
			
		{1,0,0,0,
		 1,0,1,0,
		 0,1,0,1,
		 0,0,0,1},
		 
		{0,0,0,0,
		 1,1,0,1,
		 1,0,1,1,
		 0,0,0,0},
		 
		{0,0,0,1,
		 0,1,0,0,
		 1,1,1,0,
		 0,1,0,0},
		
		{0,0,1,0,
		 0,1,0,1,
		 0,1,0,1,
		 0,0,1,0},
		 
		{0,1,1,1,
		 0,0,0,0,
		 0,0,0,0,
		 1,1,1,0}
	};
	
	public static double getDifference(double[] code, double[] code2){
		double sum = 0;
		
		for(int i=0; i<16; i++)
			sum += (code[i]-code2[i])*(code[i]-code2[i]);
		return sum;
	}
	
	
	public static double[][] getData(int n){
		double[][] res = new double[n][];
		
		for(int i=0; i<n; i++)
			res[i] = dataset[i];
		
		return res;
	}
	
	public static void main(String args[]){
		for(int i=0; i<8; i++){
			Log.print(i+ " ");
			for(int j=0; j<8; j++){
				if(i==j)
					Log.print("\t-");
				else
					Log.print("\t"+getDifference(dataset[i],dataset[j]));
			}
			Log.println("");
		}		
	}

}
