package Visual;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javax.swing.*;


public class Plotter extends JPanel{

		private static final long serialVersionUID = 1L;
		private Color background = Color.WHITE;
		
		double width, height;
		
		class Line{
			double x,y,x2,y2;
			int t;
			Color c;
			Line(double px, double py, double px2, double py2, Color co, int ti){
				x = px;
				y = py;
				x2 = px2;
				y2 = py2;
				c = co;
				t = ti;
				
			}
		}
		
		LinkedList<Line> col = new LinkedList<Line>();
		Semaphore sem = new Semaphore(1);
		
		
		public void setBackground(Color bg){
			background = bg;
		}
		
		public Plotter(double w, double h) {
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			width = w;
			height = h;
		}
 
        public void paintComponent(Graphics g) {
        	g.setColor(background);
        	g.fillRect(0, 0, this.getWidth(), this.getHeight());
        	

        	for(Line l : col){            	
            	g.setColor(l.c);
            	g.drawLine(	(int) ((l.x)*this.getWidth()/width),
            				(int) ((height - l.y)*this.getHeight()/height),
            				(int) ((l.x2)*this.getWidth()/width),
            				(int) ((height - l.y2)*this.getHeight()/height));		
            }
        }

        public void addLine(double x1, double y1, double x2, double y2, Color co, int t) {
    
        	try{
        		sem.acquire();
            	col.add(new Line(x1, y1, x2, y2, co, t));
            	sem.release();	
        	}catch(Exception e){};
        	
        	
        }
}
