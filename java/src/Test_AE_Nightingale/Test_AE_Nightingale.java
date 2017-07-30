package Test_AE_Nightingale;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Core.Autoencoder;
import Core.DBN;
import Core.Layer;
import Core.RBM;
import KMeans.KMeans;
import Nightingale.Song;
import Tools.ArrayManipulation;
import Tools.Log;
import Visual.ArrayDraw;



public class Test_AE_Nightingale extends JFrame{

	private static final long serialVersionUID = 1L;
	double song[];
	
	static  double[][] songs;
	static  double[][] tests;
	
	
	static Song avg;
	static LinkedList<Song> lSongs = new LinkedList<Song>(),
							lTests = new LinkedList<Song>(),
							lGui = lTests;
	static int current = 0;
	static double scale = 0.2;
	static int bottleneck = 2;
	
	static Autoencoder ae;
	static Timer t;
	static boolean feedForward = true;
	
	static double codes[][];
	
	static String aeName="";
	
	static int architecture[];
	
	public static LinkedList<JFrame> buildKMeans(int n){
		Log.print("KMeans ("+n+")");
        
		LinkedList<JFrame> lFrames = new LinkedList<JFrame>();
		Song avgSong = null;
		
    	/*final JTextField jeClasses = new JTextField("5");
    	jeClasses.setBounds(10,10,50,jeClasses.getPreferredSize().height);
    	fKMeans.getContentPane().add(jeClasses);*/
		
		
    	for(Song s : lSongs){
            Layer in = ae.getLayer(0);
            for(int i=0; i<s.data.length; i++)
            	in.value[i] = s.data[i];
            ae.feedForward();
            s.setCode(ArrayManipulation.subArray(ae.getLayer(bottleneck).value,0, ae.getLayer(bottleneck).getCount()));
       	}
    	
    	int dim = ae.getLayer(bottleneck).getCount();
    	
    	/*LinkedList<Song> twoStroph = new LinkedList<Song>();
    	for(int i=60; i<100; i++)
    		twoStroph.add(lSongs.get(i));*/
    	
    	final LinkedList<KMeans<Song>.Cluster> lCluster = (new KMeans<Song>(dim)).run(n, lSongs);
    	Log.println("");
        
    	for(KMeans<Song>.Cluster cl : lCluster){
        	double min = 4;
        	if(cl.lPoints.size()!=0){
        		Log.print("("+cl.lPoints.size()+")\t");
        		String clname="";
        		for(int i=0; i<dim; i++)
        			clname += Math.round(cl.mean[i]);
        				
        		Log.print(clname);
            	for(KMeans<Song>.Cluster com : lCluster){
            		if(com.lPoints.size()!=0){
            			double d = Math.round(com.getDifference(cl.mean)*100)/100.0;
              				if(com != cl)
               					min = Math.min(min, d);
               				Log.print("\t"+ (com==cl ? "-":d));
            			}
            	}
            	Log.println("\t"+min);	
            	
            	JFrame fKMeansClass = new JFrame();
            	fKMeansClass.setLayout(null);
            	fKMeansClass.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            	fKMeansClass.setAlwaysOnTop(false);
            	fKMeansClass.setLocationByPlatform(true);
            	fKMeansClass.setResizable(true);
            	fKMeansClass.setTitle("Cluster "+clname);
            	
            	ArrayDraw[] adSongs = new ArrayDraw[100]; 
            	int i=0;
            	avgSong = null;
            	for(Song s : cl.lPoints){
            		
            		if(clname.contains("0101111111001111"))
            			Log.println(s.file);
            		
            		adSongs[i] = new ArrayDraw();
            		adSongs[i].setBounds(10+(int)(s.width+10)*(i%5), 10+(int)(s.height+10)*(i/5), (int)(s.width), (int)(s.height));
            		adSongs[i].setEditable(false);
            		adSongs[i].drawArray(s.data, s.width, s.height);
            		fKMeansClass.add(adSongs[i]);
            		
            		if(avgSong==null)
                    	avgSong = s.clone();
                    else
                    	avgSong.mix(s);
            		i++;
            		
            	}
            	adSongs[i] = new ArrayDraw();
        		adSongs[i].setBounds(10+(int)(avgSong.width+10)*(i%5), 10+(int)(avgSong.height+10)*(i/5), (int)(avgSong.width), (int)(avgSong.height));
        		adSongs[i].setEditable(false);
        		
        		adSongs[i].drawArray(avgSong.data, avgSong.width, avgSong.height);
        		adSongs[i].setEditable(true);
        		fKMeansClass.add(adSongs[i]);
        		i++;
        		
            	fKMeansClass.setSize(10+(int)(avg.width+10)*5, 30+(int)(avg.height+10)*((i+4)/5));
            	fKMeansClass.setVisible(true);
            	lFrames.add(fKMeansClass);
        	}
     	}
        
        Log.println(lFrames.size()+" different clusters");
        return lFrames;
    }
    
	
    public static void buildGUI() {
    	Log.print("Build GUI... ");
    	
    	architecture = new int[ae.getLayerList().size()];
    	for(int i=0; i<architecture.length; i++){
    		architecture[i] = ae.getLayer(i).getCount();
    		Log.print(architecture[i]+" ");
    	}
    	bottleneck = Math.round(architecture.length/2);
		
    	Log.print("- bottleneck: "+bottleneck+"\n");
    	
    	
    	// Testdata //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	
    	final JFrame fPad = new JFrame();
    	fPad.setLayout(null);
    	fPad.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	fPad.setAlwaysOnTop(false);
    	fPad.setLocationByPlatform(true);
    	fPad.setSize((int) (avg.width/scale)+20, (int) (avg.height/scale)+40);
    	fPad.setResizable(true);
    	
    	final ArrayDraw adPad = new ArrayDraw();
    	adPad.setBounds(10, 10, (int)(avg.width/scale), (int)(avg.height/scale));
    	adPad.setEditable(true);
    	adPad.drawArray(avg.data, avg.width, avg.height);
		fPad.add(adPad);
    	
		// Autoencoder //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		final JFrame fAutoencoder = new JFrame();
    	fAutoencoder.setLayout(null);
    	fAutoencoder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	fAutoencoder.setAlwaysOnTop(false);
    	fAutoencoder.setLocationByPlatform(true);
    	fAutoencoder.setResizable(true);
    	fAutoencoder.setTitle(aeName);
    	
    	final ArrayDraw[] adLayer = new ArrayDraw[architecture.length];
    	
    	adLayer[0] = new ArrayDraw();
    	adLayer[0].setBounds(10, 10, (int)(avg.width/scale), (int)(avg.height/scale));
    	adLayer[0].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[0]);
    
