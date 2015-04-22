package edu.buffalo.cse562.berkeley;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

public class KeyCreator implements SecondaryKeyCreator {
  
  private int columnIndex; 
  public KeyCreator(int columnIndex) {
    this.columnIndex = columnIndex; 
  }

  @Override
  public boolean createSecondaryKey(SecondaryDatabase secondary,
                                    DatabaseEntry key,
                                    DatabaseEntry data,
                                    DatabaseEntry result) {
    

    Byte keyByte = key.getData()[7];
    Byte dataByte = data.getData()[15];

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(byteOut);
    try {
      dataOut.write(keyByte);
      dataOut.write(dataByte);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
        
    System.out.print("Data: ");
    System.out.println(Arrays.toString(data.getData()));
    result.setData(byteOut.toByteArray());
    System.out.print("result: ");
    System.out.println(Arrays.toString(result.getData()));
    return true;
  }

}
