package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

public class SortJoinIterator implements RowIterator {
  private static final int  NONE     = 0;
  private static final int  LONG     = 1;
  private static final int  DOUBLE   = 2;
  private static final int  DATE     = 3;
  private static final int  STRING   = 4;
  private static final int  MISMATCH = 5;
  private final RowIterator leftIterator;
  private final RowIterator rightIterator;
  private final Integer     leftIndex;
  private final Integer     rightIndex;
  private ArrayList<Row>    stash;
  private int               stashIndex;
  private int               type;
  private Row               left;
  private Row               right;
  private Row               next;

  public SortJoinIterator(RowIterator leftIterator,
                          RowIterator rightIterator,
                          Schema leftSchema,
                          Schema rightSchema,
                          BinaryExpression expression) {
    // Acquire the columns
    Column firstColumn = (Column) expression.getLeftExpression();
    Column secondColumn = (Column) expression.getRightExpression();

    // Initialize the order as ascending
    OrderByElement leftOrder = new OrderByElement();
    leftOrder.setAsc(true);
    OrderByElement rightOrder = new OrderByElement();
    rightOrder.setAsc(true);
    
    // Acquire the correct left and right columns and their index
    Integer index = leftSchema.getIndex(firstColumn);
    if (index == null) {
      leftIndex = leftSchema.getIndex(secondColumn);
      leftOrder.setExpression(secondColumn);
      rightIndex = rightSchema.getIndex(firstColumn);
      rightOrder.setExpression(firstColumn);
    } else {
      leftIndex = index;
      leftOrder.setExpression(firstColumn);
      rightIndex = rightSchema.getIndex(secondColumn);
      rightOrder.setExpression(secondColumn);
    }
    
    // Sort the left and right inputs TODO: Move this out of here and into the node
    List<OrderByElement> orders = new ArrayList<OrderByElement>(1);
    orders.add(leftOrder);
    this.leftIterator = new MergeSortIterator(leftIterator, orders, leftSchema);
    orders = new ArrayList<OrderByElement>(1);
    orders.add(rightOrder);
    this.rightIterator = new MergeSortIterator(rightIterator, orders, rightSchema);
    
    type = NONE;
    open();
  }

  @Override
  public boolean hasNext() {
    if (stash == null) return false;
    if (leftIndex == null || rightIndex == null) {
      close();
      return false;
    }
    if (next != null) return true;
    if ((!leftIterator.hasNext() && left == null) ||
        (!rightIterator.hasNext() && right == null && stash.isEmpty())) {
      close();
      return false;
    }
    
    if (left == null) left = leftIterator.next();
    if (right == null) right = rightIterator.next();
    boolean alt = false;
    if (right == null) {
      left = leftIterator.next();
      if (left == null) {
        close();
        return false;
      }
      stashIndex = 0;
      alt = true;
      right = stash.get(stashIndex);
    }
    
    LeafValue leftValue = left.getValue(leftIndex);
    LeafValue rightValue = right.getValue(rightIndex);
//    System.out.println(new Row(left, right));
//    System.out.println(stash);
    
    if (type == NONE) {
      if (leftValue instanceof LongValue && rightValue instanceof LongValue) type = LONG;
      else if (leftValue instanceof DoubleValue && rightValue instanceof DoubleValue) type = DOUBLE;
      else if (leftValue instanceof DateValue && rightValue instanceof DateValue) type = DATE;
      else if (leftValue instanceof StringValue && rightValue instanceof StringValue) type = STRING;
      else type = MISMATCH;
    }
    
    if (type == MISMATCH) {
      close();
      return false;
    }
    
    Integer compare = compareValues(leftValue, rightValue);
    if (alt && compare == 0) compare = -1; 
    
    if (compare == null) {
      close();
      return false;
    } else if (compare == 0) {
      if (stash.isEmpty()) {
        stashIndex = 0;
      } else {
        if (compareValues(left.getValue(leftIndex), stash.get(0).getValue(rightIndex)) != 0) {
          stashIndex = 0;
          stash.clear();
        }
      }
      stash.add(right);
      right = null;
      next = new Row(left, stash.get(stashIndex));
      return true;
    } else if (compare < 0) {
      if (stash.isEmpty()) {
        left = null;
        return this.hasNext();
      } else {
        if (stashIndex >= stash.size()) {
          stashIndex = 0;
          left = null;
          return this.hasNext();
        } else {
          next = new Row(left, stash.get(stashIndex));
          return true;
        }
      }
    } else {
      right = null;
      return this.hasNext();
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
    leftIterator.close();
    rightIterator.close();
    left = null;
    right = null;
    next = null;
    stash = null;
    type = NONE;
  }

  @Override
  public void open() {
    if (stash != null) return;
    leftIterator.open();
    rightIterator.open();
    stash = new ArrayList<Row>();
  }
}
