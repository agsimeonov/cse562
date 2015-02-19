package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.evaluate.Evaluate;
import edu.buffalo.cse562.table.Schema;

/**
 * Abstract class which sets up common code for projection queries. 
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public abstract class ProjectIterator implements RowIterator {
  protected final RowIterator                iterator;
  protected final List<SelectExpressionItem> items;
  protected ArrayList<Expression>            inExpressions;
  protected Schema                           outSchema;
  protected Evaluate                         evaluate;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param items - contains expressions and their aliases
   */
  public ProjectIterator(RowIterator iterator, List<SelectExpressionItem> items) {
    this.iterator = iterator;
    this.items = items;
    open();
  }

  @Override
  public void close() {
    iterator.close();
    inExpressions = null;
    outSchema = null;
    evaluate = null;
  }
  
  @Override
  public void open() {
    if (inExpressions != null) return;
    iterator.open();
    inExpressions = new ArrayList<Expression>();
    ArrayList<Column> columns = new ArrayList<Column>();
    Table table = new Table();
    evaluate = new Evaluate();

    for (int i = 0; i < items.size(); i++) {
      Expression expression = items.get(i).getExpression();
      inExpressions.add(items.get(i).getExpression());

      String alias = items.get(i).getAlias();
      if (expression instanceof Column && alias == null) {
        columns.add((Column) expression);
      } else {
        if (alias == null) alias = expression.toString();
        columns.add(new Column(table, alias));
      }
    }

    outSchema = new Schema(columns);
  }
}
