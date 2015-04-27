package edu.buffalo.cse562.berkeley;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.jsqlparser.expression.LeafValue;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

import edu.buffalo.cse562.table.Row;

public class KeyCreator implements SecondaryKeyCreator {
  private final ArrayList<String> types;
  private final int               index;
  ByteArrayOutputStream           byteOut = new ByteArrayOutputStream();
  DataOutputStream                dataOut = new DataOutputStream(byteOut);

  public KeyCreator(ArrayList<String> types, int index) {
    this.types = types;
    this.index = index;
  }

  @Override
  public boolean createSecondaryKey(SecondaryDatabase secondary,
                                    DatabaseEntry key,
                                    DatabaseEntry data,
                                    DatabaseEntry result) {
    Row row = Row.readIn(data, types);
    LeafValue value = row.getValue(index);
    try {
      Row.writeOutHelper(dataOut, value);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    result.setData(byteOut.toByteArray());
    byteOut.reset();
    return true;
  }
}
