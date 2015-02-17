package edu.buffalo.cse562.iterator;

import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.parsetree.Evaluate;

public abstract class ProjectIterator implements RowIterator {
  protected RowIterator                     iterator;
  protected ArrayList<SelectExpressionItem> items;
  protected ArrayList<Expression>           expressions;
  protected ArrayList<String>               aliases;
  protected Evaluate                        evaluate;

  public ProjectIterator(RowIterator iterator, ArrayList<SelectExpressionItem> items) {
    this.iterator = iterator;
    this.items = items;
    evaluate = new Evaluate();
    
    for (int i = 0; i < expressions.size(); i++) {
      String alias = items.get(i).getAlias();
      Expression expression = items.get(i).getExpression();
      aliases.add(alias == null ? expression.toString() : alias);
      expressions.add(items.get(i).getExpression());
    }
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
