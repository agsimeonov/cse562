package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.iterator.MergeSortIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SortJoinIterator;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * A node that handles simple joins.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class JoinNode extends ParseTree {
  private final Schema outSchema;
  private final int    leftIndex;
  private final int    rightIndex;
  private final List<OrderByElement> leftOrders;
  private final List<OrderByElement> rightOrders;

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
    
    // Set the output schema
    outSchema = new Schema(left.getSchema(), right.getSchema());
    
    // Unwrap the expression
    while (expression instanceof Parenthesis)
      expression = ((Parenthesis) expression).getExpression();
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
    
    // Initialize the order rules
    leftOrders = new ArrayList<OrderByElement>(1);
    rightOrders = new ArrayList<OrderByElement>(1);
    leftOrders.add(leftOrder);
    rightOrders.add(rightOrder);
  }

  @Override
  public Iterator<Row> iterator() {
    RowIterator leftIterator = (RowIterator) left.iterator();
    RowIterator rightIterator = (RowIterator) right.iterator();
    leftIterator = new MergeSortIterator(leftIterator, leftOrders, left.getSchema());
    rightIterator = new MergeSortIterator(rightIterator, rightOrders, right.getSchema());
    return new SortJoinIterator(leftIterator, rightIterator, leftIndex, rightIndex);
  }

  @Override
  public Schema getSchema() {
    return outSchema;
  }
}
