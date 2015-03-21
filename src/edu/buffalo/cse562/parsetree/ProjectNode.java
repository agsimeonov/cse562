package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
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
import edu.buffalo.cse562.table.Schema;

/**
 * A node that handles projection, be it non-aggregate, aggregate, or group by aggregate.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class ProjectNode extends ParseTree {
  protected ArrayList<SelectExpressionItem> expressionItems;
  protected Schema                          outSchema;
  private boolean                           allColumns = false;

  /**
   * Initializes the projection node.
   * 
   * @param base - the parent node
   * @param items - contains projection expressions and their aliases
   */
  public ProjectNode(ParseTree base, List<SelectItem> items) {
    super(base);
    // Build expression items or return child iterator if wildcard is present
    this.expressionItems = new ArrayList<SelectExpressionItem>();
    
    for (SelectItem item : items) {
      if (item instanceof AllColumns) {
        allColumns = true;
      } else if (item instanceof AllTableColumns) {
        AllTableColumns allTableColumns = (AllTableColumns) item;
        Table table = new Table();
        table.setName(allTableColumns.getTable().getName());
        Column column = new Column(table, null);
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
        selectExpressionItem.setExpression(column);
        expressionItems.add(selectExpressionItem);
      } else {
        expressionItems.add((SelectExpressionItem) item);
      }
    }
    
    // Determine the output schema
    ArrayList<Expression> inExpressions = new ArrayList<Expression>();
    ArrayList<Column> columns = new ArrayList<Column>();
    Table table = new Table();

    for (int i = 0; i < items.size(); i++) {
      Expression expression = expressionItems.get(i).getExpression();
      inExpressions.add(expressionItems.get(i).getExpression());

      String alias = expressionItems.get(i).getAlias();
      if (expression instanceof Column && alias == null) {
        columns.add((Column) expression);
      } else {
        if (alias == null) alias = expression.toString();
        columns.add(new Column(table, alias));
      }
    }

    outSchema = new Schema(columns);
  }

  @Override
  public Iterator<Row> iterator() {
    if (allColumns) return this.getLeft().iterator();
    
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

  @Override
  public Schema getSchema() {
    return outSchema;
  }
}
