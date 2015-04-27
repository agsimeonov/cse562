package edu.buffalo.cse562.berkeley.cursor;

import java.util.ArrayList;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DiskOrderedCursor;
import com.sleepycat.je.DiskOrderedCursorConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class PrimaryIterator implements RowIterator {
  private final ArrayList<String> types;
  private final Database database;
  private DiskOrderedCursor cursor;
  private Row next;
  
  public PrimaryIterator(Database database, ArrayList<String> types) {
    this.database = database;
    this.types = types;
    this.open();
  }

  @Override
  public boolean hasNext() {
    if (cursor == null) return false;
    if (next != null) return true;
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    if (cursor.getNext(key, data, LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS) {
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
    next = null;
  }

  @Override
  public void open() {
    if (cursor != null) return;
    cursor = database.openCursor(DiskOrderedCursorConfig.DEFAULT);
  }
}
