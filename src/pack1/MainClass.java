package pack1;

import java.io.IOException;
import java.util.List;
import pack2.RandomGenerator;
import pack2.SearchHandler;

public class MainClass {		
	private final static String FILE_NAMES_A[] = {"A_case27.bin", "A_case55.bin" };  //Names of files for A)
	private final static String FILE_NAMES_B[] = {"B_case27.bin", "B_case55.bin"};	//Names of files for B)
	private final static String FILE_NAMES_C[] = {"C_case27.bin", "C_case55.bin"};	//Names of files for C)
	private final static int INFO_LENGTH_CASES[] = {27,55};  //cases: 27 length info, 55 length info
	private final static int TOTAL_RECORDS[] = {50 ,100, 200, 500, 800, 1000, 2000, 5000, 10000, 50000, 100000, 200000}; //Total amount of records to be created,

	public static void main(String[] args) throws IOException{ //added exception for DataPage Class	
		long startTime, endTime;	  	//variables used to calculate the total runtime of the program.
		startTime = System.nanoTime();
		
		System.out.println("Average time for program completion: 7-7,5 minutes.");
		System.out.println("A - Serialized search.");
		System.out.println("B - Coupled search.");
		System.out.println("C - Binary search.");
		System.out.println("Starting...");
		
		//1st for loop runs 2 times in total, 1st time for case 27, 2nd time for case 55.
		for(int i=0; i<INFO_LENGTH_CASES.length; i++){

			System.out.println("*******************************");
			System.out.println("*** For info with length  " + INFO_LENGTH_CASES[i] + " **");
			System.out.println("*******************************");
			
			//2nd for loop runs for each N amount of keys requested by the exercise.
			for(int y=0; y<TOTAL_RECORDS.length; y++){
				System.out.println("Total records = " + TOTAL_RECORDS[y]);
				//Generating a record list.
				List<DataClass> recordList = RandomGenerator.createRecords(TOTAL_RECORDS[y], INFO_LENGTH_CASES[i]);
				DataPage DP = new DataPage(FILE_NAMES_A[i], FILE_NAMES_B[i], FILE_NAMES_C[i]);		

				//Create the files.
				DP.writeTotal(recordList);			
				DP.writeCouplesTotal(RandomGenerator.createCouples(recordList), DP.getRaf2());
				DP.writeCouplesTotal(RandomGenerator.organiseCouples(RandomGenerator.createCouples(recordList)), DP.getRaf3());
				
				//Search in them.
				SearchHandler.searchSerialized(DP, RandomGenerator.getSearchNumbers(TOTAL_RECORDS[y]));
				SearchHandler.searchCouple(DP, RandomGenerator.getSearchNumbers(TOTAL_RECORDS[y]));
				SearchHandler.searchBinary(DP, RandomGenerator.getSearchNumbers(TOTAL_RECORDS[y]));
				
				System.out.println(" ");

				//Reset the list.
				recordList.clear();
				//Remove the generated files.
				DP.removeGeneratedFiles();
			}

			System.out.println("*******************************");
			System.out.println("********** end here ***********");
			System.out.println("*******************************");
			System.out.println(" ");
		}

		//print time.
		endTime = System.nanoTime();
		System.out.println("Total run time:" + SearchHandler.showTime(endTime, startTime) + " seconds.");
	}
}
