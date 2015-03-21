package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.table.Row;

public class SortJoinIterator implements RowIterator {
  private final RowIterator          leftIterator;
  private final RowIterator          rightIterator;
  private final List<OrderByElement> orderByElements;
  private RowIterator                leftSort;
  private RowIterator                rightSort;

  public SortJoinIterator(RowIterator left, RowIterator right, Expression expression) {
    this.leftIterator = left;
    this.rightIterator = right;
    OrderByElement orderByElement = new OrderByElement();
    orderByElement.setAsc(true);
    orderByElement.setExpression(expression);
    this.orderByElements = new ArrayList<OrderByElement>();
    orderByElements.add(orderByElement);
    open();
  }
  
  @Override
  public boolean hasNext() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Row next() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void close() {
    // TODO Auto-generated method stub
  }

  @Override
  public void open() {
    // CHECK
//    leftSort = new MergeSortIterator(leftIterator)
    // TODO Auto-generated method stub
  }
}
