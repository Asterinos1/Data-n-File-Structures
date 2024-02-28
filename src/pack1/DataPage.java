package pack1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;

public class DataPage{
	private final static int TOTAL_PAGE_SIZE = 256; //page size
	private final static int MAINBODY_PAGE_SIZE55 = 236; //part of the page containing elements in 55 length info.
	private final static int MAINBODY_PAGE_SIZE27 = 248;	//part of the page containing elements in 27 length info.
	private int pCounter=0;  //page counter
	private int pCounter3=0;
	
	private RandomAccessFile raf1; //A) File for A, contains all N DataClass instances.
	private RandomAccessFile raf2; //B) File for B, contains all key-page couples unorganized.
	private RandomAccessFile raf3; //C) File for C, contains all key-page couples organized.

	private String fileNames[];
	
	//Constructor of DatPage takes the names of the files as parameters.
	public DataPage(String fname1, String fname2, String fname3) throws FileNotFoundException{
		this.raf1 = new RandomAccessFile(fname1, "rw");
		this.raf2 = new RandomAccessFile(fname2, "rw");
		this.raf3 = new RandomAccessFile(fname3, "rw");

		this.fileNames = new String[]{fname1, fname2, fname3};
	}
		
	//This method will write all  DataClass instances, given a list containing them.
	//The format of each page is:
	//A page of 256 bytes where the first 236 contain the DataClass instances, and the last 8 contain
	//the total amount of instances written in the first 236 bytes, as well as the current page of the file.
	//First we calculate the total number of pages needed to write all the instances. We divide the size of the list
	//with the maximum amount of records we can write in each case, then we get the module to make sure if there is going to be
	//need for an extra not full page. We get the first element of the list and check its info length
	//to find out in which case we are working on, then write each page.
	//We use 3 byte arrays in total, pageArray which is the final page (256 byte size) consisting of 2 'sub parts' arrays
	//mainBody containing the instances and then the infoBody containing the amount of instances and the current page.
	//Then we use system.arraycopy to put them in the pageArray and then write it to the file.
	//ByteBuffers are used to modify these arrays.
	public void writeTotal(List<DataClass> list) throws IOException {
		pCounter=0; //pCounter is used to keep track of the total pages written in the file so far.
		
		int totalPages, extraPage;
		
		if(list.get(0).getInfo().length()>27) {
			totalPages = list.size()/4;
			extraPage = list.size()%4;
			if(extraPage>0) {
				totalPages++;
			}
		}else {
			totalPages = list.size()/8;
			extraPage = list.size()%8;
			if(extraPage>0) {
				totalPages++;
			}
		}
	
		int maxNumOfCouples; //maxNumOfCouples contains the max amount of pages we can write to a page depending the case

		//in the case of info length 55, in a 256 byte page we can write max 4 DataClass instances (4x59=236) with 20 bytes remaining 
		//which are not enough
		//on the other case of info length 27, we can writ max 8 DataClass instances (8x31=248) with a total of 8 bytes remaining.
		if(list.get(0).getInfo().length()>27) {
			//System.out.println("~~~~~ 55 length info ~~~~~~");
			//Case 55, can max 4 instances
			maxNumOfCouples =4;
			int y=0,i=0;
			
			do {
				byte[] pageArray = new byte[TOTAL_PAGE_SIZE];
				byte[] mainBody = new byte[MAINBODY_PAGE_SIZE55];
				byte[] infoBody = new byte[8];
				
				ByteBuffer bb1 = ByteBuffer.wrap(mainBody);
				ByteBuffer bb2 = ByteBuffer.wrap(infoBody);
				ByteBuffer bb3 = ByteBuffer.wrap(pageArray);
				
				int num1 =0; //num1 is used to keep track of total records written in the page.
				
				do {
					if(i<list.size()) {
						bb1.put(list.get(i).dataClassToByteArray());
						num1++;
						i++;
					}else{
						break;
					}	
				}while(i<maxNumOfCouples && i<list.size());
				//we keep adding instances in the mainBody until the maxNumOfCouples has been reached or all the list has been written.
				maxNumOfCouples = maxNumOfCouples + 4;
				
				bb2.putInt(num1);
	
				num1=0;
				bb2.putInt(pCounter+1);  //pCounter used as indicator 
				
				System.arraycopy(mainBody, 0, pageArray, 0, mainBody.length);
				System.arraycopy(infoBody, 0, pageArray, 248, infoBody.length);
				
				raf1.seek(pCounter*256);
				raf1.write(pageArray);
	
				pCounter++;
				
				bb1.clear();
				bb2.clear();
				bb3.clear();
			
				y++;
			}while(y<totalPages);
			
		}else{
			//einai 27 length info ara xwraei 8 records.
			maxNumOfCouples =8;
			int y=0,i=0;
			
			do {
				byte[] pageArray = new byte[TOTAL_PAGE_SIZE];
				byte[] mainBody = new byte[MAINBODY_PAGE_SIZE27]; 
				byte[] infoBody = new byte[8];
				
				ByteBuffer bb1 = ByteBuffer.wrap(mainBody);
				ByteBuffer bb2 = ByteBuffer.wrap(infoBody);
				ByteBuffer bb3 = ByteBuffer.wrap(pageArray);
				
				int num1 =0; //num1 is used to keep track of total records written in the page.
				
				do {
					if(i<list.size()) {
						bb1.put(list.get(i).dataClassToByteArray());
						num1++;
						i++;
					}else{
						break;
					}
				}while(i<maxNumOfCouples && i<list.size());
				//we keep adding instances in the mainBody until
				//the maxRecords has been reached or all the list has been written.
				
				maxNumOfCouples = maxNumOfCouples + 8;
				
				bb2.putInt(num1);
				num1=0;
				bb2.putInt(pCounter+1);
				
				System.arraycopy(mainBody, 0, pageArray, 0, mainBody.length);
				System.arraycopy(infoBody, 0, pageArray, 248, infoBody.length);

				raf1.seek(pCounter*256);
				raf1.write(pageArray);
	
				pCounter++;
					
				bb1.clear();
				bb2.clear();
				bb3.clear();
			
				y++;
			}while(y<totalPages);
		}
	}
	
