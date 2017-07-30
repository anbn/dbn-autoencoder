package Test_CRBM;


import java.awt.Color;

import javax.swing.JFrame;

import Core.Layer;
import Core.RBM;
import Tools.Log;
import Visual.ArrayDraw;
import Visual.Plotter;



public class RBMTest extends JFrame{


	private static final long serialVersionUID = 1L;
	static Plotter p;
	static ArrayDraw ah,av;
	
	public RBMTest() {
    	super();
    	this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setAlwaysOnTop(true);
        this.setLocationByPlatform(true);
        this.setSize(620, 440);
        this.setTitle("CRBMTest Plotter");
        
        p = new Plotter(1,1);
        p.setBounds(10, 10, 400, 400);
        this.add(p);
        
        ah = new ArrayDraw();
        ah.setBounds(420, 10, 160, 20);
        this.add(ah);
        av = new ArrayDraw();
        av.setBounds(420, 40, 160, 20);
        this.add(av);
        
        this.setVisible(true);   
    }
	
	
 
    public static void main(String[] args) {
    	new RBMTest();
    	
    	RBM rbm = new RBM(new Layer(2), new Layer(8)){
    		@Override
    		public boolean trainCallback(int epoch, double[][] input, double[][] testset){
    			Log.println("Epoch "+epoch+"... \t " + getError(testset) +" \t "+ getError(input) +" \t");
    			return (epoch < 500);
    		}
    	};
    	
    	double input[][] = new double[1000][2];
		
		for(int i=0; i<input.length; i++){
			input[i][0] = Math.random();
			input[i][1] = Math.abs(Math.sin(Math.PI*input[i][0]));
			/*if(Math.random()<=0.5)
				input[i][1] = Math.sin(Math.PI*input[i][0]);
			else
				input[i][1] = 1-Math.sin(Math.PI*input[i][0]);*/
			p.addLine(input[i][0],input[i][1],input[i][0],input[i][1], Color.BLACK, 0);
		}
		
		
		rbm.train(input,null);
		
		/*for(int e=1; e<=10000; e++){
			if(e%100 == 0){
					rbm.vis.prob[0] = Math.random();
					rbm.vis.prob[1] = Math.random();
					rbm.reconstructVisible();
					ah.drawArray(rbm.hid.prob, rbm.hid.count+1, 1);
					av.drawArray(rbm.vis.prob, rbm.vis.count+1, 1);
					//p.addLine(rbm.vis.prob[0],rbm.vis.prob[1],rbm.vis.prob[0],rbm.vis.prob[1], Color.RED,1);
				System.out.println("Epoch "+e+"... "+ error);
				p.paint(p.getGraphics());
			}
		}*/
			
		
		for(int i=0; i<1000; i++){
			double x = rbm.vis.value[0] = Math.random();
			double y = rbm.vis.value[1] = Math.random();
			
			rbm.reconstructVisible();
			
			p.addLine(x,y,rbm.vis.value[0],rbm.vis.value[1], Color.LIGHT_GRAY,0);
			p.addLine(rbm.vis.value[0],rbm.vis.value[1],rbm.vis.value[0],rbm.vis.value[1], Color.BLUE,0);
		}
		p.paint(p.getGraphics());
		
    }
}
