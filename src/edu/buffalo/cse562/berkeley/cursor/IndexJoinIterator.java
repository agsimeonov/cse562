package edu.buffalo.cse562.berkeley.cursor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.sf.jsqlparser.expression.LeafValue;

import com.sleepycat.je.DatabaseEntry;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class IndexJoinIterator implements RowIterator {
  private final RowIterator       left;
  private final SecondaryIterator right;
  private final int               leftIndex;
  private boolean                 isOpen = false;
  private Row                     leftRow;
  private DatabaseEntry           key;
  private Row                     next;

  public IndexJoinIterator(RowIterator left, SecondaryIterator right, int leftIndex) {
    this.left = left;
    this.right = right;
    this.leftIndex = leftIndex;
    this.open();
  }

  @Override
  public boolean hasNext() {
    if (!isOpen) return false;
    if (next != null) return true;
    
    if (leftRow == null) {
      if (!left.hasNext()) {
        this.close();
        return false;
      }
      leftRow = left.next();
    }
    
    if (key == null) {
      key = this.getKey(leftRow);
      right.setKey(key);
    }
    
    if (!right.hasNext()) {
      leftRow = null;
      key = null;
      return this.hasNext();
    }
    
    next = new Row(leftRow, right.next());
    return true;
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
    if (!isOpen) return;
    left.close();
    right.close();
    leftRow = null;
    key = null;
    next = null;
  }

  @Override
  public void open() {
    if (isOpen) return;
    left.open();
  }
  
  private DatabaseEntry getKey(Row row) {
    DatabaseEntry out = new DatabaseEntry();
    LeafValue value = row.getValue(leftIndex);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(byteOut);
    try {
      Row.writeOutHelper(dataOut, value);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    out.setData(byteOut.toByteArray());
    return out;
  }
}