		for(int i=1, b=10; i<architecture.length-1; i++){
			adLayer[i] = new ArrayDraw();
	    	adLayer[i].setBounds(b, 10+adLayer[0].getY()+adLayer[0].getHeight(), (int) Math.sqrt(architecture[i])*10, (int) Math.sqrt(architecture[i])*10);
			adLayer[i].setEditable(false);
			fAutoencoder.getContentPane().add(adLayer[i]);
			b = 10 + adLayer[i].getX()+adLayer[i].getWidth();
		}
		
		adLayer[architecture.length-1] = new ArrayDraw();
    	adLayer[architecture.length-1].setBounds(10, 10+adLayer[1].getY()+adLayer[1].getHeight(), (int)(avg.width/scale), (int)(avg.height/scale));
		adLayer[architecture.length-1].setEditable(false);
		fAutoencoder.getContentPane().add(adLayer[architecture.length-1]);
    
		// Average //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final JFrame fCodes = new JFrame();
    	fCodes.setLayout(null);
    	fCodes.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	fCodes.setAlwaysOnTop(false);
    	fCodes.setLocationByPlatform(true);
    	fCodes.setSize(260,300);
    	fCodes.setResizable(true);
    	fCodes.setTitle("Average Codes: Labeled");
    	
    	
    	final ArrayDraw adAvg[] = new ArrayDraw[5];
    	final JLabel lAvg[] = new JLabel[5];
    	codes = new double[5][];
    	for(int i=0; i<adAvg.length; i++){
    		codes[i] = ae.getLayerAverage(bottleneck, ArrayManipulation.subArray(songs, i*20, 20));
    		adAvg[i] = new ArrayDraw();
    		adAvg[i].setEditable(false);
    		adAvg[i].setBounds(10,10+(int) (Math.sqrt(architecture[bottleneck])*10+10)*i, (int) Math.sqrt(architecture[bottleneck])*10, (int) Math.sqrt(architecture[bottleneck])*10);
        	adAvg[i].drawArray(codes[i], (int)Math.sqrt(architecture[bottleneck]), (int)Math.sqrt(architecture[bottleneck]));
        	fCodes.add(adAvg[i]);
        	
        	lAvg[i] = new JLabel("bla "+i);
        	lAvg[i].setBounds(60, 10+(int) (Math.sqrt(architecture[bottleneck])*10+10)*i, 200, (int) Math.sqrt(architecture[bottleneck])*10);
        	lAvg[i].setForeground(Color.WHITE);
        	lAvg[i].setBackground(Color.BLACK);
			lAvg[i].setOpaque(true);
			fCodes.add(lAvg[i]);
    	}
    	
    	
    	// KMeans //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
    	final LinkedList<JFrame> lFrames = new LinkedList<JFrame>();
    	
