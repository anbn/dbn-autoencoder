package Tools;

public class ArrayManipulation {
	public static double[][] deepClone(double array[][]){
		double[][] result = new double[array.length][];
		
		for(int i=0; i<array.length; i++)
			result[i] = array[i].clone();
		return result;
	}
	
    public static double[][] subArray(double input[][], int f, int t){
		double[][] res = new double[t][];
		
		for(int i=0; i<t; i++)
			res[i] = input[f+i].clone();
    	
    	return res;
	}
    
    public static double[] subArray(double input[], int f, int t){
		double[] res = new double[t];
		
		for(int i=0; i<t; i++)
			res[i] = input[f+i];
    	
    	return res;
	}
    
    public static double compareArrays(double a[], double b[]){
    	if(a.length!=b.length){
    		Log.println("ArrayManipulation.compareArrays: Dimension Error "+a.length+" "+b.length);
    		return 0;
    	}
    		
    	double res=0;
    	for(int i=0; i<a.length; i++)
    		res += Math.abs(a[i]-b[i]);
    	return (res/a.length);
    }
    
}
