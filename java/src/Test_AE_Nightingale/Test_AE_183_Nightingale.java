package Test_AE_Nightingale;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Core.Autoencoder;
import Core.DBN;
import Core.Layer;
import Core.RBM;
import Nightingale.Song;
import Tools.Log;
import Visual.ArrayDraw;



public class Test_AE_183_Nightingale extends JFrame{

	private static final long serialVersionUID = 1L;
	double song[];
	
	static Song avg;
	static LinkedList<Song> lSongs = new LinkedList<Song>(),
							lTests = new LinkedList<Song>(),
							lGui = lTests;
	static int current=0;
	
	static double scale = 0.2;
	static Autoencoder ae;
	static Timer t;
	static boolean feedForward = true;
	
    public static void buildGUI() {
    	Log.println("Build GUI...");
    	
    	// Testdata //
    	
    	final JFrame fPad = new JFrame();
    	fPad.setLayout(null);
    	fPad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	fPad.setAlwaysOnTop(false);
    	fPad.setLocationByPlatform(true);
    	fPad.setSize((int) (avg.width/scale)+20, (int) (avg.height/scale)+40);
    	fPad.setResizable(false);
    	
    	final ArrayDraw adPad = new ArrayDraw();
    	adPad.setBounds(10, 10, (int)(avg.width/scale), (int)(avg.height/scale));
    	adPad.setEditable(true);
    	adPad.drawArray(avg.data, avg.width, avg.height);
		fPad.add(adPad);
    	
		// Autoencoder //
		
		final JFrame fAutoencoder = new JFrame();
    	fAutoencoder.setLayout(null);
    	fAutoencoder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	fAutoencoder.setAlwaysOnTop(false);
    	fAutoencoder.setLocationByPlatform(true);
    	fAutoencoder.setResizable(false);
    	
    	final ArrayDraw[] adLayer = new ArrayDraw[5];
    	
    	adLayer[0] = new ArrayDraw();
    	adLayer[0].setBounds(10, 10, (int)(avg.width/scale), (int)(avg.height/scale));
    	adLayer[0].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[0]);
    
