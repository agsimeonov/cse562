package edu.buffalo.cse562.parsetree;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.iterator.AggregateIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class ProjectNode extends ParseTree {
  protected List<SelectExpressionItem> items;

  public ProjectNode(ParseTree base, List<SelectExpressionItem> items) {
    super(base);
    this.items = items;
  }

  @Override
  public Iterator<Row> iterator() {
    // Determine here whether we have an aggregate, non aggregate, or distinct and choose correctly
//    return new NonAggregateIterator((RowIterator) left.getLeft().iterator(), null, expressions);
    return new AggregateIterator((RowIterator) left.iterator(), items);
  }
}
