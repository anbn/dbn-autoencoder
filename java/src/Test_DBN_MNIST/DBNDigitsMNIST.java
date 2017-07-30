package Test_DBN_MNIST;

import Core.DBN;
import Core.Layer;
import Core.RBM;
import MNIST.MNISTReader;
import Tools.Log;

public class DBNDigitsMNIST {
	
	static DBN dbn;
	static double digits[][],testset[][];
	
	
	public static void main(String[] args) {
    	System.out.println("Launching Test_DBN_MNIST.DBNDigitsMNIST");
    	
    	if(args.length < 1){
    		System.out.println("Usage: <Path>");
    		System.exit(0);
    	}
    	
    	dbn = new DBN(new Layer(784), new Layer(625)){
    		@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double[][] input, double[][] testset){
    			Log.println("RBM("+rbm.vis.getCount()+","+rbm.hid.getCount()+"): Epoch "+epoch+": "+rbm.getError(input));
    			return epoch<2;
    		}
    	};
    	dbn.addLayer(new Layer(100));
    	dbn.addLayer(new Layer(50));
    	dbn.addLayer(new Layer(10));
    	
    	digits = new double[10000][];
		testset  = new double[100][]; 
    	MNISTReader.readMNIST(args[0], digits, testset);
    	
    	
    	System.out.println("Start training...");
    	dbn.train(digits, testset);
    	dbn.save("final.txt");
    	System.out.println("Training done.");
    	
    	//TODO gui
    }
}
