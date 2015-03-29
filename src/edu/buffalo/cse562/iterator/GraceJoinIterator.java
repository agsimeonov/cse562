package edu.buffalo.cse562.iterator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class GraceJoinIterator implements RowIterator {
  private static final long      THRESHOLD = 50 << 20; // minimum available memory
  private static final int       NONE      = 0;
  private static final int       LONG      = 1;
  private static final int       DOUBLE    = 2;
  private static final int       DATE      = 3;
  private static final int       STRING    = 4;
  private final RowIterator      leftIterator;
  private final RowIterator      rightIterator;
  private final int              leftIndex;
  private final int              rightIndex;
  private File                   swapDirectory;
  private HashMap<Object, InOut> leftBuffer;
  private HashMap<Object, InOut> rightBuffer;
  private boolean                bufferRight;
  private int                    type;
  private Row                    next;
  private Row                              right;
  private Iterator<Row>                    current;

  /**
   * Initializes the iterator.
   * 
   * @param leftIterator - the left iterator producing sorted results
   * @param rightIterator - the right iterator producing sorted results
   * @param leftIndex - the left index used for comparison
   * @param rightIndex - the right index used for comparison
   */
  public GraceJoinIterator(RowIterator leftIterator,
                          RowIterator rightIterator,
                          int leftIndex,
                          int rightIndex) {
    this.leftIterator = leftIterator;
    this.rightIterator = rightIterator;
    this.leftIndex = leftIndex;
    this.rightIndex = rightIndex;
    open();
  }
  
  @Override
  public boolean hasNext() {
    if (leftBuffer == null) return false;
    if (next != null) return true;
    if (leftBuffer.isEmpty()) return doesNotHaveNext();

    try {
      if (bufferRight) {
        if (rightBuffer == null || rightBuffer.isEmpty()) return doesNotHaveNext();
        for (Object key : leftBuffer.keySet()) {
          if (rightBuffer.containsKey(key)) {
            Row left = leftBuffer.get(key).pop();
            Row right = rightBuffer.get(key).pop();
            if (left == null || right == null) {
              if (left != null) leftBuffer.get(key).in.close();
              leftBuffer.put(key, null);
              rightBuffer.remove(key);
            } else {
              next = new Row(left, right);
              return true;
            }
          }
        }
      } else {
        if (current == null || !current.hasNext()) {
          while (rightIterator.hasNext()) {
            right = rightIterator.next();
            LeafValue leaf = right.getValue(rightIndex);
            Object key = getKey(leaf);
            if (leftBuffer.containsKey(key)) current = leftBuffer.get(key).list.iterator();
          }
        }
        
        if (current != null && current.hasNext() && right != null) {
          next = new Row (current.next(), right);
          return true;
        }
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return doesNotHaveNext();
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
    if (leftBuffer == null) return;
    swapDirectory = null;
    bufferRight = false;
    type = NONE;
    leftBuffer = null;
    rightBuffer = null;
    next = null;
  }
  
  @Override
  public void open() {
    if (leftBuffer != null) return;
    swapDirectory = new File(TableManager.getSwapDir());
    bufferRight = false;
    type = NONE;
    
    try {
      leftBuffer = getBuffer(leftIterator, leftIndex);
      if (bufferRight) rightBuffer = getBuffer(rightIterator, rightIndex);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Acquires the available memory.
   * 
   * @return - the available memory in bytes
   */
  private static long getAvailableMemory() {
    Runtime runtime = Runtime.getRuntime();
    return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
  }
  
  private HashMap<Object, InOut> getBuffer(RowIterator iterator, int index) throws IOException {
    HashMap<Object, InOut> buffer = new HashMap<Object, InOut>();
    
    while (iterator.hasNext()) {
      Row row = iterator.next();
      LeafValue leaf = row.getValue(index);
      
      Object key = getKey(leaf);
      
      InOut values;
      if (buffer.containsKey(key)) {
        values = buffer.get(key);
      } else {
        values = new InOut();
        buffer.put(key, values);
      }
      
      values.push(row);
    }
    
    return buffer;
  }
  
  private Object getKey(LeafValue leaf) {
    try {
      switch (type) {
        case LONG:
          return leaf.toLong();
        case DOUBLE:
          return leaf.toDouble();
        case DATE:
          return ((DateValue) leaf).getValue().getTime();
        case STRING:
        default:
          return leaf.toString();
      }
    } catch (InvalidLeaf e) {
      e.printStackTrace();
      System.exit(-1);
      return null;
    }
  }
  
  /**
   * Helper function for hasNext(), closes the iterator and returns false.
   * 
   * @return false
   */
  private boolean doesNotHaveNext() {
    close();
    return false;
  }
  
  private class InOut {
    private File               temporary;
    private ObjectInputStream  in;
    private ObjectOutputStream out;
    private LinkedList<Row>    list;

    private void push(Row row) throws IOException {
      if (getAvailableMemory() < THRESHOLD) {
        bufferRight = true;

        if (out == null) {
          temporary = File.createTempFile("tmp", null, swapDirectory);
          temporary.deleteOnExit();
          FileOutputStream fos = new FileOutputStream(temporary);
          BufferedOutputStream bos = new BufferedOutputStream(fos);
          out = new ObjectOutputStream(bos);
        }
        
        out.writeUnshared(row);
        System.gc();
      } else {
        if (list == null) list = new LinkedList<Row>();
        list.add(row);
      }
    }
    
    private Row pop() throws ClassNotFoundException, IOException {
      if (out != null) {
        out.writeObject(null);
        out.close();
        out = null;
        
        FileInputStream fis = new FileInputStream(temporary);
        BufferedInputStream bif = new BufferedInputStream(fis);
        in = new ObjectInputStream(bif);
      }
      
      if (list != null && !list.isEmpty()) {
        return list.pop();
      } else if (in != null) {
        Row row = (Row) in.readObject();
        if (row == null) {
          in.close();
          in = null;
        }
        return row;
      } else {
        return null;
      }
    }
  }
}
