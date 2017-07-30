package KMeans;

class MyPoint implements IKMeansCapable{

	double coo[];
	
	MyPoint(double x, double y){
		coo = new double[2];
		coo[0] = x;
		coo[1] = y;
	}
	
	@Override
	public double[] getCode() {
		return coo;
	}
	
}