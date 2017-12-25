package com.yfwl.yfgp.utils;

public class RiskAssessUtil {
	
	public static void main(String[] args) throws Exception {
		
		/*String options = "A,B,A,C,A,D";
		getGrade(options);
		System.out.println(getGrade(options));*/
		Integer grade = 1000;
		if(grade <= 20){
			//稳健型
			System.out.println("稳健型");
		}else if( grade <= 50){
			//成长型
			System.out.println("成长型");
		}else{
			//激进型
			System.out.println("激进型");
		}
	}
	
	
	public static Integer getGrade(String options){
		
		String[] strarr = options.split(",");
		Integer g1 = getGradeOfOne(strarr[0].toString());
		Integer g2 = getGradeOfTwo(strarr[1].toString());
		Integer g3 = getGradeOfThree(strarr[2].toString());
		Integer g4 = getGradeOfFour(strarr[3].toString());
		Integer g5 = getGradeOfFive(strarr[4].toString());
		Integer g6 = getGradeOfSix(strarr[5].toString());
		
		Integer grade = g1+g2+g3+g4+g5+g6;
		
		return grade;
	}
	
	
	private static Integer getGradeOfOne(String option){
		Integer grade = 0;
		switch (option) {
			case "A":
				grade = 8;
				break;
			
			case "B":
				grade = 10;
				break;
				
			case "C":
				grade = 6;
				break;
			
			default:
				break;
		}
		return grade;
	}
	
	private static Integer getGradeOfTwo(String option){
		Integer grade = 0;
		switch (option) {
			/*case "A":
				grade = 0;
				break;*/
			
			case "B":
				grade = 2;
				break;
				
			case "C":
				grade = 4;
				break;
				
			case "D":
				grade = 6;
				break;
				
			case "E":
				grade = 8;
				break;
			default:
				break;
		}
		return grade;
	}
	
	private static Integer getGradeOfThree(String option){
		Integer grade = 0;
		switch (option) {
			/*case "A":
				grade = 0;
				break;*/
			
			case "B":
				grade = 2;
				break;
				
			case "C":
				grade = 4;
				break;
				
			case "D":
				grade = 6;
				break;
				
			case "E":
				grade = 8;
				break;
			default:
				break;
		}
		return grade;
	}
	
	private static Integer getGradeOfFour(String option){
		Integer grade = 0;
		switch (option) {
			/*case "A":
				grade = 0;
				break;*/
			
			case "B":
				grade = 4;
				break;
				
			case "C":
				grade = 8;
				break;
				
			case "D":
				grade = 10;
				break;
				
			default:
				break;
		}
		return grade;
	}
	
	private static Integer getGradeOfFive(String option){
		Integer grade = 0;
		switch (option) {
			case "A":
				grade = 2;
				break;
			
			case "B":
				grade = 6;
				break;
				
			case "C":
				grade = 10;
				break;
				
			default:
				break;
		}
		return grade;
	}
	
	private static Integer getGradeOfSix(String option){
		Integer grade = 0;
		switch (option) {
			case "A":
				grade = -5;
				break;
			
			case "B":
				grade = 5;
				break;
				
			case "C":
				grade = 10;
				break;
				
			case "D":
				grade = 15;
				break;
				
			case "E":
				grade = 20;
				break;
			default:
				break;
		}
		return grade;
	}
}