		// Controls //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
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
		final JButton bLoad = new JButton("Load...");
        	
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
        			bLoad.setEnabled(true);
        			bRun.setText("Run");
        		}else{
        			t.start();
        			bLoad.setEnabled(false);
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
   
		bLoad.setBounds(10,190,90,bLoad.getPreferredSize().height);
        bLoad.setEnabled(false);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JFileChooser jFc = new JFileChooser();
        		if (jFc.showOpenDialog(fControl)==JFileChooser.APPROVE_OPTION) {
                    String filename = jFc.getSelectedFile().getAbsolutePath();
                    if(ae.load(filename)){
                    	fAutoencoder.dispose();
                    	fPad.dispose();
                    	fControl.dispose();
                    	fCodes.dispose();
                    	for(JFrame f : lFrames)
                			f.dispose();
                    	aeName = jFc.getSelectedFile().getName();
                    	buildGUI();
                    	System.out.println("Autoencoder "+filename+" loaded.");
                    }
               }        		
        	}
        });
        fControl.getContentPane().add(bLoad);
        
        
        final JTextField jeClasses = new JTextField("5");
    	jeClasses.setBounds(100,190,40,jeClasses.getPreferredSize().height);
    	//jeClasses.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
    	fControl.getContentPane().add(jeClasses);
    	
        JButton bKMeans = new JButton("KMeans");
        bKMeans.setBounds(140,190,50,bLoad.getPreferredSize().height);
        bKMeans.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for(JFrame f : lFrames)
        			f.dispose();
        		lFrames.clear();
        		lFrames.addAll(buildKMeans(new Integer(jeClasses.getText())));
        	}
        });
        fControl.getContentPane().add(bKMeans);
        
        
        t = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(feedForward)
					ae.feedForward();
				else
					ae.reconstructForward();
				
				adLayer[0].drawArray(ae.getLayer(0).value, avg.width, avg.height);
				
				for(int i=1; i<architecture.length-1; i++){
					adLayer[i].drawArray(ae.getLayer(i).value,(int) Math.sqrt(architecture[i]), (int) Math.sqrt(architecture[i]));
				}
				
				adLayer[architecture.length-1].drawArray(ae.getLayer(architecture.length-1).value, avg.width, avg.height);
				
				for(int i=0; i<5; i++){
					double comp = ArrayManipulation.compareArrays(codes[i], ArrayManipulation.subArray(ae.getLayer(bottleneck).value,0, ae.getLayer(bottleneck).getCount()))*100;
					lAvg[i].setBounds(60, 10+(int) (Math.sqrt(architecture[bottleneck])*10+10)*i, (int) (200-2*comp), (int) Math.sqrt(architecture[bottleneck])*10);
					lAvg[i].setText(" "+Math.round(comp));
				}
			}
        });
        
        fAutoencoder.setSize(Math.max((int) (avg.width/scale), adLayer[architecture.length-2].getX()+adLayer[architecture.length-2].getWidth())+20, adLayer[architecture.length-1].getY()+adLayer[architecture.length-1].getHeight()+30);
    	
        fAutoencoder.setVisible(true);
        fControl.setVisible(true);
        fPad.setVisible(true);
    	fCodes.setVisible(true);
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
    
    public static boolean inArgs(String args[], String s){
    	for(int i=2; i<args.length; i++){
    		if(args[i].equals(s))
    			return true;
    	}
    	return false;
    }
    
    public static LinkedList<Integer> getTopology(String arg){
    	LinkedList<Integer> l = new LinkedList<Integer>();
    	
    	StringTokenizer st = new StringTokenizer(arg.replaceAll(":", " "));
    	while(st.hasMoreTokens()){
    		l.add(new Integer(st.nextToken()));
    	}
    	return l;
    }
 
    public static void main(String[] args) {
    	Log.println("Launching Test_AE_Nightingale.Test_AE_Nightingale");
    	Log.print("Arguments:");
    	for(int i=0; i<args.length; i++)
    		Log.print(" "+args[i]);
    	Log.print("\n");
    	
    	if(args.length < 2){
    		System.out.println("Usage: <Path> <topology>");
    		System.exit(0);
    	}
    	
    	LinkedList<Integer> topology = getTopology(args[1]);
    	
    	readFiles(args[0]+"processed/A315/", 0.25);
    	readFiles(args[0]+"processed/A093/", 0.25);
    	readFiles(args[0]+"processed/A254/", 0.25);
    	readFiles(args[0]+"processed/A061/", 0.25);
    	readFiles(args[0]+"processed/A081/", 0.25);
    	
    	songs = new double[lSongs.size()][];
    	tests = new double[lTests.size()][];
    	
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
    	
    	DBN dbn = new DBN(new Layer(avg.width*avg.height), new Layer(topology.removeFirst())){
    		double besterror = 0;
    		double bestclone[][];
    		int badsteps = 0;
    		
    		@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double input[][], double testset[][]){
    			double etest, einput;
    			
    			// First step
    			if(epoch==0){
    				rbm.initializeTraining(input, rbm==rbmList.getFirst() ? 0.1 : 1);
    				besterror = 0;
    				badsteps = 0;	
    			}
    			
    			etest = rbm.getError(testset);
    			einput = rbm.getError(input);
    			
    			if(besterror==0 || etest < besterror){
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput +" \t (best)");
        			besterror = etest;
    				bestclone = ArrayManipulation.deepClone(rbm.connection);
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
    	
    	while(topology.size()>0)
    		dbn.addLayer(new Layer(topology.removeFirst()));
    	
    	if(inArgs(args, "cd")){	
    		dbn.train(songs, tests);
        	dbn.save("dbn.txt");
    	}
    	
    	
    	ae = new Autoencoder(dbn){
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
    				save("bestae.txt");
    			}else{
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput);
    				badsteps++;
    			}
    			return (badsteps < 20);	
    		};
    	};
    	
    	if(inArgs(args, "bp")){
    		ae.setLearningRate(0.0005);
        	ae.train(songs, tests);
    	}
    	
    	//ae.load("/Users/anselmbrachmann/Documents/Uni/Semester6/Bachelor/heap/finalae.txt");
    	//ae.load("/Users/anselmbrachmann/Desktop/bestae.txt");
    	
    	
    	if(inArgs(args,"gui"))
    		buildGUI();    	
    	
    	Log.println("Done.");
    }
}