package pack1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CoupleClass {
	private int key; 	//key contains the key of the given record.
	private int page;	//page contains the page of the record in which it is located inside the first file.
	
	public CoupleClass(int key, int page) {
		this.key = key;
		this.page = page;
	}
	
	//Same as in DataClass dataClassToByteArray method, small changes to adjust to CoupleClass variables.
	//This methods returns the content of this current CoupleClass key and info contents 
	//as byte array in the following form:
	//byte array: [[key][page]], total 8 bytes. 

	// Converts CoupleClass instance to byte array
    // The byte array contains key (4 bytes) followed by page index (4 bytes)
	public byte[] coupleClassToByteArray(){
		ByteBuffer bb = ByteBuffer.allocate(8);
		byte[] bArray = new byte[8];
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(this.key);
		bb.putInt(this.page);	
		bArray = bb.array();	
		return bArray;
	}
	
	//setters, getters
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
