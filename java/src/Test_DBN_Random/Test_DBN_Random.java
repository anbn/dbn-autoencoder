package Test_DBN_Random;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Core.DBN;
import Core.Layer;
import Core.RBM;
import Tools.Log;
import Visual.ArrayDraw;

public class Test_DBN_Random {

	private static final long serialVersionUID = 1L;
	
	//static RBM rbm;
	static DBN dbn;
	
	static double digits[][],testset[][];
	static boolean reconstructForward = true;
	
	static JButton bReset1, bAssign1,  bStart1, bReset2, bAssign2,  bStart2, bSave, bLoad;
	static JSlider sliderTest;
	static JLabel lTest;
	static ArrayDraw adInput1,adLayer0,adLayer1,adLayer2, adInput2;
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
		
		adInput2 = new ArrayDraw();
		adInput2.setBounds(470, 10, 100, 100);
		adInput2.setEditable(true);
		adInput2.reset(4, 2, 2);
		frame.getContentPane().add(adInput2);
    
		
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
        		RBM rbm = dbn.getRBM(0);
        		for(int i=0; i<rbm.vis.getCount(); i++)
        			rbm.vis.value[i] = adInput1.get(i);
        		adLayer0.drawArray(rbm.vis.value, 4, 4);
        	}
        });
        frame.getContentPane().add(bAssign1);
        
        bReset2 = new JButton("Reset");	// >>
		bReset2.setBounds(470,150,100, bReset2.getPreferredSize().height);
		bReset2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adInput2.reset(4, 2, 2);
			}
		});
		frame.getContentPane().add(bReset2);
    
        bAssign2 = new JButton("Assign");
        bAssign2.setBounds(470,120,100,bAssign2.getPreferredSize().height);
        bAssign2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Layer l = dbn.getLayer(dbn.size());
        		for(int i=0; i<l.getCount(); i++)
        			l.value[i] = adInput2.get(i);
        		adLayer2.drawArray(l.value, 2, 2);
        	}
        });
        frame.getContentPane().add(bAssign2);
    
        
        
        bStart1 = new JButton("Start >>");
        bStart1.setBounds(130,120,100, bStart1.getPreferredSize().height);
        bStart1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(timer.isRunning()){
        			timer.stop();
        			bStart1.setText("Start >>");
        			bStart2.setEnabled(true);
        		}else{
        			reconstructForward = true;
        			bStart1.setText("Stop");
        			bStart2.setEnabled(false);
        			timer.start();
        		} 			
			}
        });     
        frame.getContentPane().add(bStart1);
        
        
        bStart2 = new JButton("<< Start");
        bStart2.setBounds(350,120,100, bStart1.getPreferredSize().height);
        bStart2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(timer.isRunning()){
        			timer.stop();
        			bStart2.setText("<< Start");
        			bStart1.setEnabled(true);
        		}else{
        			reconstructForward = false;
        			bStart2.setText("Stop");
        			bStart1.setEnabled(false);
        			timer.start();
        		} 			
			}
        });     
        frame.getContentPane().add(bStart2);
        
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
        		if(dbn.load("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			System.out.println("RBM loaded.");
        	}
        });
        frame.getContentPane().add(bLoad);
        
        
        bSave = new JButton("Save DBN");
        bSave.setBounds(640,40,100,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(dbn.save("/Users/anselmbrachmann/Desktop/dbn.txt"))
        			System.out.println("RBM saved.");
        	}
        });
        frame.getContentPane().add(bSave);
        
        timer = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(reconstructForward)
					dbn.reconstructForward();
				else
					dbn.reconstructBackward();
				adLayer0.drawArray(dbn.getLayer(0).export(), 4, 4);
				adLayer1.drawArray(dbn.getLayer(1).export(), 4, 4);
				adLayer2.drawArray(dbn.getLayer(2).export(), 2, 2);
			}
        });
        frame.setVisible(true);	
	}
	
 
    public static void main(String[] args) {
    	//String comment = new String("Update Visible Stochastic");
    	
    	System.out.println("Launching Test_DBN_Random.Test_DBN_Random");
    	//System.out.println("- "+comment);
    	
    	dbn = new DBN(new Layer(16), new Layer(16)){
    		@Override
    		public boolean RBMTrainCallback(RBM rbm, int epoch, double[][] input, double[][] testset){
    			Log.println("RBM("+rbm.vis.getCount()+","+rbm.hid.getCount()+"): Epoch "+epoch+": "+rbm.getError(input));
    			return epoch<100;
    		}
    	};
    	dbn.addLayer(new Layer(4));
    	
    	digits = testset = DataRandom.Generator.getData(5);
    	
    	System.out.println("Start training...");
    	dbn.train(digits, testset);
    	System.out.println("Training done.");
    	
    	for(int i=0; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    }
}
