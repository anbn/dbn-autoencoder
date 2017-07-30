package MNIST;

import java.io.RandomAccessFile;

public class MNISTReader {
	 
	RandomAccessFile f = null;
		
	int rows, cols;
	int count,pos;
	
		
	MNISTReader(String name){
		try{
			f = new RandomAccessFile(name, "r");
			if(f.readInt() != 2051){
				System.out.println("MNISTReader: Magic number not equal to 2051, no MNIST data found...");
				throw(new Exception());
			}
			count = f.readInt();
			rows = f.readInt();
			cols = f.readInt();
			
			System.out.println("MNISTReader: "+count+" images, "+rows+"*"+cols+"pixels");
			
		}catch(Exception e){
			System.out.println("MNISTReader: Error opening "+name);
		}
	}
	
	public double[] readImage(){
		double[] input = new double[rows*cols];
	
		try{
			for(int i=0; i<rows*cols; i++)
				input[i] = (f.readUnsignedByte()/255.0);
		}catch(Exception e){
			System.out.println("MNISTReader: Error reading file");
		}
		return input;
	}
	
	public void close(){
		try{
			f.close();
		}catch(Exception e){
			System.out.println("MNISTReader: Unable to close file.");
		}
	}
	
	public static boolean readMNIST(String path, double digits[][], double testset[][]){
		System.out.println("MNISTReader: Reading MNIST data...");
		
		MNISTReader mdb = new MNISTReader(path + "train-images-idx3-ubyte");
		MNISTReader mt = new MNISTReader(path + "t10k-images-idx3-ubyte");
		
		for(int i=0; i<digits.length; i++)
			digits[i] = mdb.readImage();
		for(int i=0; i<testset.length; i++)
			testset[i] = mt.readImage();
		
		System.out.println("MNISTReader: Done.\n   Trainimages: "+digits.length+"\n   Testimages:  "+testset.length);
		
		mt.close();
		mdb.close();
		return true;
	}
}
