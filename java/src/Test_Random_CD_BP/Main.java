package Test_Random_CD_BP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Core.Autoencoder;
import Core.DBN;
import Core.Layer;
import Core.RBM;
import Tools.ArrayManipulation;
import Tools.Log;
import Visual.ArrayDraw;

public class Main {

	private static final long serialVersionUID = 1L;
	
	static String name = "Test_Random_CD_BP.Main";
	
	static DBN dbn;
	static Autoencoder ae;
	static Autoencoder aeb;
	
	static double digits[][],testset[][];
	static boolean reconstructForward = true;
	
	static JButton bReset1, bAssign1,  bStart1, bSave, bLoad, bContDiv, bBackpropagation;
	static JSlider sliderTest;
	static JLabel lTest;
	static ArrayDraw adInput1,	adLayer0,adLayer1,adLayer2,adLayer3,adLayer4,adLayer5,adLayer6,
								adLayer0b,adLayer1b,adLayer2b,adLayer3b,adLayer4b,adLayer5b,adLayer6b;
	static Timer timer;
	

	public static void buildGUI(){
		JFrame frame = new JFrame();
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setSize(900, 400);
		frame.setTitle(name);
    
		adInput1 = new ArrayDraw();
		adInput1.setBounds(10, 10, 100, 100);
		adInput1.setEditable(true);
		adInput1.reset(16, 4, 4);
		frame.getContentPane().add(adInput1);
    
		adLayer0 = new ArrayDraw();
		adLayer0.setBounds(130, 10, 100, 100);
		adLayer0.setEditable(false);
		frame.getContentPane().add(adLayer0);
    
		adLayer1 = new ArrayDraw();
		adLayer1.setBounds(240, 10, 100, 100);
		adLayer1.setEditable(false);
		frame.getContentPane().add(adLayer1);
    
		adLayer2 = new ArrayDraw();
		adLayer2.setBounds(350, 10, 100, 100);
		adLayer2.setEditable(false);
		frame.getContentPane().add(adLayer2);
		
		adLayer3 = new ArrayDraw();
		adLayer3.setBounds(460, 10, 100, 100);
		adLayer3.setEditable(false);
		frame.getContentPane().add(adLayer3);
		
		adLayer4 = new ArrayDraw();
		adLayer4.setBounds(570, 10, 100, 100);
		adLayer4.setEditable(false);
		frame.getContentPane().add(adLayer4);
		
		adLayer5 = new ArrayDraw();
		adLayer5.setBounds(680, 10, 100, 100);
		adLayer5.setEditable(false);
		frame.getContentPane().add(adLayer5);
		
		adLayer6 = new ArrayDraw();
		adLayer6.setBounds(790, 10, 100, 100);
		adLayer6.setEditable(false);
		frame.getContentPane().add(adLayer6);
		
		
		
		adLayer0b = new ArrayDraw();
		adLayer0b.setBounds(130, 120, 100, 100);
		adLayer0b.setEditable(false);
		frame.getContentPane().add(adLayer0b);
    
		adLayer1b = new ArrayDraw();
		adLayer1b.setBounds(240, 120, 100, 100);
		adLayer1b.setEditable(false);
		frame.getContentPane().add(adLayer1b);
    
		adLayer2b = new ArrayDraw();
		adLayer2b.setBounds(350, 120, 100, 100);
		adLayer2b.setEditable(false);
		frame.getContentPane().add(adLayer2b);
		
		adLayer3b = new ArrayDraw();
		adLayer3b.setBounds(460, 120, 100, 100);
		adLayer3b.setEditable(false);
		frame.getContentPane().add(adLayer3b);
		
		adLayer4b = new ArrayDraw();
		adLayer4b.setBounds(570, 120, 100, 100);
		adLayer4b.setEditable(false);
		frame.getContentPane().add(adLayer4b);
		
		adLayer5b = new ArrayDraw();
		adLayer5b.setBounds(680, 120, 100, 100);
		adLayer5b.setEditable(false);
		frame.getContentPane().add(adLayer5b);
		
		adLayer6b = new ArrayDraw();
		adLayer6b.setBounds(790, 120, 100, 100);
		adLayer6b.setEditable(false);
		frame.getContentPane().add(adLayer6b);
		
		
//		
//		adLayer5 = new ArrayDraw();
//		adLayer5.setBounds(680, 10, 100, 100);
//		adLayer5.setEditable(false);
//		frame.getContentPane().add(adLayer5);
//		
//		adLayer6 = new ArrayDraw();
//		adLayer6.setBounds(790, 10, 100, 100);
//		adLayer6.setEditable(false);
//		frame.getContentPane().add(adLayer6);
		
		
		bAssign1 = new JButton("Assign");
        bAssign1.setBounds(10,120,100,bAssign1.getPreferredSize().height);
        bAssign1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		RBM rbm = ae.getRBM(0);
        		RBM rbmb = aeb.getRBM(0);
        		for(int i=0; i<rbm.vis.getCount(); i++){
        			rbm.vis.value[i] = adInput1.get(i);
        			rbmb.vis.value[i] = adInput1.get(i);
        		}
        			
        		adLayer0.drawArray(rbm.vis.value, 4, 4);
        		adLayer0b.drawArray(rbmb.vis.value, 4, 4);
        	}
        });
        frame.getContentPane().add(bAssign1);
        
        
        bReset1 = new JButton("Reset"); // <<
		bReset1.setBounds(10,150,100,bReset1.getPreferredSize().height);
		bReset1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adInput1.reset(16, 4, 4);
			}
		});
		frame.getContentPane().add(bReset1);
    
	    
     /*   bStart1 = new JButton("Start");
        bStart1.setBounds(10,180,100, bStart1.getPreferredSize().height);
        bStart1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(timer.isRunning()){
        			timer.stop();
        			bStart1.setText("Start");
        		}else{
        			reconstructForward = true;
        			bStart1.setText("Stop");
        			timer.start();
        		} 			
			}
        });     
        frame.getContentPane().add(bStart1);
        */
        
        sliderTest = new JSlider();
        sliderTest.setMinimum(0); 
        sliderTest.setMaximum(testset.length-1);
        sliderTest.setValue(0);
        sliderTest.setBounds(10,230,180,20);
        sliderTest.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		lTest.setText(""+sliderTest.getValue());
                adInput1.drawArray(testset[sliderTest.getValue()], 4, 4);
                bAssign1.doClick();
            }
        });
        adInput1.drawArray(testset[sliderTest.getValue()], 4, 4);
        frame.getContentPane().add(sliderTest);
        
        lTest = new JLabel();
        lTest.setBounds(190,230,20,20);
        frame.getContentPane().add(lTest);
        
        bContDiv = new JButton("Contrastive Divergence");
        bContDiv.setBounds(240,230,210,bContDiv.getPreferredSize().height);
        bContDiv.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		timer.stop();
            	dbn.train(digits, testset);
            	
            	ae  =  new Autoencoder(dbn);
            	aeb =  new Autoencoder(dbn){
            		@Override
            		public boolean trainCallback(int epoch, double[][] input, double[][] testset){
            			Log.println("Epoch "+epoch+"... \t " + getError(testset) +" \t "+  getError(input) +" \t (best)");
            			return epoch<100;
            		};
            	};
            	
            	timer.start();
        	}
        });
        frame.getContentPane().add(bContDiv);
        
        
        bBackpropagation = new JButton("Backpropagation");
        bBackpropagation.setBounds(240,260,210,bBackpropagation.getPreferredSize().height);
        bBackpropagation.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		timer.stop();
        		aeb.train(digits, testset);
        		timer.start();
        	}
        });
        frame.getContentPane().add(bBackpropagation);
        
        bLoad = new JButton("Load");
        bLoad.setBounds(10,260,100,bLoad.getPreferredSize().height);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ae.load("/Users/anselmbrachmann/Desktop/Bsp8-3/dbn.txt") && aeb.load("/Users/anselmbrachmann/Desktop/Bsp8-3/dbn.txt"))
        			Log.println("DBN loaded.");
        		
        		int p=0;
            	for(double pattern[] : digits){
                    Layer in = ae.getLayer(0);
                    for(int i=0; i<pattern.length; i++)
                    	in.value[i] = pattern[i];
                    ae.feedForward();
                    
                    Log.println((p++) +" "+ ae.getLayer(3).value[0]+" "+ ae.getLayer(3).value[1]+" "+ ae.getLayer(3).value[2]);
            	}
        	}
        });
        frame.getContentPane().add(bLoad);
        
        
        bSave = new JButton("Save");
        bSave.setBounds(10,290,100,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(aeb.save("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			Log.println("RBM saved.");
        	}
        });
        frame.getContentPane().add(bSave);
        
        
        timer = new Timer(500, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ae.reconstructForward();
				adLayer0.drawArray(ae.getLayer(0).export(), 4, 4);
				adLayer1.drawArray(ae.getLayer(1).export(), 5, 5);
				adLayer2.drawArray(ae.getLayer(2).export(), 2, 2);
				adLayer3.drawArray(ae.getLayer(3).export(), 3, 1);
				adLayer4.drawArray(ae.getLayer(4).export(), 2, 2);
				adLayer5.drawArray(ae.getLayer(5).export(), 5, 5);
				adLayer6.drawArray(ae.getLayer(6).export(), 4, 4);
				
				aeb.feedForward();
				adLayer0b.drawArray(aeb.getLayer(0).export(), 4, 4);
				adLayer1b.drawArray(aeb.getLayer(1).export(), 3, 3);
				adLayer2b.drawArray(aeb.getLayer(2).export(), 2, 2);
				adLayer3b.drawArray(aeb.getLayer(3).export(), 3, 1);
				adLayer4b.drawArray(aeb.getLayer(4).export(), 2, 2);
				adLayer5b.drawArray(aeb.getLayer(5).export(), 3, 3);
				adLayer6b.drawArray(aeb.getLayer(6).export(), 4, 4);
			}
        });
        
        timer.start();
        frame.setVisible(true);	
	}
	
 
    public static void main(String[] args) {
    	Log.println(name);
    	dbn = new DBN(new Layer(16), new Layer(9)){
    		double besterror = 0;
    		double bestclone[][];
    		int badsteps = 0;
    		
    		@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double input[][], double testset[][]){
    			double etest, einput;
    			
    			// First step
    			if(epoch==0){
    				rbm.initializeTraining(input, 1);
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
    	dbn.addLayer(new Layer(4));
    	dbn.addLayer(new Layer(3));
    	
    	digits = testset = DataRandom.Generator.getData(8);
    	
//    	Log.println("Pretraining (Contrastive Divergence)...");
//    	Log.inc(2);
//    	dbn.train(digits, testset, 0);
//    	Log.dec(2);
//    	
//    	Log.println("Creating Autoencoder... ");
    	ae  =  new Autoencoder(dbn);
    	aeb =  new Autoencoder(dbn);
    	
//    	Log.println("Training (Backpropagation)...");
//    	Log.inc(2);
//    	aeb.train(digits, testset, 0);
//    	Log.dec(2);
    	
    	for(int i=0; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    }
}
