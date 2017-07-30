package Test_RBM_Digits;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageReader {
	
	public static double[] readImage(String name){ // read Image from File
		try{	
			BufferedImage bi = ImageIO.read(new File(name));
			 
			int [] rgbs = new int[bi.getWidth()*bi.getHeight()];
			double[] input = new double[bi.getWidth()*bi.getHeight()];
			double red,green,blue;
			
			bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), rgbs, 0, bi.getWidth());
			for(int i=0;i<rgbs.length;i++){
				 red	= Math.floor((Math.pow(256, 3) + rgbs[i]) / Math.pow(256, 2));
				 green	= Math.floor((Math.pow(256, 3) + rgbs[i]) / 256 % 256);
				 blue	= Math.floor((Math.pow(256, 3) + rgbs[i]) % 256);
				 input[i] = (((red+green+blue)/3)/255);
			}
			return input;
		}catch(Exception e){
			System.out.println("File "+name+" not found");
		}
		return null;
	}
}
