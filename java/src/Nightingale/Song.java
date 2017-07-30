package Nightingale;

import java.io.BufferedReader;
import java.io.FileReader;

import KMeans.IKMeansCapable;
import Tools.Log;

public class Song implements IKMeansCapable{
	public int width, height;
	public double data[];
	
	double min, max;
	int avg = 1;
	
	public String file="";
	public double[] code;
	
	
	public Song(String filename){
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
	       	
			file = new String(filename);
			min = 0;
			max = 0;
        	height = Integer.parseInt(in.readLine());
        	width = Integer.parseInt(in.readLine());
        	
        	data = new double[width*height];
        	
        	for(int i=0; i<data.length; i++){
    				data[i] = Double.parseDouble(in.readLine());
    				if(data[i]>max)
    					max = data[i];
    				if(data[i]<min)
    					min = data[i];
    		}				
    		in.close();
	    }catch (Exception e){
			 Log.println("Song.load: " + e.getMessage());
		}
	}
	
	public Song(int w, int h, double[] d){
		width = w;
		height = h;
		data = d;
	}
	
	public void normalize(){
		
		for(int i=0; i<data.length; i++){
			data[i] = Math.min(1, Math.log((data[i]-min)+1));
			
			if(data[i]>max)
				max = data[i];
		}
		
		//for(int i=0; i<data.length; i++)
		//	data[i] = (data[i]-min)/(max-min);
		
		
		min=0;
	}

	public void mix(Song s){
		//Log.println("Mixing "+width+"x"+height+" und "+s.width+"x"+s.height);
		
		if(s.width > width){
			double ndata[] = new double[s.width*s.height];
			
			for(int w=0; w<s.width; w++)
				for(int h=0; h<s.height; h++)
					ndata[s.width*h+w] = (s.data[s.width*h+w]*s.avg + (w<width ? data[width*h+w]*avg : 0))/(s.avg+avg);
			
			avg = s.avg+1;
			width = s.width;
			height = s.height;
			data = ndata;
			
		}else{ // width > s.width
			for(int w=0; w<width; w++)
				for(int h=0; h<height; h++)
					data[width*h+w] = (data[width*h+w]*avg + (w<s.width ? s.data[s.width*h+w]*s.avg : 0))/(avg+s.avg);
			avg++;
		}
	}

	
	private double getArea(int px, int py, int lw, int lh){
		double sum=0;
		
		for(int h=0; h<lh; h++)
			for(int w=0; w<lw; w++){
				sum += data[(py+h)*width + (px+w)]; //data[(py+h)*lw + (py+w)];
			}
					
		return (sum/(lw*lh));
	}
	
	public void scale(int nw, int nh){
		double ndata[] = new double[nw*nh];
		
		for(int h=0; h<nh; h++)
			for(int w=0; w<nw; w++){
				ndata[h*nw + w] = getArea(w*(width/nw),h*(height/nh), width/nw, height/nh);
			}
		width = nw;
		height = nh;
		data = ndata;
	}
	
	
	public int firstOccurence(double threshold){
		for(int w=0; w<width; w++)
			for(int h=0; h<height-10; h++)
				if(data[h*width + w]>threshold)
					return w;
		return 0;
	}
	
	public int lastOccurence(double threshold){
		for(int w=width-1; w>=0; w--)
			for(int h=0; h<height-10; h++)
				if(data[h*width + w]>threshold)
					return w;
		return 0;
	}
	
	public void crop(double threshold){
		int w1 = firstOccurence(threshold);
		int w2 = lastOccurence(threshold);
		
		double ndata[] = new double[(w2-w1+1)*height];
		
		for(int w=0; w<w2-w1+1; w++)
			for(int h=0; h<height; h++){
				ndata[(w2-w1+1)*h+w] = data[width*h+(w+w1)];
				//data[width*h+(w+w1)] +=0.1;
			}
			
		data = ndata;
		width = w2-w1+1;
	}
	
	public double[] toArray(){
		return data;
	}
	
	public void setDimension(int nw, int nh){
		double ndata[] = new double[nw*nh];
		
		for(int w=0; w<width; w++)
			for(int h=0; h<height; h++)
				ndata[nw*h+w] = data[width*h+w];
		
		width = nw;
		height = nh;
		data = ndata;
	}
	
	public Song clone(){
		return new Song(width, height, data.clone());
	}

	
	public void setCode(double[] c){
		code = c;
	}
	
	@Override
	public double[] getCode() {
		return code;
	}
}
