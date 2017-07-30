package KMeans;

import java.util.LinkedList;

public class KMeans<T extends IKMeansCapable> {

	int dim;
	
	public class Cluster{
		
		public double[] mean;
		public LinkedList<T> lPoints;
		
		Cluster(){
			mean = new double[dim];
			for(int i=0; i<dim; i++)
				mean[i]=Math.random();
			lPoints = new LinkedList<T>();
		}
		
		public double getDifference(double[] code){
			double sum = 0;
			
			for(int i=0; i<dim; i++)
				sum += (code[i]-mean[i])*(code[i]-mean[i]);
			return Math.sqrt(sum);
		}

		
		void addPoint(T p){
			lPoints.add(p);
		}
		
		void clear(){
			lPoints.clear();
		}
		
		boolean calculateNewMean(){
			if(lPoints.isEmpty())
				return false;
			
			double[] old = mean.clone();
			mean = new double[dim];
			
			
			for(T point : lPoints){
				double[] code = point.getCode();
				for(int i=0; i<dim; i++)
					mean[i] += code[i];
			}
			
			for(int i=0; i<dim; i++)
				mean[i] /= lPoints.size();
			
			for(int i=0; i<dim; i++)
				if(old[i]!=mean[i])
					return true;
			return false;
		}
	}
	
	public KMeans(int d){
		dim = d;
	}
	
	Cluster getNearestCluster(LinkedList<Cluster> cluster, T point){
		double minDiff = 0;
		Cluster nearestCluster = null;
		
		for(Cluster cl : cluster){
			double diff = cl.getDifference(point.getCode());
			if(nearestCluster==null || diff<minDiff){
				minDiff = diff;
				nearestCluster = cl;
			}	
		}
		return nearestCluster;
	}
	
	public LinkedList<Cluster> run(int n, LinkedList<T> points){
		LinkedList<Cluster> allClusters = new LinkedList<Cluster>();
		for(int i=0; i<n; i++)
			allClusters.add(new Cluster());
		
		
		int steps = 0;
		boolean changed = true;
		do{
			changed = false;
			for(Cluster cl : allClusters)
				cl.clear();
			
			for(T point : points){
				getNearestCluster(allClusters, point).addPoint(point);
			}
			
			for(Cluster cl : allClusters)
				changed |= cl.calculateNewMean();
			
			System.out.print(".");
		}while(changed && steps++<1000);
		
		return allClusters;
	}

}










