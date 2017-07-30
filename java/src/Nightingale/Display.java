package Nightingale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import Tools.Log;
import Visual.ArrayDraw;



public class Display extends JFrame{

	private static final long serialVersionUID = 1L;
	double song[];
	
	static Song avg;
	
	double scale = 0.2;
	
	
    public Display(final String path, final String file, Song song) {
    	super();
    	
    	int width, height;
    	
    	song.normalize();
    	Log.println("Song: "+song.width+"x"+song.height+ " - "+song.max+" "+song.min);
    	
    	width = song.width;
    	height = song.height;
    	
    	
    	this.setLayout(null);
    	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(false);
        this.setLocationByPlatform(true);
        this.setSize(song.width+20, 3*height+100);
        this.setTitle(file);
        
        JButton jbPlay = new JButton("Play");
        jbPlay.setBounds(10,10,100,jbPlay.getPreferredSize().height);
        jbPlay.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		try {
        			InputStream in = new FileInputStream(path+file.substring(0,file.length()-4));
        			AudioStream as = new AudioStream(in);
    				AudioPlayer.player.start(as);            
        		}catch(Exception ex){};		
        	}
        });
        getContentPane().add(jbPlay);
        
        ArrayDraw adOrig = new ArrayDraw();
        adOrig.setBounds(10, 40, width, height);
        adOrig.drawArray(song.data, song.width, height);
        adOrig.setEditable(true);
        getContentPane().add(adOrig);
        
        song.scale((int)(song.width*scale), (int)(song.height*scale));
        song.crop(0.25);
        
        
        
        
        ArrayDraw adMin = new ArrayDraw();
        adMin.setBounds(10, 50+height, (int) (song.width/scale), (int) (song.height/scale));
        adMin.drawArray(song.data, song.width, song.height);
        adMin.setEditable(true);
        getContentPane().add(adMin);
        
        
        if(avg==null)
        	avg = song.clone();
        else
        	avg.mix(song);
        
        ArrayDraw adCropped = new ArrayDraw();
        adCropped.setBounds(10, 60+2*height, (int)(avg.width/scale), (int)(avg.height/scale));
        adCropped.drawArray(avg.data, avg.width, avg.height);
        adCropped.setEditable(true);
        getContentPane().add(adCropped);  
        
        setVisible(true);
    }
    
 
    public static void main(String[] args) {
    	
    	String path = "/Users/anselmbrachmann/Documents/Uni/Semester6/Bachelor/data/Nightingale/processed/A093/";
    	File folder = new File(path);
    	File[] files = folder.listFiles();
    	
    	
    	for (int i = 0; i < files.length; i++) {//files.length
    		if(files[i].isFile()){
    			String name = files[i].getName();
    			if(name.endsWith(".txt")){
    				new Display(path, name, new Song(path+name));
    			}
    		}
    	}

    	/*
    	path = "/Users/anselmbrachmann/Documents/Uni/Semester6/Bachelor/data/Nightingale/processed/A093/";
    	folder = new File(path);
    	files = folder.listFiles(); 
    	for (int i = 0; i < 5; i++) {//files.length
    		if(files[i].isFile()){
    			String name = files[i].getName();
    			if(name.endsWith(".txt")){
    				new Display(path, name, new Song(path+name));
    			}
    		}
    	}
    	*/
    }
}