package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.berkeley.cursor.IndexJoinIterator;
import edu.buffalo.cse562.iterator.HashJoinIterator;
import edu.buffalo.cse562.iterator.MergeSortIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SortJoinIterator;
import edu.buffalo.cse562.optimizer.Optimizer;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

/**
 * A node that handles simple joins.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class JoinNode extends ParseTree {
  private final Expression expression;

  /**
   * Initializes the Join node.
   * 
   * @param base - the parent node
   * @param left - left node
   * @param right - right node
   * @param expression - the join on expression statement
   */
  public JoinNode(ParseTree base, ParseTree left, ParseTree right, Expression expression) {
    super(base);
    super.left = left;
    super.right = right;
    
    // Unwrap the expression
    while (expression instanceof Parenthesis)
      expression = ((Parenthesis) expression).getExpression();
    this.expression = expression;
  }

  @Override
  public Iterator<Row> iterator() {
    BinaryExpression binaryExpression = (BinaryExpression) expression;
    
    // Get the join columns
    Column columnOne = (Column) binaryExpression.getLeftExpression();
    Column columnTwo = (Column) binaryExpression.getRightExpression();
    
    // Initialize the order as ascending
    OrderByElement leftOrder = new OrderByElement();
    leftOrder.setAsc(true);
    OrderByElement rightOrder = new OrderByElement();
    rightOrder.setAsc(true);
    
    // Initialize the correct left and right columns and their index
    Integer index = left.getSchema().getIndex(columnOne);
    int leftIndex;
    int rightIndex;
    if (index == null) {
      leftIndex = left.getSchema().getIndex(columnTwo);
      leftOrder.setExpression(columnTwo);
      rightIndex = right.getSchema().getIndex(columnOne);
      rightOrder.setExpression(columnOne);
    } else {
      leftIndex = index;
      leftOrder.setExpression(columnOne);
      rightIndex = right.getSchema().getIndex(columnTwo);
      rightOrder.setExpression(columnTwo);
    }
    
    RowIterator leftIterator = (RowIterator) left.iterator();
    RowIterator rightIterator;
    
    if (TableManager.getDbDir() != null) {
      this.setSecondaryColumn(right.getSchema().getColumn(rightIndex));
      rightIterator = (RowIterator) right.iterator();
      return new IndexJoinIterator(leftIterator, rightIterator, leftIndex);
    }
    
    rightIterator = (RowIterator) right.iterator();
    
    // Initialize the order rules
    List<OrderByElement> leftOrders = new ArrayList<OrderByElement>(1);
    List<OrderByElement> rightOrders = new ArrayList<OrderByElement>(1);
    leftOrders.add(leftOrder);
    rightOrders.add(rightOrder);
    
    ParseTree root = this;
    while (root.base != null) root = root.base;
    int i = Optimizer.getAllTypeNodes(root, TableNode.class).size();
    if (i == 3 || i == 6) {
      return new HashJoinIterator(leftIterator, rightIterator, leftIndex, rightIndex);
    }
    
    leftIterator = new MergeSortIterator(leftIterator, leftOrders, left.getSchema());
    rightIterator = new MergeSortIterator(rightIterator, rightOrders, right.getSchema());
    return new SortJoinIterator(leftIterator, rightIterator, leftIndex, rightIndex);
  }

  @Override
  public Schema getSchema() {
    return new Schema(left.getSchema(), right.getSchema());
  }

  @Override
  public String nodeString() {
    return "⋈ " + expression;
  }
  
  /**
   * Sets the secondary column
   */
  private void setSecondaryColumn(Column column) {
    ParseTree node = right;
    
    while (node.getLeft() != null)
      node = node.getLeft();
    
    ((TableNode) node).setSecondary(column);
  }
}
