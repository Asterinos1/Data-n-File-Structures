package pack2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import pack1.CoupleClass;
import pack1.DataClass;

public class RandomGenerator {
	//This classed is used to generate all the content needed for the exercise.
	//(DataClass instances, CoupleClass instances, random numbers for searching etc.)
	//Simple sorting method taken from the Internet, it takes an unorganized list of CoupleClass instances
	//and it organizes it according to key size from the smallest to the biggest key.
	//Returns the organized list of CoupleClass instances.
	public static List<CoupleClass> organiseCouples(List<CoupleClass> list) {
		int i, j, min_idx;
		int xp, yp;
		int xp2, yp2;
		// One by one move boundary of the unsorted list.
		for(i=0; i<list.size(); i++) {
			//Find the minimum element in unsorted list.
	        min_idx = i;
	        for (j=i+1; j<list.size(); j++) {
	        	 if (list.get(j).getKey() < list.get(min_idx).getKey()) {
	        		 min_idx = j;
	        	 }
	        }
	        // Swap the found minimum element
	        // with the first element
	        // first save their values in temporary variables xp, yp , xp2, yp2.
	        xp = list.get(min_idx).getKey();
	        xp2 = list.get(min_idx).getPage();
	        		
	        yp = list.get(i).getKey();
	        yp2 = list.get(i).getPage(); 
	        
	        list.get(min_idx).setKey(yp);
	        list.get(min_idx).setPage(yp2);
	        list.get(i).setKey(xp);
	        list.get(i).setPage(xp2);
		}
		return list;
	}
	
	//Method to create a list of couples.
	//first we get the desired list of DataClass instances.
	//check in which case we are a) 55 info len b) 27 info len.
	//check by getting the first element of the given list and looking at its info size.
	//we insert each key from DataClass to couple, then add the current
	//page starting from 0 and increasing after 4 or 8 keys have been 
	//added to the couple list depending on the cases above.
	//Returns the CoupleClass instances list.
	public static  List<CoupleClass> createCouples(List<DataClass> list) {
		List<CoupleClass> cclist = new ArrayList<CoupleClass>();
		int maxRecords, curPage=1;
		
		if(list.get(0).getInfo().length()>27) {
			//CASE 55, 
			maxRecords=4;
			
			for(int i=0; i<list.size(); i++){
				if(i>3 && i%maxRecords==0) {
					curPage++;
				}
				cclist.add(new CoupleClass(list.get(i).getKey(), curPage));
			}
		}else {
			//CASE 27
			maxRecords=8;
			
			for(int i=0; i<list.size(); i++){
				if(i>3 && i%maxRecords==0) {
					curPage++;
				}
				cclist.add(new CoupleClass(list.get(i).getKey(), curPage));
			}
		}
		return cclist;
	}
	
	//Method to print DataClass instances of a given DataClass list.
	//Used foe checking, **not used in the final version**.
	public static void printRecords(List<DataClass> list) {
		for(int i=0; i<list.size(); i++) {
			System.out.println("I'm key:" + list.get(i).getKey() + ", with info:" + list.get(i).getInfo());
		} 
	}
	
	//Method to create a specified amount of records and size of info.
	//numOfR = number of records.
	//strL = length in characters of record's info.
	//Returns a DataClass instances list.
	public static List<DataClass> createRecords(int numOfR, int strL) {
		List<DataClass> records = new ArrayList<DataClass>();
		ArrayList<Integer> list1 = getRNums(numOfR);
		
		for(int i=0; i<numOfR; i++) {
			records.add(new DataClass(list1.get(i),getRString(strL)));
		}
		return records;
	}
	
	//Random integers generator.
	//this methods creates a list and feels it 
	//with integer numbers from 1 to 2*N
	//then shuffle them and return a second and final list,
	//containing half the content of the first list.
	//Thus we ensure the numbers are unique.
	public static ArrayList<Integer> getRNums(int numOfKeys){	    
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<numOfKeys*2; i++) list.add(i);
		Collections.shuffle(list);
		
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		for(int i=0; i<numOfKeys; i++) {
			list2.add(i, list.get(i));
		}
		return list2;
	}
	
	//Method to generate numbers for the searches 
	//given from the exercise
	//This method returns an array of 1000 numbers used for the search part of the exercise.
	public static int[] getSearchNumbers(int numOfKeys){
		if(numOfKeys<=1000) {
			Random randomGenerator = new Random();
			int[] randomInts = randomGenerator.ints(0, 2*numOfKeys+1).limit(1000).toArray();
			return randomInts;
		}else{
			Random randomGenerator = new Random();
			int[] randomInts = randomGenerator.ints(0, 2*numOfKeys+1).distinct().limit(1000).toArray();	
			return randomInts;
		}
	}
	
	//Random string generator.
	//parameter n is the length of info a)55 bytes , b)27 bytes.
	//Returns a string.
	//!!Used ASCII 48 to 122, old 32 to 126 limits caused some problems!!
	public static String getRString(int n) {
		int leftLimit = 48;  // numeral '0' 48, 32 = SPACE
	    int rightLimit = 122; // letter 'z' 122, 126 = ~
	    int targetStringLength = n; //27 , 55 length from exercise (1 char = 1 byte).
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	    return generatedString;
	}
}
