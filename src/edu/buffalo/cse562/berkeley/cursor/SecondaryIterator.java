package edu.buffalo.cse562.berkeley.cursor;

import java.util.ArrayList;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.SecondaryDatabase;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class SecondaryIterator implements RowIterator {
  private final SecondaryDatabase secondary;
  private final ArrayList<String> types;
  private SecondaryCursor         cursor;
  private DatabaseEntry           key;
  private Row                     next;
  private OperationStatus         status;

  public SecondaryIterator(SecondaryDatabase secondary, ArrayList<String> types) {
    this.secondary = secondary;
    this.types = types;
  }
  
  @Override
  public boolean hasNext() {
    if (cursor == null) return false;
    if (key == null) return false;
    if (next != null) return true;
    DatabaseEntry data = new DatabaseEntry();
    
    if (status == null) {
      status = cursor.getSearchKey(key, data, LockMode.READ_UNCOMMITTED);
      if (status == OperationStatus.SUCCESS) {
        next = Row.readIn(data, types);
        return true;
      }
    } else if (cursor.getNextDup(key, data, LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS) {
      next = Row.readIn(data, types);
      return true;
    }
    
    this.close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row out = next;
    next = null;
    return out;
  }

  @Override
  public void close() {
    if (cursor == null) return;
    cursor.close();
    cursor = null;
    key = null;
    next = null;
    status = null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void open() {
    if (key == null) return;
    if (cursor != null) return;
    cursor = secondary.openSecondaryCursor(null, null);
  }
  
  @Override
  public void setKey(DatabaseEntry key) {
    this.key = key;
    this.open();
  }
}
