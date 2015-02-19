package edu.buffalo.cse562.parsetree;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.iterator.AggregateIterator;
import edu.buffalo.cse562.iterator.GroupByIterator;
import edu.buffalo.cse562.iterator.NonAggregateIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

/**
 * A node that handles projection, be it non-aggregate, aggregate, or group by aggregate.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class ProjectNode extends ParseTree {
  protected List<SelectExpressionItem> items;

  /**
   * Initializes the projection node.
   * 
   * @param base - the parent node
   * @param items - contains projection expressions and their aliases
   */
  public ProjectNode(ParseTree base, List<SelectExpressionItem> items) {
    super(base);
    this.items = items;
  }

  @Override
  public Iterator<Row> iterator() {
    boolean hasColumns = false;
    boolean hasFunctions = false;
    
    for (SelectExpressionItem item : items) {
      if (item.getExpression() instanceof Column) hasColumns = true;
      if (item.getExpression() instanceof Function) hasFunctions = true;
    }
    
    if (hasColumns && hasFunctions) {
      return new GroupByIterator((RowIterator) left.iterator(), items);
    } else if (!hasColumns && hasFunctions) {
      return new AggregateIterator((RowIterator) left.iterator(), items);
    } else {
      return new NonAggregateIterator((RowIterator) left.iterator(), items);
    }
  }
}
