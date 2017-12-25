package T;

import java.util.HashMap;

public class gr {
	static{  
        System.out.println("执行静态代码块...");  
    }  
      
    /**  
     * 构造代码块  
     */  
    {  
        System.out.println("执行构造代码块...");  
    }  
      
    /**  
     * 无参构造函数  
     */  
    public gr(){  
        System.out.println("执行无参构造函数...");  
    }  
      
    /**  
     * 有参构造函数  
     * @param id  
     */  
    public gr(String id){  
        System.out.println("执行有参构造函数...");  
    }  
      
    public static void main(String[] args) {  
        System.out.println("----------------------");  
        new gr();  
        System.out.println("----------------------");  
        new gr("1");  
    }  
  
  
}
