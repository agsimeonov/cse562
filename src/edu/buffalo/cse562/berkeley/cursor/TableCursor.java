package edu.buffalo.cse562.berkeley.cursor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class TableCursor implements RowIterator {
  private final ArrayList<String> types;
  private final Database database;
  private Cursor cursor;
  private Row next;
  
  public TableCursor(Database database, ArrayList<String> types) {
    this.database = database;
    this.types = types;
  }

  @Override
  public boolean hasNext() {
    if (cursor == null) return false;
    if (next != null) return true;
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    if (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
      ByteArrayInputStream byteIn = new ByteArrayInputStream(data.getData());
      DataInputStream dataIn = new DataInputStream(byteIn);
      next = Row.readIn(dataIn, types);
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
    cursor = database.openCursor(null, null);
  }
}