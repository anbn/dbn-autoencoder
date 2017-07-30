package Nightingale;

import java.io.File;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Tools.Log;
import Visual.ArrayDraw;



public class DisplayAverage extends JFrame{

	private static final long serialVersionUID = 1L;
	double song[];
	
	static Song avg;
	static LinkedList<Song> list = new LinkedList<Song>();
	
	static double scale = 0.2;
	
	
    public DisplayAverage(Song song) {
    	super();
    	
    	this.setLayout(null);
    	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(false);
        this.setLocationByPlatform(true);
        this.setSize((int) (song.width/scale+20), (int) (2*(song.height/scale)+30)+100);
        
        
        final ArrayDraw adMin = new ArrayDraw();
        //adMin.setBounds(10, 10, (int) (song.width/scale), (int) (song.height/scale));
        //adMin.drawArray(song.data, song.width, song.height);
        adMin.setEditable(true);
        getContentPane().add(adMin);
        
        
        final ArrayDraw adAvg = new ArrayDraw();
        adAvg.setBounds(10, 20 + (int) (avg.height/scale), (int)(avg.width/scale), (int)(avg.height/scale));
        adAvg.drawArray(avg.data, avg.width, avg.height);
        adAvg.setEditable(true);
        getContentPane().add(adAvg);  
        
        final JSlider sliderTest = new JSlider();
        sliderTest.setMinimum(0); 
        sliderTest.setMaximum(list.size()-1);
        sliderTest.setValue(0);
        sliderTest.setBounds(10,(int)(2*(song.height/scale)+30), 200, 20);
        sliderTest.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		Song s = list.get(sliderTest.getValue());
        		setTitle(s.file);
        		adMin.setBounds(10, 10, (int) Math.round(s.width/scale), (int) Math.round(s.height/scale));
                adMin.drawArray(s.data, s.width, s.height);
            }
        });
        getContentPane().add(sliderTest);
        
        setVisible(true);
    }
    
 
    public static boolean readFiles(String path, double threshold){
    	int count=0;
    	
    	Log.println("Reading files in "+path);
    	
    	File folder = new File(path);
    	File[] files = folder.listFiles();
        
        for (int i = 0; i < files.length; i++) {//files.length
        	if(files[i].isFile()){
        		String name = files[i].getName();
        		if(name.endsWith(".txt")){
        			Song song = new Song(path+name);
        			song.normalize();
                	song.scale((int)(song.width*scale), (int)(song.height*scale));
                    song.crop(threshold);
                    
                    if(avg==null)
                    	avg = song.clone();
                    else
                    	avg.mix(song);
                    
        			list.add(song);
        			count++;
        		}
        	}
        }
        Log.println("   "+count+" files");
    	return true;
    }
    
    public static void main(String[] args) {
    	Log.println("Launching Nightingale.DisplayAverage");
    	
    	if(args.length < 1){
    		System.out.println("Usage: <Path>");
    		System.exit(0);
    	}
    	
    	//readFiles(args[0]+"processed/A081/",0.25);
    	readFiles(args[0]+"processed/A093/", 0.25);
    	//readFiles(args[0]+"processed/A254/");

    	 
    	new DisplayAverage(avg);
    	
    	System.out.println("Average: "+avg.width+"x"+avg.height);
    	
    }
}