package Visual;

import javax.swing.*;

import Visual.ArrayDraw;


public class Example extends JFrame{

	private static final long serialVersionUID = 1L;
	
	
	
    public Example() {
    	super();
    	this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationByPlatform(true);
        this.setSize(520, 450);
 
        ArrayDraw md1 = new ArrayDraw();
        ArrayDraw md2 = new ArrayDraw();
        
        md1.setBounds(10, 10, 200, 200);
        md2.setBounds(10, 220, 200, 200);
        this.add(md1);
        this.add(md2);
        
        this.setVisible(true);
        
        double a[] = new double[100];
        for(int i=0; i<100; i++)
        	a[i] = (double) 0;
        
        md1.drawArray(a,10,10);
        md2.drawArray(a,10,10);
    }
 
    public static void main(String[] args) {
        new Example();
    }
}

