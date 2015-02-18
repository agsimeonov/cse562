package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.parsetree.Evaluate;
import edu.buffalo.cse562.table.Schema;

/**
 * Abstract class which sets up common code for projection queries. 
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public abstract class ProjectIterator implements RowIterator {
  protected ArrayList<Expression>      expressions = new ArrayList<Expression>();
  protected ArrayList<String>          aliases     = new ArrayList<String>();
  protected RowIterator                iterator;
  protected List<SelectExpressionItem> items;
  protected Schema                     schema;
  protected Evaluate                   evaluate;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param items - contains expressions that should be handled
   */
  public ProjectIterator(RowIterator iterator, List<SelectExpressionItem> items) {
    ArrayList<Column> columns = new ArrayList<Column>();
    Table table = new Table();
    this.iterator = iterator;
    this.items = items;
    evaluate = new Evaluate();
    
    for (int i = 0; i < items.size(); i++) {
      Expression expression = items.get(i).getExpression();
      String alias = items.get(i).getAlias();
      if (alias == null) alias = expression.toString();
      aliases.add(alias);
      expressions.add(items.get(i).getExpression());
      columns.add(new Column(table, alias));
    }

    schema = new Schema(columns);
    open();
  }

  @Override
  public void close() {
    iterator.close();
  }
  
  @Override
  public void open() {
    iterator.open();
  }
}
