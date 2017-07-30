package Test_AE_Random;

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
import Tools.Log;
import Visual.ArrayDraw;

public class Test_AE_Random {

	private static final long serialVersionUID = 1L;
	
	static Autoencoder ae;
	
	static double digits[][],testset[][];
	
	static JButton bReset1, bAssign1,  bStart1, bSave, bLoad;
	static JSlider sliderTest;
	static JLabel lTest;
	static ArrayDraw adInput1,adLayer0,adLayer1,adLayer2,adLayer3,adLayer4,adLayer5,adLayer6;
	static Timer timer;
	

	public static void buildGUI(){
		JFrame frame = new JFrame();
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setSize(900, 300);
		frame.setTitle("Test_AE_RANDOM.Test_AE_RANDOM");
    
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
		
		
		bReset1 = new JButton("Reset"); // <<
		bReset1.setBounds(10,150,100,bReset1.getPreferredSize().height);
		bReset1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adInput1.reset(16, 4, 4);
			}
		});
		frame.getContentPane().add(bReset1);
    
		
        bAssign1 = new JButton("Assign");
        bAssign1.setBounds(10,120,100,bAssign1.getPreferredSize().height);
        bAssign1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		RBM rbm = ae.getRBM(0);
        		for(int i=0; i<rbm.vis.getCount(); i++)
        			rbm.vis.value[i] = adInput1.get(i);
        		adLayer0.drawArray(rbm.vis.value, 4, 4);
        	}
        });
        frame.getContentPane().add(bAssign1);
        
        
        bStart1 = new JButton("Start >>");
        bStart1.setBounds(130,120,100, bStart1.getPreferredSize().height);
        bStart1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(timer.isRunning()){
        			timer.stop();
        			bStart1.setText("Start >>");
        		}else{
        			bStart1.setText("Stop");
        			timer.start();
        		} 			
			}
        });     
        frame.getContentPane().add(bStart1);
        
        sliderTest = new JSlider();
        sliderTest.setMinimum(0); 
        sliderTest.setMaximum(testset.length-1);
        sliderTest.setValue(0);
        sliderTest.setBounds(10,210,180,20);
        sliderTest.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		lTest.setText(""+sliderTest.getValue());
                adInput1.drawArray(testset[sliderTest.getValue()], 4, 4);
            }
        });
        adInput1.drawArray(testset[sliderTest.getValue()], 4, 4);
        frame.getContentPane().add(sliderTest);
        
        lTest = new JLabel();
        lTest.setBounds(190,210,20,20);
        frame.getContentPane().add(lTest);
        
        bLoad = new JButton("Load DBN");
        bLoad.setBounds(240,210,100,bLoad.getPreferredSize().height);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ae.load("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			Log.println("RBM loaded.");
        	}
        });
        frame.getContentPane().add(bLoad);
        
        
        bSave = new JButton("Save DBN");
        bSave.setBounds(240,240,100,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ae.save("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			Log.println("RBM saved.");
        	}
        });
        frame.getContentPane().add(bSave);
        
        timer = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ae.reconstructForward();
				adLayer0.drawArray(ae.getLayer(0).export(), 4, 4);
				adLayer1.drawArray(ae.getLayer(1).export(), 3, 3);
				adLayer2.drawArray(ae.getLayer(2).export(), 3, 1);
				adLayer3.drawArray(ae.getLayer(3).export(), 3, 3);
				adLayer4.drawArray(ae.getLayer(4).export(), 4, 4);
				//adLayer5.drawArray(dbn.getLayer(5).export(), 3, 3);
				//adLayer6.drawArray(dbn.getLayer(6).export(), 4, 4);
			}
        });
        frame.setVisible(true);	
	}
	
 
    public static void main(String[] args) {
    	Log.println("Test_AE_Random.Test_AE_Random");
    	
    	DBN dbn = new DBN(new Layer(16), new Layer(9)){
    		@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double[][] input, double[][] testset){
    			Log.println("RBM("+rbm.vis.getCount()+","+rbm.hid.getCount()+"): Epoch "+epoch+": "+rbm.getError(input));
    			return (epoch<1000);
    		}
    	};
    	dbn.addLayer(new Layer(3));
    	//dbn.addLayer(new Layer(2));
    	
    	digits = testset = DataRandom.Generator.getData(8);
    	
    	Log.println("Pretraining (Contrastive Divergence)...");
    	Log.inc(2);
    	dbn.train(digits, testset);
    	Log.dec(2);
    	
    	Log.println("Creating Autoencoder... ");
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
    				save("_ae.txt");
    			}else{
    				Log.println("Epoch "+epoch+"... \t " + etest +" \t "+ einput);
    				badsteps++;
    			}
    			return (badsteps < 50);	
    		}
    	};
    	
    	Log.println("Training (Backpropagation)...");
    	Log.inc(2);
    	ae.setLearningRate(0.01);
    	ae.train(digits, testset);
    	Log.dec(2);
    	
    	for(int i=0; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    }
}
