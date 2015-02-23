package edu.buffalo.cse562.table;

import java.util.Comparator;
import java.util.List;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Handles row comparison used for ordering operations.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class RowComparator implements Comparator<Row> {
  private final List<OrderByElement> orderByElements;

  /**
   * Initializes the comparator by setting the order by conditions.
   * 
   * @param orderByElements - order by conditions for the comparator
   */
  public RowComparator(List<OrderByElement> orderByElements) {
    this.orderByElements = orderByElements;
  }

  @Override
  public int compare(Row left, Row right) {
    if (orderByElements == null) return 0;
    
    for (OrderByElement element : orderByElements) {
      Column column = (Column) element.getExpression();
      LeafValue thisValue = left.getValue(column);
      LeafValue rowValue = right.getValue(column);

      try {
        if (thisValue instanceof LongValue || thisValue instanceof DateValue) {
          if (thisValue.toLong() > rowValue.toLong()) {
            return element.isAsc() ? 1 : -1;
          } else if (thisValue.toLong() < rowValue.toLong()) {
            return element.isAsc() ? -1 : 1;
          }
        } else if (thisValue instanceof DoubleValue) {
          if (thisValue.toDouble() > rowValue.toDouble()) {
            return element.isAsc() ? 1 : -1;
          } else if (thisValue.toLong() < rowValue.toLong()) {
            return element.isAsc() ? -1 : 1;
          }
        } else if (thisValue instanceof StringValue) {
          if (thisValue.toString().compareTo(rowValue.toString()) > 0) {
            return element.isAsc() ? 1 : -1;
          } else if (thisValue.toString().compareTo(rowValue.toString()) < 0) {
            return element.isAsc() ? -1 : 1;
          }
        }
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }
    }

    return 0;
  }
}
