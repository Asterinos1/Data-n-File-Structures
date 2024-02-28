package pack2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import pack1.CoupleClass;
import pack1.DataClass;
import pack1.DataPage;

public class SearchHandler {
	//Class SearchHandlers contains all search methods and their components for A) B) C)
	//Serialized search. index file serialized search and index file binary search.
	
	//***BASIC SEARCHING METHODS***

	//A)Serialized search, prints the results of the search.
	//Requires DataPage object to access the file for reading.
	//Parameter int[] array contains the generated numbers we will search for.
	public static void searchSerialized(DataPage dp, int[] array) throws IOException {
		float totalAccess=0, totalTime=0;
		int[] finalResults = new int[2]; //this array is going to contain the total amount of:
													//disc accesses for search at index 0
		for(int i=0; i<array.length; i++) {			//time needed for search at index 1
			finalResults=searchSerializedHelper(dp,array[i]);
			totalAccess=totalAccess+finalResults[0];
			totalTime=totalTime+finalResults[1];
		}
		
		System.out.println("A) Average disc access:" + showAvAccess(totalAccess) 
		+ " with average time elapsed:" +showAvTime(totalTime) + " nanoseconds.");
	}
 
	//This method requires the DataPage object to access the file for reading.
	//Parameter num is the number we are looking for.
	//returns the total disk accesses and the total time needed 
	//in an array, index 0 disk accesses, index 1 time
	public static int[] searchSerializedHelper(DataPage dp, int num) throws IOException {
		int discAccess=1;
		//First calculate total number of pages
		int maxPages=getNumberOfPages(dp.getRaf1());

		long startTime, endTime, time1;
		int[] results = new int[2];
		
		//Search each page of the file by calling getPage
		startTime=System.nanoTime();

		for(int i=0; i<maxPages; i++) { //removed key, added num
			if(searchPage(dp.getPage(i,dp.getRaf1()), num)==true) {
				break; 	//When found, stop searchin the remaining file pages.
			}else{
				discAccess++;
			}
		}	
		endTime=System.nanoTime();
		time1 = endTime-startTime; //get the time needed
		//fill the results in the array.
		results[0]=discAccess;
		results[1]=(int)time1;
	
		return results;
	}
	
	//B)index file serialized search, prints the results of the search.
	//Requires DataPage object to access the file for reading.
	//Parameter int[] array contains the generated numbers we will search for.
	public static void searchCouple(DataPage dp, int[] array) throws IOException {
		float totalAccess=0, totalTime=0;
		int[] finalResults = new int[2]; //same as in search()

		for(int i=0; i<array.length; i++) {	
			finalResults=searchCoupleHelper(dp,array[i]);
			totalAccess=totalAccess+finalResults[0];
			totalTime=totalTime+finalResults[1];
		}
		
		System.out.println("B) Average disc access:" + showAvAccess(totalAccess) 
		+ " with average time elapsed:" +showAvTime(totalTime) + " nanoseconds.");
	}

	//the following method is a component of the searchCouple() method.
	//It takes a DataPage object and the number we are looking for as parameters.
	//First we calculate how many pages the file contains, then we search every page using the method
	//searchPageCouple which, if the elements is found, returns the page location of the key. If not found 
	//the method returns 0 (invalid number since page count starts at 1.).
	//After we get the page location, we use the method searchPage where we look at the specific 
	//page we found above to actually access the DataClaa instance on the first file (A).
	//After finishing this methods returns the total accesses and the total time for the current search.
	public static int[] searchCoupleHelper(DataPage dp, int num) throws IOException{
		int discAccess=1;
		int pageLocation=0;
		long startTime, endTime, time1;
		int[] results = new int[2];
		int maxPages=getNumberOfPages(dp.getRaf2());
		
		startTime=System.nanoTime();
		for(int i=0; i<maxPages; i++) { //removed key, added num
			pageLocation=searchPageCouple(dp.getPage(i,dp.getRaf2()), num);
			if(pageLocation!=0) {
				if(searchPage(dp.getPage(pageLocation,dp.getRaf1()), num)==true) {
					discAccess++;
				}
				break;
			}else{
				discAccess++;
			}
		}	
		endTime=System.nanoTime();
		time1 = endTime-startTime;
		
		results[0]=discAccess;
		results[1]=(int)time1;
		return results;
	}	
		
	//C)index file binary search, prints the results of the search.
	//Requires DataPage object to access the file for reading.
	//Parameter int[] array contains the generated numbers we will search for.
	public static void searchBinary(DataPage dp, int[] array) throws IOException {	
		float totalAccess=0, totalTime=0; 
		int[] finalResults = new int[2];	//same as in searchSerialized()
		
		for(int i=0; i<array.length; i++) {
			finalResults=searchBinaryHelper(dp, array[i]);
			totalAccess=totalAccess+finalResults[0];
			totalTime=totalTime+finalResults[1];
		}
		
		System.out.println("C) Average disc access:" + showAvAccess(totalAccess) 
		+ " with average time elapsed:" +showAvTime(totalTime) + " nanoseconds.");
	}

