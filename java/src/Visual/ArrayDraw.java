package Visual;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class ArrayDraw extends JPanel{

		private static final long serialVersionUID = 1L;
		boolean editable = true;
		double[] array;
		int x,y;
		
		class MouseEventImpl implements MouseListener{
			boolean pressed = false;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!editable)
					return;
				
				int cx = (int)(e.getX()/(double)getWidth()*Math.round(x));
				int cy = (int)(e.getY()/(double)getHeight()*Math.round(y));
				
				array[(int) (x*cy+cx)] = array[(int) (x*cy+cx)]==0? 1 : 0;
				
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				pressed = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				pressed = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		}

		public double get(int i){
			return array[i];
		}
		
		private void setColouredBorder(){
		
		}
		
		public ArrayDraw() {
			x = y = 1;
			array = new double[1];
			addMouseListener(new MouseEventImpl());
			//setBorder(BorderFactory.createLineBorder(new Color(255,0,255)));
		}
 
        public void paintComponent(Graphics g) {
            for(int i=0; i<array.length; i++){
            	if(editable)
            		g.setColor((new Color((int)(array[i]*255), 0, (int)(array[i]*255))));
            	else
            		g.setColor(new Color((int)(array[i]*255), (int)(array[i]*255), (int)(array[i]*255)));
            	
            	g.fillRect(	(int) Math.rint((i%x)*this.getWidth()/x),
            				(int) Math.rint((i/x)*this.getHeight()/y),
            				(int) Math.rint(this.getWidth()/x),
            				(int) Math.rint(this.getHeight()/y));
            }
        }

        public void drawArray(double[] a, int dx, int dy) {
        	array = new double[a.length];
        	
        	for(int i=0; i<array.length; i++)
        		array[i] = a[i] < 1? a[i] : 1;
        	
        	x = dx;
        	y = dy;
        	setColouredBorder();
			repaint();
		}
        
        public void reset(int n, int dx, int dy){
        	array = new double[n];
        	x = dx;
        	y = dy;
        	for(int i=0; i<array.length; i++)
        		array[i] = 0;
        	repaint();
        }
        
        public void setEditable(boolean e){
        	editable = e;
			repaint();
        }
        
        public double[] getArray(){
        	return array.clone();
        }
}
