package T;

import java.util.Arrays;
import java.util.List;

public class asList {
	public static void main(String[] args) {
		f();
		
	}
	
	public static void a(){
		int [] ints={1,2,3,4,5};
		List list=Arrays.asList(ints);
		System.out.println(list.size());
		System.out.println(list.get(0).getClass());
		System.out.println("list.get(0) == ints：" + list.get(0).equals(ints));
	}
	
	public static void b(){
		 Integer[] ints = {1,2,3,4,5};  
	        List list = Arrays.asList(ints);  
	        list.add(6);  
	}
	public static void c(){
		 String a="asd";
		 String b="asd";
		 String c=new String("asd");
		 System.out.println(a==b);
		 System.out.println(b==c);
		 System.out.println(b.equals(c));
	}
	public static void d(){
		int a=3;
		Integer b=3;
		System.out.println(a==b);
	}
	public static void f(){
	
	int hash[]=new int[30];
	boolean hashid[]=new boolean[100];
	System.out.println(hash[1]);
	System.out.println(hashid[1]);
	}
}