	//This method uses a basic binary search algorithm taken from the Internet.
	//This method requires the DataPage object to access the file for reading.
	//Parameter n is the number we are looking for.
	public static int[] searchBinaryHelper(DataPage dp, int n) throws IOException {	
		int maxPages=getNumberOfPages(dp.getRaf3());	//get the total amount of pages of the file.
		
		int first=0, last=maxPages-1;
		int curPage=(first+last)/2;
		
		long startTime, endTime, time1;
		int[] results = new int[2];
		
		int pageLocation=0, discAccess=0;
		//pageLocation: the page where the key we are looking for is located at.
		//discAccess total number of discAccess	
		
		startTime=System.nanoTime();
		while(first<=last){
			byte[] arrayPage = new byte[256];
			arrayPage = dp.getPage(curPage, dp.getRaf3());		
			ByteBuffer bb = ByteBuffer.wrap(arrayPage);
			pageLocation=searchPageCouple(dp.getPage(curPage, dp.getRaf3()), n);
			discAccess++;
			
			if(pageLocation!=0){
				discAccess++;	
				if(searchPage(dp.getPage(pageLocation+1, dp.getRaf1()), n)==true) {
					discAccess++;
		
				}
				break;		
			}else if(pageLocation==0){
				if(n<bb.getInt(0)) {
					first = curPage+1;
				}else if(bb.getInt(252)<n) {
					last = curPage-1;
				}
			}
			curPage=(first+last)/2;
			if(curPage==0) {
				break;
			}
			bb.clear();
		}
		endTime=System.nanoTime();
		time1 = endTime-startTime;//get the time needed

		//fill the results in the array.
		results[0]=discAccess;
		results[1]=(int)time1;
	
		return results;
	}
	
	//The following method takes a random access file as a parameter.
	//It opens the file and after getting its size in bytes it calculates how many pages it consists of
	//by diving it with the page size in bytes (Given page size: 256 bytes).
	public static int getNumberOfPages(RandomAccessFile raf3) throws IOException {		
		long fileSize = raf3.length();
		long totalPages = fileSize/256, extraPage = fileSize%256;	
		
		if(extraPage>0) {
			totalPages++;
		}	
		//System.out.println("Size of file:"+fileSize + ", pages need:" + (int)totalPages);	
		return (int)totalPages;
	}
	
	//The searchPage takes a byte array (page) and the number we look for in a specific page.
	//After checking the number of DataClass instances located in the first page
	//(position 248-249-250-251, these 4 bytes contain the number (int) of elements written in this page.
	//if this number is equal or greater to 5 then we know we are at case of info len 27, else case of info len 55)
	//27 info -> 31 dataclass instance len -> max 8 instances in a page.
	//55 info -> 59 dataclass instance len -> max 4 instances in a page.
	//After we know in which case we are working on we create an array of DataClass instances which we fill with all the instances
	//contained in the page we are currently looking at, then search the array for the number.
	//If found searchPage returns true, else false.
	public static boolean searchPage(byte[] array, int n) {
		ByteBuffer bb = ByteBuffer.wrap(array);
		
		if(bb.getInt(248)>=5){
			//System.out.println("Case 27");
			
			byte[] info = new byte[27];
			DataClass records[] = new DataClass[8];
			
			
			for(int i=0; i<8; i++) {
				int key = bb.getInt(31*i);
				
				//System.out.println("Key is:" + key);
				
				System.arraycopy(array, 31*i+4, info, 0, 27);
				//bb.get(info, 0, info.length);
				
				String infoStr = new String(info, StandardCharsets.UTF_8);
				records[i] = new DataClass(key, infoStr);
				//System.out.println("Record key:" + records[i].getKey()+" and info:" + records[i].getInfo());
			}		
			
			for(int i=0; i<8; i++) {
				if(records[i].getKey()==n) {
					return true;
				}
			}
		}else{
			
			byte[] info = new byte[55];
			DataClass records[] = new DataClass[4];
			//System.out.println("Case 55");
			
			for(int i=0; i<4; i++) {
				int key = bb.getInt(59*i);
				
				//bb.get(info, 59*i+4, 55);
				System.arraycopy(array, 59*i+4, info, 0, 55);
				String infoStr = new String(info, StandardCharsets.UTF_16);
				records[i] = new DataClass(key, infoStr);
			}		
			
			for(int i=0; i<4; i++) {
				if(records[i].getKey()==n) {
				//	System.out.println("Found!");
					return true;
				}
			}
			
		}
		return false;
	}
	
	//This methods works similarly to searchPage.
	//it takes a byte array (page) and the number we are currently looking for as parameters.
	//We turn the CoupleClass instances, written in bytes, contained in the page to an array of CoupleClass instances
	//and then we check every object in the array to find the key we are looking for. If found the method returns the page where
	//the key belongs to. If not found the method returns 0 which is invalid page location since pages in the first file start with 1.
	public static int searchPageCouple(byte[] array, int n) {
		ByteBuffer bb = ByteBuffer.wrap(array);
		int key, page, flag=0;
		CoupleClass list[] = new CoupleClass[32];
		
		for(int i=0; i<32; i++) {
			key=bb.getInt(i*8);
			page=bb.getInt(i*8+4);
			
			list[i] = new CoupleClass(key,page);
		}
		for(int y=0; y<list.length; y++){
			if(list[y].getKey()==n){
				flag=1;
				return list[y].getPage();
			}
		}
		if(flag==0) {
			//System.out.println("It's not here.");
			return 0;
		}
		return 0;
	}
	
	//This method takes as parameter 2 long vars 
	//These are supposed to contain time in nanoseconds
	//It returns a string of the time difference in seconds.
	//***Not used in the final build**
	public static String showTime(long a, long b) {
		float diff2 = (float) a-b;
		diff2 = diff2/1000000000;
		String formatted = String.format("%.4f", diff2);
		return formatted;
	}
	
	//This method takes the total number of disc access
	//Calculates the average and returns it as formatted string of 2 decimals
	public static String showAvAccess(float a) {
		float num = a/1000;
		String formatted = String.format("%.2f", num);
		return formatted;
	}
	
	//This method takes the total time
	//Calculates the average and returns it as formatted string of 2 decimals
	public static String showAvTime(float a) {
		float num = a/1000;
		String formatted = String.format("%.2f", num);
		return formatted;
	}
}