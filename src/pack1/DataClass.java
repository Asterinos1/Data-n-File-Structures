package pack1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataClass{
	private  int key;  		// Variable to hold the key of the DataClass instance.
	private  String info; 	 // Variable variable to hold the info of the DataClass instance.

	public DataClass(int key, String info) {
		this.key=key;
		this.info=info;
	}
	
	//This method returns a byte[] contains:
	//The instance's key (int) at the first 4 bytes.
	//The instance's info (string) at its remaining bytes.
	//According to each case we will get either a 59 bytes or 31 bytes length array.
	//inside the array [[key],[info]].

	// Converts DataClass instance to byte array
    // The byte array contains key (4 bytes) followed by info in ASCII encoding
	public byte[] dataClassToByteArray(){
		ByteBuffer bb = ByteBuffer.allocate(4+this.info.length());
		byte[] bArray = new byte[4+this.info.length()];
		
		bb.order(ByteOrder.BIG_ENDIAN); //default order
		bb.putInt(this.key);
		bb.put(this.info.getBytes(StandardCharsets.US_ASCII));	
		bArray = bb.array();	
					
		return bArray;
	}
	
	//getters for key and info
	public int getKey() {
		return key;
	}

	public String getInfo(){
		return info;
	}
}
