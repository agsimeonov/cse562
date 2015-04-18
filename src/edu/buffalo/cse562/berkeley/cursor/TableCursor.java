package edu.buffalo.cse562.berkeley.cursor;

import net.sf.jsqlparser.expression.LongValue;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class TableCursor implements RowIterator {
  private final Database database;
  private Cursor cursor;
  private Row next;
  
  public TableCursor(Database database) {
    this.database = database;
  }

  @Override
  public boolean hasNext() {
    if (cursor == null) return false;
    if (next != null) return true;
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    if (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
      // TODO Convert byte array to row
      next = new Row(2);
      System.out.println(data);
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
