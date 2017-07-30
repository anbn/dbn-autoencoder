package KMeans;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JFrame;

import Tools.Log;
import Visual.Plotter;

public class KMeansTest {
	
	
	public static void main(String args[]){
		KMeans<MyPoint> km = new KMeans<MyPoint>(2);
		
		LinkedList<MyPoint> lPoints = new LinkedList<MyPoint>();
		/*lPoints.add(new double[] {0.2,0.8});
		lPoints.add(new double[] {0.8,0.2});
		lPoints.add(new double[] {0,2,0.8});
		lPoints.add(new double[] {0.8,0.8});*/
		
		for(int i=0; i<500; i++)
			lPoints.add(new MyPoint(Math.random()*0.2+0.2, Math.random()*0.2+0.7));
		
		for(int i=0; i<500; i++)
			lPoints.add(new MyPoint(Math.random()*0.5+0.4, Math.random()*0.2+0.4));
		
		for(int i=0; i<500; i++)
			lPoints.add(new MyPoint(Math.random()*0.5+0.1, Math.random()*0.3+0.1));
		
		for(int i=0; i<500; i++)
			lPoints.add(new MyPoint(Math.random(),Math.random()));
		
		
		JFrame f = new JFrame();
		f.setLayout(null);
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	f.setAlwaysOnTop(false);
    	f.setLocationByPlatform(true);
    	f.setSize(520,550);
    	
    	Plotter plotter = new Plotter(1,1);
    	plotter.setBounds(10, 10, 500, 500);
    	plotter.setBackground(Color.BLACK);
        f.add(plotter);
        f.setVisible(true);
    	
      
        LinkedList<KMeans<MyPoint>.Cluster> lCluster = km.run(100,lPoints);
        
        Log.println("\nCluster:");
        
        for(KMeans<MyPoint>.Cluster cl : lCluster){
			Color rgb = new Color((int)(Math.random()*155)+100, (int)(Math.random()*155)+100, (int)(Math.random()*155)+100);
			
	        Log.println((cl.mean[0]*1)/1.0+ " "+(cl.mean[1]*1)/1.0);
			plotter.addLine(cl.mean[0]+0.02,cl.mean[1]+0.02,cl.mean[0]-0.02,cl.mean[1]-0.02, rgb, 0);
			plotter.addLine(cl.mean[0]+0.02,cl.mean[1]-0.02,cl.mean[0]-0.02,cl.mean[1]+0.02, rgb, 0);
			 
			
			for(MyPoint p : cl.lPoints){
				plotter.addLine(p.coo[0]+0.001,p.coo[1]+0.001,p.coo[0]-0.001,p.coo[1]-0.001, rgb, 0);
				plotter.addLine(p.coo[0]+0.001,p.coo[1]-0.001,p.coo[0]-0.001,p.coo[1]+0.001, rgb, 0);
				//Log.println("\t"+p[0]+" "+p[1]);
			}
			
			
		}
		
		Log.println("Abstand:");
		for(KMeans<MyPoint>.Cluster cl : lCluster){
			double min = 1;
			Log.print("("+Math.round(cl.mean[0]*100)/100.0+","+Math.round(cl.mean[1]*100)/100.0+")");
			for(KMeans<MyPoint>.Cluster com : lCluster){
				double d = Math.round(com.getDifference(cl.mean)*100)/100.0;
				if(com != cl)
					min = Math.min(min, d);
				Log.print("\t"+ (com==cl ? "-":d));
			}
				
			Log.println("\t"+min);
		}
		f.repaint();
	}
}
