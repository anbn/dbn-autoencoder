package Test_RBM_Nightingale;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Core.Layer;
import Core.RBM;
import Nightingale.Song;
import Tools.Log;
import Visual.ArrayDraw;



public class Test_RBM_Nightingale extends JFrame{

	private static final long serialVersionUID = 1L;
	double song[];
	
	static Song avg;
	static LinkedList<Song> lSongs = new LinkedList<Song>(),
							lTests = new LinkedList<Song>(),
							lGui = lTests;
	static int current=0;
	
	static double scale = 0.2;
	static RBM rbm;
	static Timer t;
	
    public static void buildGUI() {
    	final JFrame frame = new JFrame();
    	
    	frame.setLayout(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setAlwaysOnTop(false);
    	frame.setLocationByPlatform(true);
    	frame.setSize((int) (avg.width/scale) + (int)(avg.height/scale)+30, (int) (avg.height/scale)*2+30+20);
    	frame.setResizable(false);
    	
    	final ArrayDraw adPad = new ArrayDraw();
    	adPad.setBounds(10, 20 + (int)(avg.height/scale), (int)(avg.width/scale), (int)(avg.height/scale));
    	adPad.setEditable(true);
    	adPad.drawArray(avg.data, avg.width, avg.height);
		frame.getContentPane().add(adPad);
    
		final ArrayDraw adVis = new ArrayDraw();
		adVis.setBounds(10, 10, (int)(avg.width/scale), (int)(avg.height/scale));
		adVis.setEditable(false);
		frame.getContentPane().add(adVis);
    
		final ArrayDraw adHid = new ArrayDraw();
		adHid.setBounds(20+(int)(avg.width/scale), 10, 250,250);
		adHid.setEditable(false);
		frame.getContentPane().add(adHid);
    
		
		// Controls //
		
		final JFrame control = new JFrame();
		control.setLayout(null);
		control.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		control.setAlwaysOnTop(false);
		control.setLocationByPlatform(true);
		control.setResizable(false);
		control.setSize(200, 250);
    	
		final JButton bRun = new JButton("Stop");
		final JButton bBackwards = new JButton("Backwards");
		final JButton bForwards = new JButton("Forwards");
        
		
        final JSlider sSlider = new JSlider();
        final JLabel lLabel = new JLabel("1 / "+ (lGui.size()), SwingConstants.CENTER);
        final JButton bTransfer = new JButton("Transfer");
        final JRadioButton cTest = new JRadioButton("Testdata",true);
        final JRadioButton cTrain = new JRadioButton("Train");
        
		
		bRun.setBounds(10,10,180, bRun.getPreferredSize().height);
        bRun.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(t.isRunning()){
        			t.stop();
        			adHid.setEditable(true);
        			bBackwards.setEnabled(true);
        			bForwards.setEnabled(true);
        			bRun.setText("Run");
        		}else{
        			t.start();
        			adHid.setEditable(false);
        			bBackwards.setEnabled(false);
        			bForwards.setEnabled(false);
            		bRun.setText("Stop");
        		} 			
			}
        });     
        control.getContentPane().add(bRun);
        
        bBackwards.setBounds(10,40,90, bRun.getPreferredSize().height);
        bBackwards.setEnabled(false);
        bBackwards.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for(int i=0; i<rbm.hid.getCount(); i++)
        			rbm.hid.value[i] = adHid.get(i);
        		rbm.updateInput();
        		adVis.drawArray(rbm.vis.value, avg.width, avg.height);
        	}
        });     
        control.getContentPane().add(bBackwards);
        
        bForwards.setBounds(100,40,90, bRun.getPreferredSize().height);
        bForwards.setEnabled(false);
        bForwards.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		rbm.updateFeatures();
        		adHid.drawArray(rbm.hid.value, 18,18);
        	}
        });     
        control.getContentPane().add(bForwards);
       
        
		lLabel.setBounds(10,80,90,lLabel.getPreferredSize().height);
        control.getContentPane().add(lLabel);
        
        JButton bNext = new JButton("Next");
        bNext.setBounds(100, 75, 90, bNext.getPreferredSize().height);
        bNext.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = (current+1)%lGui.size();
        		sSlider.setValue(current);
        		bTransfer.doClick();
        	}
        });
        control.getContentPane().add(bNext);
		
        sSlider.setMinimum(0); 
        sSlider.setMaximum(lGui.size()-1);
        sSlider.setValue(0);
        sSlider.setBounds(10,105,180,20);
        sSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		current = sSlider.getValue();
        		lLabel.setText((current+1)+" / "+ (lGui.size()));
        		frame.setTitle(lGui.get(current).file);
                adPad.drawArray(lGui.get(current).toArray(), avg.width, avg.height);
            }
        });
        control.getContentPane().add(sSlider);
        
		
        cTest.setBounds(10,130,90,cTest.getPreferredSize().height);
        cTest.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = 0;
        		lGui = lTests;
        		sSlider.setMaximum(lGui.size()-1);
        		sSlider.setValue(current);
        		lLabel.setText("1 / "+ (lGui.size()));
        		cTest.setSelected(true);
        		cTrain.setSelected(false);
        	}
        });
        control.getContentPane().add(cTest);
        
        
        cTrain.setBounds(100,130,90,cTrain.getPreferredSize().height);
        cTrain.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = 0;
        		lGui = lSongs;
        		sSlider.setMaximum(lGui.size()-1);
        		sSlider.setValue(current);
        		lLabel.setText("1 / "+ (lGui.size()));
        		cTrain.setSelected(true);
                cTest.setSelected(false);
        	}
        });
        control.getContentPane().add(cTrain);
         
        
        
        bTransfer.setBounds(10,160,90,bTransfer.getPreferredSize().height);
        bTransfer.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for(int i=0; i<rbm.vis.getCount(); i++)
        			rbm.vis.value[i] = adPad.get(i);
        		adVis.drawArray(rbm.vis.value, avg.width, avg.height);
        	}
        });
        control.getContentPane().add(bTransfer);
        
        
		JButton bReset = new JButton("Reset");
		bReset.setBounds(100,160,90,bReset.getPreferredSize().height);
		bReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0; i<rbm.vis.getCount(); i++)
					rbm.vis.value[i] = 0;
				adPad.drawArray(rbm.vis.value, avg.width, avg.height);
			}
		});
		control.getContentPane().add(bReset);
    
		
		JButton bSave = new JButton("Save RBM");
        bSave.setBounds(10,190,90,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rbm.save("/Users/anselmbrachmann/Desktop/rbm.txt"))
        			System.out.println("RBM saved.");
        	}
        });
        control.getContentPane().add(bSave);
         
        JButton bLoad = new JButton("Load RBM");
        bLoad.setBounds(100,190,90,bLoad.getPreferredSize().height);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rbm.load("/Users/anselmbrachmann/Desktop/rbm.txt"))
        			System.out.println("RBM loaded.");
        	}
        });
        control.getContentPane().add(bLoad);
        
        
        t = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				rbm.reconstructVisible();
				adVis.drawArray(rbm.vis.value, avg.width, avg.height);
				adHid.drawArray(rbm.hid.value, 18, 18);
			}
        });
        
        frame.setVisible(true);
        control.setVisible(true);
        t.start();
        
        Point frameLoc = frame.getLocationOnScreen();
        control.setLocation(new Point(frameLoc.x+frame.getWidth()+10, frameLoc.y));
		
    }
    
    
    public static boolean readFiles(String path, double threshold){
    	int count=0;
    	
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
                    
                    (count++<20 ? lSongs : lTests).add(song);
                 }
        	}
        }
        Log.println("Read directory "+path+", "+count+" files");
    	return true;
    }
    
 
    public static void main(String[] args) {
    	Log.println("Launching Test_RBM_Nightingale.Test_RBM_Nightingale");
    	
    	if(args.length < 1){
    		System.out.println("Usage: <Path>");
    		System.exit(0);
    	}
    	
    	readFiles(args[0]+"processed/A315/", 0.25);
    	readFiles(args[0]+"processed/A093/", 0.25);
    	readFiles(args[0]+"processed/A254/", 0.25);
    	readFiles(args[0]+"processed/A061/", 0.25);
    	readFiles(args[0]+"processed/A081/", 0.25);
    	
    	
    	final double[][] songs = new double[lSongs.size()][];
    	final double[][] tests = new double[lTests.size()][];
    	
    	int j=0;
    	for(Song song : lSongs){
    		song.setDimension(avg.width,avg.height);
    		songs[j++] = song.toArray();
    	}
    	j=0;
    	for(Song song : lTests){
    		song.setDimension(avg.width,avg.height);
    		tests[j++] = song.toArray();
    	}
    	
    	System.out.println("Train: "+lSongs.size()+", Test: "+lTests.size()+", average dimension: "+avg.width+"x"+avg.height);
    	
    	
    	
    	rbm = new RBM(new Layer(avg.width*avg.height), new Layer(18*18)){
    		@Override
    		public boolean trainCallback(int epoch, double[][] input, double[][] testset){
    			if(epoch==0)
    				rbm.initializeTraining(input, 0.25);
    			
    			Log.println("RBM("+rbm.vis.getCount()+","+rbm.hid.getCount()+"): Epoch "+epoch+": "+rbm.getError(input));
    			return epoch<40;
    		}
    	};
    	
    	rbm.initializeTraining(songs, 0.25);
    	rbm.train(songs, tests);
    	
    	for(int i=1; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    	
    }
}