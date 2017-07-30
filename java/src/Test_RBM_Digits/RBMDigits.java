/*	Nutzt eigene Photoshop-Digits, funzt nicht mehr
 * 
 * */
package Test_RBM_Digits;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

import Core.Layer;
import Core.RBM;
import Tools.Log;
import Visual.ArrayDraw;



public class RBMDigits extends JFrame{


	private static final long serialVersionUID = 1L;
	static RBM rbm;
	
	static JButton bReset, bReconstruct, bSave, bTest;
	static ArrayDraw p,p2,h1;
	static Timer t;
	static int tc;
	
	
	static double digits[][],testset[][];
	
	
	public static void run(){
		int sets = 4;
		
		rbm = new RBM(new Layer(256), new Layer(256)){
			@Override
			public boolean trainCallback(int epoch, double[][] input, double[][] testset){
    			if(epoch==0)
    				rbm.initializeTraining(input, 1);
    			
    			Log.println("RBM("+rbm.vis.getCount()+","+rbm.hid.getCount()+"): Epoch "+epoch+": "+rbm.getError(input));
    			return epoch<100;
    		}
		};
		
		System.out.print("Reading data...");
		digits = new double[sets*10][];
		testset  = new double[10][]; 
		
		for(int i=0; i<sets; i++)
			for(int d=0; d<10; d++)
				digits[i*10+d] = ImageReader.readImage("/Users/anselmbrachmann/Desktop/Bachelor/digits/digit"+d+"."+i+".png");
		for(int d=0; d<10; d++)
			testset[d] = ImageReader.readImage("/Users/anselmbrachmann/Desktop/Bachelor/digits/digit"+d+"."+sets+".png");
		
		System.out.println("done.");

		
		System.out.println("Training RBM...");
		rbm.train(digits,testset);
		System.out.println("Done.");
	}
	
	
 
    public static void main(String[] args) {
    	JFrame frame = new JFrame("RBMTest Digits");
    	frame.setLayout(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setSize(700, 300);
        frame.setTitle("RBMTest Digits");
        
        p = new ArrayDraw();
        p.setBounds(10, 10, 200, 200);
        frame.getContentPane().add(p);
        
        p2 = new ArrayDraw();
        p2.setBounds(220, 10, 200, 200);
        frame.getContentPane().add(p2);
        
        h1 = new ArrayDraw();
        h1.setBounds(440, 10, 200, 200);
        frame.getContentPane().add(h1);
        
        
        bTest = new JButton("Next");
        bTest.setBounds(10,250,100,20);
        bTest.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		p.drawArray(testset[tc++%10], 16, 16);
			}
        });
        frame.getContentPane().add(bTest);
        
        
        
        bReset = new JButton("Reset");
        bReset.setBounds(10,220,100,20);
        bReset.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for(int i=0; i<rbm.vis.getCount(); i++)
        			rbm.vis.value[i] = 0;
        		p.drawArray(rbm.vis.value, 16, 16);
			}
        });
        frame.getContentPane().add(bReset);
        
        bSave = new JButton("Transfer");
        bSave.setBounds(120,220,90,20);
        bSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for(int i=0; i<rbm.vis.getCount(); i++)
        			rbm.vis.value[i] = p.get(i);
        		p2.drawArray(rbm.vis.value, 16, 16);
        	}
        });
        frame.getContentPane().add(bSave);
        
        t = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				rbm.reconstructVisible();
				p2.drawArray(rbm.vis.value, 16, 16);
				h1.drawArray(rbm.hid.value, 16, 16);
			}
        });
        
        bReconstruct = new JButton("Start");
        bReconstruct.setBounds(220,220,100,20);
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
        frame.setVisible(true);	
        run();
    }
}
