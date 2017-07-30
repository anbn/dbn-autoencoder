package Test_RBM_MNIST;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Core.Layer;
import Core.RBM;
import MNIST.MNISTReader;
import Tools.Log;
import Visual.ArrayDraw;



public class RBMDigitsMNIST extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	
	static RBM rbm;
	static double digits[][],testset[][];
	
	static JButton bReset, bReconstruct, bTransfer, bTest, bTrain, bSave, bLoad;
	static JSlider sliderTest;
	static JLabel lTest;
	static ArrayDraw p,p2,h1;
	static Timer t;
	

	public static void buildGUI(){
		JFrame frame = new JFrame();
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setSize(750, 300);
		frame.setTitle("Test_RBM_MNIST.RBMDigitsMNIST");
    
		p = new ArrayDraw();
		p.setBounds(10, 10, 200, 200);
		p.setEditable(true);
		frame.getContentPane().add(p);
    
		p2 = new ArrayDraw();
		p2.setBounds(220, 10, 200, 200);
		p2.setEditable(false);
		frame.getContentPane().add(p2);
    
		h1 = new ArrayDraw();
		h1.setBounds(430, 10, 200, 200);
		h1.setEditable(false);
		frame.getContentPane().add(h1);
    
		bReset = new JButton("Reset");
		bReset.setBounds(10,220,100,bReset.getPreferredSize().height);
		bReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0; i<rbm.vis.getCount(); i++)
					rbm.vis.value[i] = 0;
				p.drawArray(rbm.vis.value, 28, 28);
			}
		});
		frame.getContentPane().add(bReset);
    
        bTransfer = new JButton("Transfer");
        bTransfer.setBounds(110,220,100,bTransfer.getPreferredSize().height);
        bTransfer.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for(int i=0; i<rbm.vis.getCount(); i++)
        			rbm.vis.value[i] = p.get(i);
        		p2.drawArray(rbm.vis.value, 28, 28);
        	}
        });
        frame.getContentPane().add(bTransfer);
        
        bReconstruct = new JButton("Start");
        bReconstruct.setBounds(220,220,100, bReconstruct.getPreferredSize().height);
        bReconstruct.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(t.isRunning()){
        			t.stop();
        			bReconstruct.setText("Start");
        		}else{
        			t.start();
        			bReconstruct.setText("Stop");
        		} 			
			}
        });     
        frame.getContentPane().add(bReconstruct);
        
        sliderTest = new JSlider();
        sliderTest.setMinimum(0); 
        sliderTest.setMaximum(testset.length-1);
        sliderTest.setValue(0);
        sliderTest.setBounds(10,250,180,20);
        sliderTest.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		lTest.setText(""+sliderTest.getValue());
                p.drawArray(testset[sliderTest.getValue()], 28, 28);
            }
        });
        frame.getContentPane().add(sliderTest);
        
        lTest = new JLabel();
        lTest.setBounds(190,250,20,20);
        frame.getContentPane().add(lTest);
        
        
        /*
        bTrain = new JButton("Train");
        bTrain.setBounds(330,220,90,20);
        bTrain.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.out.print("Train... ");
        		rbm.train(digits,testset, 1);
			}
        });
        frame.getContentPane().add(bTrain);
        */
        
        bLoad = new JButton("Load RBM");
        bLoad.setBounds(640,10,100,bLoad.getPreferredSize().height);
        bLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rbm.load("/Users/anselmbrachmann/Desktop/rbm.txt"))
        			System.out.println("RBM loaded.");
        	}
        });
        frame.getContentPane().add(bLoad);
        
        
        bSave = new JButton("Save RBM");
        bSave.setBounds(640,40,100,bSave.getPreferredSize().height);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rbm.save("/Users/anselmbrachmann/Desktop/rbm.txt"))
        			System.out.println("RBM saved.");
        	}
        });
        frame.getContentPane().add(bSave);
        
        t = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				rbm.reconstructVisible();
				p2.drawArray(rbm.vis.value, 28, 28);
				h1.drawArray(rbm.hid.value, 25, 25);
			}
        });
        frame.setVisible(true);	
	}
	
 
    public static void main(String[] args) {
    	String comment = new String("Update Visible Stochastic");
    	
    	System.out.println("Launching Test_RBM_MNIST.RBMDigitsMNIST");
    	System.out.println("- "+comment);
    	
    	if(args.length < 1){
    		System.out.println("Usage: <Path>");
    		System.exit(0);
    	}
    	
    	rbm = new RBM(new Layer(784), new Layer(625)){
    		@Override
			public boolean trainCallback(int epoch, double[][] input, double[][] testset){
    			if(epoch==0)
    				rbm.initializeTraining(input, 1);
    			
    			Log.println("RBM("+rbm.vis.getCount()+","+rbm.hid.getCount()+"): Epoch "+epoch+": "+rbm.getError(input));
    			return epoch<100;
    		}
    	};
    	
 
    	digits = new double[10000][];
		testset  = new double[100][]; 
    	MNISTReader.readMNIST(args[0], digits, testset);
    	
    	System.out.println("Start training...");
    	//rbm.initializeTraining(digits, 1);
    	//rbm.train(digits, testset, 10);
    	
    	System.out.println("Training done.");
    	
    	for(int i=1; i<args.length; i++){
    		if(args[i].equals("gui"))
    			buildGUI();
    	}
    }
}
