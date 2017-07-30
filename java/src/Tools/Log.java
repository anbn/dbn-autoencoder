package Tools;
	
public class Log{
	private static String sin = "";
	private static int indent = 0;
	
	private static void update(){
		sin = "";
		for(int i=0; i<indent; i++)
			sin +=' ';
	}
	
	public static void println(String s){
		System.out.println(sin + s);
	}
	
	public static void print(String s){
		System.out.print(s);
	}		
	
	public static void inc(int n){
		indent += n;
		update();
	}
	
	public static void dec(int n){
		indent -= n;
		update();
	}
	
	public static void set(int n){
		indent = n;
	}

}
