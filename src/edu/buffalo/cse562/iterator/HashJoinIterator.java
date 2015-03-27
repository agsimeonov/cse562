package edu.buffalo.cse562.iterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import edu.buffalo.cse562.table.Row;

public class HashJoinIterator implements RowIterator {
  private static final int                 NONE   = 0;
  private static final int                 LONG   = 1;
  private static final int                 DOUBLE = 2;
  private static final int                 DATE   = 3;
  private static final int                 STRING = 4;
  private final RowIterator                leftIterator;
  private final RowIterator                rightIterator;
  private final int                        leftIndex;
  private final int                        rightIndex;
  private HashMap<Object, LinkedList<Row>> table;
  private int                              type;
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
  public HashJoinIterator(RowIterator leftIterator,
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
    if (table == null) return false;
    if (current == null && !rightIterator.hasNext()) return doesNotHaveNext(); 
    
    if (current == null) {
      while (rightIterator.hasNext()) {
        right = rightIterator.next();
        LeafValue leaf = right.getValue(rightIndex);
        Object key = getKey(leaf);
        if (table.containsKey(key)) {
          current = table.get(key).iterator();
          return true;
        }
      }
      return doesNotHaveNext();
    }
    
    return true;
  }
  
  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row next = new Row(current.next(), right);
    if (!current.hasNext()) current = null;
    return next;
  }
  
  @Override
  public void close() {
    if (table == null) return;
    table = null;
    type = NONE;
  }
  
  @Override
  public void open() {
    if (table != null) return;
    table = new HashMap<Object, LinkedList<Row>>();
    type = NONE;
    
    while (leftIterator.hasNext()) {
      Row left = leftIterator.next();
      LeafValue leaf = left.getValue(leftIndex);
      
      if (type == NONE) {
        if (leaf instanceof LongValue) type = LONG;
        else if (leaf instanceof DoubleValue) type = DOUBLE;
        else if (leaf instanceof DateValue) type = DATE;
        else type = STRING;
      }
      
      Object key = getKey(leaf);
      
      LinkedList<Row> values;
      if (table.containsKey(key)) {
        values = table.get(key);
      } else {
        values = new LinkedList<Row>();
        table.put(key, values);
      }
      values.add(left);
    }
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
}
