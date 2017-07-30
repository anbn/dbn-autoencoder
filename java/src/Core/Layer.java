package Core;

public class Layer {
	private int count; // Number of Neurons exclusive bias
	public double	value[];
	
	public Layer(int n){
		if(n < 1)
			n = 1;
		count = n;
		value = new double[count+1];	// Bias
		value[count] = 1;				
	}
	
	public int getCount(){
		return count;
	}
	

	public double[] getSample(){	// Returns sample with states 0|1 and bias
		double[] res = new double[count+1];
		
		for(int i=0; i<count+1; i++)
			res[i] = Math.random() < value[i] ? 1 : 0;
		
		return res;
	}
	
	
	public double[] export(){
		double[] res = new double[count];
		
		for(int i=0; i<count; i++)
			res[i] = value[i];
		
		return res;
	}
}
