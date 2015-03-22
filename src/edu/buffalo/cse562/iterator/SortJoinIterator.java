package edu.buffalo.cse562.iterator;

import java.util.ArrayList;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import edu.buffalo.cse562.table.Row;

public class SortJoinIterator implements RowIterator {
  private static final int  NONE     = 0;
  private static final int  LONG     = 1;
  private static final int  DOUBLE   = 2;
  private static final int  DATE     = 3;
  private static final int  STRING   = 4;
  private static final int  MISMATCH = 5;
  private final RowIterator leftIterator;
  private final RowIterator rightIterator;
  private final int         leftIndex;
  private final int         rightIndex;
  private ArrayList<Row>    stash;
  private int               stashIndex;
  private int               type;
  private Row               left;
  private Row               right;
  private Row               next;

  public SortJoinIterator(RowIterator leftIterator,
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
    if (stash == null) return doesNotHaveNext();
    if (next != null) return true;
    if (left == null) {
      stashIndex = 0;
      left = leftIterator.next();
    }
    if (left == null) return doesNotHaveNext();
    if (right == null) right = rightIterator.next();
    if (right == null && stashIndex >= stash.size()) return doesNotHaveNext();
    
    // Acquire the correct values
    LeafValue leftValue = left.getValue(leftIndex);
    LeafValue rightValue = right == null ? null : right.getValue(rightIndex);
    
    // Set the comparison type if it wasn't already
    if (type == NONE) {
      if (leftValue instanceof LongValue && rightValue instanceof LongValue) type = LONG;
      else if (leftValue instanceof DoubleValue && rightValue instanceof DoubleValue) type = DOUBLE;
      else if (leftValue instanceof DateValue && rightValue instanceof DateValue) type = DATE;
      else if (leftValue instanceof StringValue && rightValue instanceof StringValue) type = STRING;
      else type = MISMATCH;
    }
    
    if (right == null) {
      // Finish up all rows on the left
      Row stashed = stash.get(stashIndex);
      Integer compare = compareValues(leftValue, stashed.getValue(rightIndex));
      if (compare == 0) {
        next = new Row(left, stashed);
        return true;
      } else {
        return doesNotHaveNext();
      }
    } else {
      Integer compare = compareValues(leftValue, rightValue);
      // Proceed as normal
      if (compare == 0) {
        // left == right
//        if (!stash.isEmpty()) {
//          LeafValue stashedValue = stash.get(0).getValue(rightIndex);
//          if (compareValues(leftValue, stashedValue) != 0) {
//            stash.clear();
//            stashIndex = 0;
//          }
//        }
//        
        next = new Row(left, right);
        stash.add(right);
        right = null;
        return true;
      } else if (compare < 0) {
        // left < right
        if (stashIndex >= stash.size()) {
          left = null;
          return this.hasNext();
        } else {
          next = new Row(left, stash.get(stashIndex));
          return true;
        }
      } else if (compare > 0) {
        // left > right
        right = null;
        return this.hasNext();
      } else {
        return doesNotHaveNext();
      }
    }
  }
  
  /**
   * Compares two values.
   * 
   * @param leftValue - the left value
   * @param rightValue - the right value.
   * @return 0 if they equal, negative value if leftValue < rightValue, positive value if 
   * leftValue > rightValue, null if the values could not be compared.
   */
  private Integer compareValues(LeafValue leftValue, LeafValue rightValue) {
    try {
      switch (type) {
        case LONG:
          return Long.compare(leftValue.toLong(), rightValue.toLong());
        case DOUBLE:
          return Double.compare(leftValue.toDouble(), rightValue.toDouble());
        case DATE:
          DateValue leftDate = (DateValue) leftValue;
          DateValue rightDate = (DateValue) rightValue;
          return Long.compare(leftDate.getValue().getTime(), rightDate.getValue().getTime());
        case STRING:
          return leftValue.toString().compareTo(rightValue.toString());
        case MISMATCH:
        default:
          return null;
      }
    } catch (InvalidLeaf e) {
      e.printStackTrace();
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

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row out = next;
    next = null;
    stashIndex += 1;
    return out;
  }

  @Override
  public void close() {
    if (stash == null) return;
    stash = null;
    stashIndex = 0;
    type = NONE;
    left = null;
    right = null;
    next = null;
  }

  @Override
  public void open() {
    if (stash != null) return;
    stash = new ArrayList<Row>();
    stashIndex = 0;
    type = NONE;
    left = null;
    right = null;
    next = null;
  }
}