		adLayer[1] = new ArrayDraw();
    	adLayer[1].setBounds(10, 10+adLayer[0].getY()+adLayer[0].getHeight(), 180, 180);
		adLayer[1].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[1]);
		
		adLayer[2] = new ArrayDraw();
    	adLayer[2].setBounds(10+adLayer[1].getX()+adLayer[1].getWidth(), 20+adLayer[0].getY()+adLayer[0].getHeight(), 160, 160);
		adLayer[2].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[2]);
		
		adLayer[3] = new ArrayDraw();
    	adLayer[3].setBounds(10+adLayer[2].getX()+adLayer[2].getWidth(), 10+adLayer[0].getY()+adLayer[0].getHeight(), 180, 180);
		adLayer[3].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[3]);
		
		adLayer[4] = new ArrayDraw();
    	adLayer[4].setBounds(10, 10+adLayer[1].getY()+adLayer[1].getHeight(), (int)(avg.width/scale), (int)(avg.height/scale));
		adLayer[4].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[4]);
    
		
		// Controls //
		
		final JFrame fControl = new JFrame();
		fControl.setLayout(null);
		fControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fControl.setAlwaysOnTop(false);
		fControl.setLocationByPlatform(true);
		fControl.setResizable(false);
		fControl.setSize(200, 250);
    	
		final JButton bRun = new JButton("Stop");
		final JRadioButton rAe = new JRadioButton("Forward");
		final JRadioButton rDbn = new JRadioButton("DBN");
        	
        final JSlider sSlider = new JSlider();
        final JLabel lLabel = new JLabel("1 / "+ (lGui.size()), SwingConstants.CENTER);
        final JButton bTransfer = new JButton("Transfer");
        final JRadioButton rTest = new JRadioButton("Testdata",true);
        final JRadioButton rTrain = new JRadioButton("Train");
        
		
		bRun.setBounds(10,10,180, bRun.getPreferredSize().height);
        bRun.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(t.isRunning()){
        			t.stop();
        			bRun.setText("Run");
        		}else{
        			t.start();
        			bRun.setText("Stop");
        		} 			
			}
        });     
        fControl.getContentPane().add(bRun);
        
        rAe.setBounds(10,40,90, bRun.getPreferredSize().height);
        rAe.setSelected(true);
        rAe.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		feedForward = true;
        		rAe.setSelected(true);
        		rDbn.setSelected(false);
        	}
        });     
        fControl.getContentPane().add(rAe);
        
        rDbn.setBounds(100,40,90, bRun.getPreferredSize().height);
        rDbn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		feedForward = false;
        		rAe.setSelected(false);
        		rDbn.setSelected(true);
        	}
        });     
        fControl.getContentPane().add(rDbn);
       
        
        JButton bLast = new JButton("<");
        bLast.setBounds(10, 75, 50, bLast.getPreferredSize().height);
        bLast.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = (lGui.size()+current-1)%lGui.size();
        		sSlider.setValue(current);
        		bTransfer.doClick();
        	}
        });
        fControl.getContentPane().add(bLast);
        
		lLabel.setBounds(70,80,60,lLabel.getPreferredSize().height);
        fControl.getContentPane().add(lLabel);
        
        JButton bNext = new JButton(">");
        bNext.setBounds(140, 75, 50, bNext.getPreferredSize().height);
        bNext.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = (current+1)%lGui.size();
        		sSlider.setValue(current);
        		bTransfer.doClick();
        	}
        });
        fControl.getContentPane().add(bNext);
		
        sSlider.setMinimum(0); 
        sSlider.setMaximum(lGui.size()-1);
        sSlider.setValue(0);
        sSlider.setBounds(10,105,180,20);
        sSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		current = sSlider.getValue();
        		lLabel.setText((current+1)+" / "+ (lGui.size()));
        		fPad.setTitle(lGui.get(current).file);
                adPad.drawArray(lGui.get(current).toArray(), avg.width, avg.height);
            }
        });
        fControl.getContentPane().add(sSlider);
        
		
        rTest.setBounds(10,130,90,rTest.getPreferredSize().height);
        rTest.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = 0;
        		lGui = lTests;
        		sSlider.setMaximum(lGui.size()-1);
        		sSlider.setValue(current);
        		lLabel.setText("1 / "+ (lGui.size()));
        		rTest.setSelected(true);
        		rTrain.setSelected(false);
        	}
        });
        fControl.getContentPane().add(rTest);
        
        
        rTrain.setBounds(100,130,90,rTrain.getPreferredSize().height);
        rTrain.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		current = 0;
        		lGui = lSongs;
        		sSlider.setMaximum(lGui.size()-1);
        		sSlider.setValue(current);
        		lLabel.setText("1 / "+ (lGui.size()));
        		rTrain.setSelected(true);
                rTest.setSelected(false);
        	}
        });
        fControl.getContentPane().add(rTrain);
         
        
        
        bTransfer.setBounds(10,160,90,bTransfer.getPreferredSize().height);
        bTransfer.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
        		Layer l = ae.getLayer(0);
        		for(int i=0; i<l.getCount(); i++)
        			l.value[i] = adPad.get(i);
        		adLayer[0].drawArray(l.value, avg.width, avg.height);
        	}
        });
        fControl.getContentPane().add(bTransfer);
        
        
		JButton bReset = new JButton("Reset");
		bReset.setBounds(100,160,90,bReset.getPreferredSize().height);
		bReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Layer l = ae.getLayer(0);
				for(int i=0; i<l.getCount(); i++)
					l.value[i] = 0;
				adPad.drawArray(l.value, avg.width, avg.height);
			}
		});
		fControl.getContentPane().add(bReset);
    
		
		/*JButton bSave = new JButton("Save");
        bSave.setBounds(10,190,90,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ae.save("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			System.out.println("Autoencoder saved.");
        	}
        });
        fControl.getContentPane().add(bSave);*/
         
        JButton bLoad = new JButton("Load");
        bLoad.setBounds(10,190,90,bLoad.getPreferredSize().height);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JFileChooser jFc = new JFileChooser();
        		if (jFc.showOpenDialog(fControl)==JFileChooser.APPROVE_OPTION) {
                    String filename = jFc.getSelectedFile().getAbsolutePath();
                    if(ae.load(filename)){
                    	fAutoencoder.setTitle(filename);
            			System.out.println("Autoencoder "+filename+" loaded.");
                    }
               }        		
        	}
        });
        fControl.getContentPane().add(bLoad);
        
        
        t = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(feedForward)
					ae.feedForward();
				else
					ae.reconstructForward();
				//dbn.getRBM(0).reconstructVisible();
				adLayer[0].drawArray(ae.getLayer(0).value, avg.width, avg.height);
				adLayer[1].drawArray(ae.getLayer(1).value, 18, 18);
				adLayer[2].drawArray(ae.getLayer(2).value, 3, 3);
				adLayer[3].drawArray(ae.getLayer(3).value, 18, 18);
				adLayer[4].drawArray(ae.getLayer(4).value, avg.width, avg.height);
			}
        });
        
        fAutoencoder.setSize((int) (avg.width/scale)+20, adLayer[4].getY()+adLayer[4].getHeight()+30);
    	fAutoencoder.setVisible(true);
        fControl.setVisible(true);
        fPad.setVisible(true);
        t.start();
        
        fPad.setLocation(new Point(fAutoencoder.getLocationOnScreen().x+fAutoencoder.getWidth()+10, fAutoencoder.getLocationOnScreen().y));
        fControl.setLocation(new Point(fAutoencoder.getLocationOnScreen().x+fPad.getWidth()+10, fPad.getLocationOnScreen().y+fPad.getHeight()+10));
		
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
    	Log.println("Launching Test_AE_Nightingale.Test_AE_183_Nightingale");
    	
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
    	
    	System.out.println("Data: Train "+lSongs.size()+", Test "+lTests.size()+", average dimension "+avg.width+"x"+avg.height);
    	
    	DBN dbn = new DBN(new Layer(avg.width*avg.height), new Layer(18*18)){
    		double besterror = 0;
    		double bestclone[][];
    		int badsteps = 0;
    		
    		double[][] deepClone(double array[][]){
    			double[][] result = new double[array.length][];
    			for(int i=0; i<array.length; i++)
    				result[i] = array[i].clone();
    			return result;
    		}
    		
    		@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double input[][], double testset[][]){
    			double etest, einput;
    			
    			if(epoch==0){
    				rbm.initializeTraining(input, rbm==rbmList.getFirst() ? 0.25 : 1);
    				besterror = 0;
    				badsteps = 0;	
    			}
    			
    			etest = rbm.getError(testset);
    			einput = rbm.getError(input);
    			
    			if(besterror==0 || etest < besterror){
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput +" \t (best)");
        			besterror = etest;
    				bestclone = deepClone(rbm.connection);
    				badsteps = 0;
    			}else{
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput);
    				badsteps++;	
    			}
    				
    			if(badsteps > 20){
    				rbm.connection = bestclone;
    				Log.println("Best " + rbm.getError(testset) +" \t "+ rbm.getError(input));
    				return false;
    			}
    			return true;
    		}
    	};
    	dbn.addLayer(new Layer(3*3));
    	//dbn.train(songs, tests);
    	//dbn.save("dbn.txt");
    	
    	ae  =  new Autoencoder(dbn){
    		double besterror = 0;
    		int badsteps = 0;
			
    		@Override
    		public boolean trainCallback(int epoch, double[][] input, double[][] testset){
    			double etest = getError(testset);
    			double einput = getError(input);
    			
    			if(besterror==0 || etest < besterror){
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput +" \t (best)");
    				besterror = etest;
    				badsteps=0;
    				save("_ae.txt");
    			}else{
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput);
    				badsteps++;
    			}
    			
    			return (badsteps < 10);	
    		};
    	};
    	ae.setLearningRate(0.0005);
    	//ae.train(songs, tests);
    	//ae.save("ae.txt");    	
    	
    	for(int i=1; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    	Log.println("Done.");
    	
    }
}