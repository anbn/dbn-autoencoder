package Sandbox;

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

public class BackpropagationTest {

	private static final long serialVersionUID = 1L;
	
	//static RBM rbm;
	static Autoencoder ae;
	
	static double digits[][],testset[][];
	
	static JButton bReset1, bAssign1, bStart1, bSave, bLoad;
	static JSlider sliderTest;
	static JLabel lTest;
	static ArrayDraw adInput1,adLayer0,adLayer1,adLayer2,adLayer3,adLayer4;
	static Timer timer;
	

	public static void buildGUI(){
		JFrame frame = new JFrame();
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setSize(750, 300);
		frame.setTitle("Test_DBN_Random.Test_DBN_Random");
    
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
		
		/*adLayer3 = new ArrayDraw();
		adLayer3.setBounds(460, 10, 100, 100);
		adLayer3.setEditable(false);
		frame.getContentPane().add(adLayer3);
		
		adLayer4 = new ArrayDraw();
		adLayer4.setBounds(570, 10, 100, 100);
		adLayer4.setEditable(false);
		frame.getContentPane().add(adLayer4);*/
		
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
        sliderTest.setBounds(10,250,180,20);
        sliderTest.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		lTest.setText(""+sliderTest.getValue());
                adInput1.drawArray(testset[sliderTest.getValue()], 4, 4);
            }
        });
        frame.getContentPane().add(sliderTest);
        
        lTest = new JLabel();
        lTest.setBounds(190,250,20,20);
        frame.getContentPane().add(lTest);
        
        bLoad = new JButton("Load DBN");
        bLoad.setBounds(640,10,100,bLoad.getPreferredSize().height);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ae.load("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			System.out.println("RBM loaded.");
        	}
        });
        frame.getContentPane().add(bLoad);
        
        
        bSave = new JButton("Save DBN");
        bSave.setBounds(640,40,100,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ae.save("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			System.out.println("RBM saved.");
        	}
        });
        frame.getContentPane().add(bSave);
        
        timer = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				for(RBM rbm : ae.rbmList)
					rbm.updateFeatures();
				
				
				adLayer0.drawArray(ae.getLayer(0).export(), 4, 4);
				adLayer1.drawArray(ae.getLayer(1).export(), 5, 5);
				adLayer2.drawArray(ae.getLayer(2).export(), 4, 4);
				//adLayer3.drawArray(ae.getLayer(3).export(), 5, 5);
				//adLayer4.drawArray(ae.getLayer(4).export(), 4, 4);
			}
        });
        frame.setVisible(true);	
	}
	
 
    public static void main(String[] args) {
    	Log.println("Launching Test_DBN_Random.Test_DBN_Random");
    	Log.println("Pure Backpropagation");
    	
    	DBN dbn = new DBN(new Layer(16), new Layer(25)){
    		/*@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double[][] input, double[][] testset){
    			return epoch<1000;
    		}*/
    	};
    	//dbn.addLayer(new Layer(36));
    	ae = new Autoencoder(dbn){
    		@Override
    		public boolean trainCallback(int epoch, double[][] input, double[][] testset){
    			Log.println("Epoch "+epoch+"... \t " + getError(testset) +" \t "+ getError(input) +" \t");
    			return epoch<10000;
    		}
    	};
    	
    	digits = testset = DataRandom.Generator.getData(8);
    	
    	System.out.println("Start training...");
    	ae.setLearningRate(0.1);
    	ae.train(digits, testset);
    	System.out.println("Training done.");
    	
    	for(int i=0; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    }
}