	//The following methods works similarly to the writeTotal method.
	//Given a list of CoupleClass instances and a specific random access file.
	//Unlike the writeTotal, we have to specify the file because this method is used for B) and C).
	//After calculating the total amount of pages needed for the file, we start writing the maximum amount
	//of CoupleClass instances inside each page. Here there is no need to keep track of how many couples have
	//be written nor the the page each couple is located.
	public void writeCouplesTotal(List<CoupleClass> list, RandomAccessFile file) throws IOException {
		pCounter3=0;
		int totalPages, extraPage;
		totalPages = list.size()/32;
		extraPage = list.size()%32;
		
		if(extraPage>0) {
			totalPages++;
		}
		
		//We know by default that a couple is 8 bytes long, 
		//therefore a 256 size page can contain max 32 couple instances.
		int maxNumOfCouples=32, y=0,i=0;	
		byte[] pageArray = new byte[TOTAL_PAGE_SIZE];
		
		do {
			ByteBuffer bb = ByteBuffer.wrap(pageArray);
							
			do {
				if(i<list.size()) {			
					bb.put(list.get(i).coupleClassToByteArray());
					i++;
				}else{
					break;
				}
			}while(i<maxNumOfCouples && i<list.size());
				
			maxNumOfCouples = maxNumOfCouples + 32;
				
			file.seek(pCounter3*256);
			file.write(pageArray);
			
			bb.clear();
	
			pCounter3++;
			y++;
			
		}while(y<totalPages);
	}
	
	//getPage is a simple method, given a random access file and a specific page inside of it
	//the getPage accesses the file and seek's to that specific page and after reading it and
	//putting inside a byte array 'output', it returns it.
	public byte[] getPage(int n, RandomAccessFile file) throws IOException {	
		byte[] output = new byte[TOTAL_PAGE_SIZE];
		ByteBuffer bb = ByteBuffer.wrap(output);
		
		file.seek(256*n);
		file.read(output);
		bb.put(output);
		
		return output;
	}

	//Methtod to remove the generated files.
	public void removeGeneratedFiles() {
		try {
			// Close the RandomAccessFile objects before deleting the files
			if (raf1 != null) {
				raf1.close();
				File file1 = new File(this.fileNames[0]);
				if (file1.exists()) {
					file1.delete();
				}
			}
			if (raf2 != null) {
				raf2.close();
				File file2 = new File(this.fileNames[1]);
				if (file2.exists()) {
					file2.delete();
				}
			}
			if (raf3 != null) {
				raf3.close();
				File file3 = new File(this.fileNames[2]);
				if (file3.exists()) {
					if (file3.delete()) {
						file3.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public RandomAccessFile getRaf1() {
		return raf1;
	}

	public RandomAccessFile getRaf2() {
		return raf2;
	}

	public RandomAccessFile getRaf3() {
		return raf3;
	}
}