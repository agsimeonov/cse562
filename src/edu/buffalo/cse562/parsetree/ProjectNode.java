package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
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
  protected List<SelectItem> items;

  /**
   * Initializes the projection node.
   * 
   * @param base - the parent node
   * @param items - contains projection expressions and their aliases
   */
  public ProjectNode(ParseTree base, List<SelectItem> items) {
    super(base);
    this.items = items;
  }

  @Override
  public Iterator<Row> iterator() {
    // Build expression items or return child iterator if wildcard is present
    ArrayList<SelectExpressionItem> expressionItems = new ArrayList<SelectExpressionItem>();
    
    for (SelectItem item : items) {
      if (item instanceof AllColumns) {
        return this.getLeft().iterator();
      } else if (item instanceof AllTableColumns) {
        AllTableColumns allTableColumns = (AllTableColumns) item;
        Table table = new Table();
        table.setName(allTableColumns.getTable().getName());
        Column column = new Column(table, "*");
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
        selectExpressionItem.setExpression(column);
        expressionItems.add(selectExpressionItem);
      } else {
        expressionItems.add((SelectExpressionItem) item);
      }
    }
    
    // Determine and return the correct projection iterator
    boolean hasColumns = false;
    boolean hasFunctions = false;
    
    for (SelectExpressionItem item : expressionItems) {
      if (item.getExpression() instanceof Column) hasColumns = true;
      if (item.getExpression() instanceof Function) hasFunctions = true;
    }
    
    if (hasColumns && hasFunctions) {
      return new GroupByIterator((RowIterator) left.iterator(), expressionItems);
    } else if (!hasColumns && hasFunctions) {
      return new AggregateIterator((RowIterator) left.iterator(), expressionItems);
    } else {
      return new NonAggregateIterator((RowIterator) left.iterator(), expressionItems);
    }
  }
}
