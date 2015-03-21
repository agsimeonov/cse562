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
  protected ArrayList<Expression>           inExpressions;
  protected Schema                          outSchema;
  private boolean                           allColumns = false;

  /**
   * Initializes the projection node.
   * 
   * @param base - the parent node
   * @param left - the left node
   * @param items - contains projection expressions and their aliases
   */
  public ProjectNode(ParseTree base, ParseTree left, List<SelectItem> items) {
    super(base);
    super.left = left;
    // Build expression items or return child iterator if wildcard is present
    ArrayList<SelectExpressionItem> expressionItems = new ArrayList<SelectExpressionItem>();
    
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
    
    // Determine the output schema and input expressions
    inExpressions = new ArrayList<Expression>();
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
    
    for (int i = 0; i < inExpressions.size(); i++) {
      Column column = outSchema.getColumns().get(i);
      
      // Handle wildcards
      if (column.getColumnName() == null) {
        ArrayList<Column> tableColumns = left.getSchema().getTableColumns(column.getTable());

        for (Column tableColumn : tableColumns) {
          inExpressions.add(i, tableColumn);
          outSchema.getColumns().add(i, tableColumn);
        }
        
        inExpressions.remove(i + tableColumns.size());
        outSchema.getColumns().remove(i + tableColumns.size());
        
        column = outSchema.getColumns().get(i);
      }
    }
  }

  @Override
  public Iterator<Row> iterator() {
    if (allColumns) return this.getLeft().iterator();
    
    // Determine and return the correct projection iterator
    boolean hasColumns = false;
    boolean hasFunctions = false;
    
    for (Expression expression : inExpressions) {
      if (expression instanceof Column) hasColumns = true;
      if (expression instanceof Function) hasFunctions = true;
    }
    
    Schema inSchema = left.getSchema();
    if (hasColumns && hasFunctions) {
      return new GroupByIterator((RowIterator) left.iterator(), inExpressions, inSchema);
    } else if (!hasColumns && hasFunctions) {
      return new AggregateIterator((RowIterator) left.iterator(), inExpressions, inSchema);
    } else {
      return new NonAggregateIterator((RowIterator) left.iterator(), inExpressions, inSchema);
    }
  }

  @Override
  public Schema getSchema() {
    return outSchema;
  }
}
