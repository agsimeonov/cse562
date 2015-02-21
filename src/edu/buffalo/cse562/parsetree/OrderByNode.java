package edu.buffalo.cse562.parsetree;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.iterator.OrderByIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class OrderByNode extends ParseTree {
  private final List<OrderByElement> orderByElements;

  public OrderByNode(ParseTree base, List<OrderByElement> orderByElements) {
    super(base);
    this.orderByElements = orderByElements;
  }

  @Override
  public Iterator<Row> iterator() {
    return new OrderByIterator((RowIterator) left.iterator(), orderByElements);
  }
}
