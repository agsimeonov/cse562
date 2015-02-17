package edu.buffalo.cse562.visitor;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.ProjectNode;
import edu.buffalo.cse562.parsetree.TableNode;
import edu.buffalo.cse562.parsetree.CartesianNode;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

/**
 * Parses and manages select statements.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class SelectManager implements
                          SelectVisitor,
                          FromItemVisitor,
                          SelectItemVisitor,
                          ExpressionVisitor {
  private ArrayList<DataTable>  fromTables  = new ArrayList<DataTable>();
  private ArrayList<Expression> expressions = new ArrayList<Expression>();
  private ParseTree             root;
  private List<SelectItem>      selectItems;
  private int                   selectItemsIndex;
  
  public ParseTree getRoot() {
    return root;
  }

  /* SelectVisitor */
  
  @SuppressWarnings("unchecked")
  @Override
  public void visit(PlainSelect plainSelect) {
    System.out.println(plainSelect);
    // Handle FROM relation-list
    // TODO: Should change to handle SubSelect and SubJoin
    plainSelect.getFromItem().accept(this);
    if (plainSelect.getJoins() != null) {
      for (Object o : plainSelect.getJoins()) {
        Join join = (Join) o;
        join.getRightItem().accept(this);
      }
    }
    
    // Convert SELECT wildcards to columns
    selectItems = plainSelect.getSelectItems();
    for (selectItemsIndex = 0; selectItemsIndex < selectItems.size(); selectItemsIndex++)
      selectItems.get(selectItemsIndex).accept(this);
    plainSelect.setSelectItems(selectItems);
    
    // Build the parse tree
    root = new ProjectNode(null, null);
    ParseTree fromCartesianTree = toCartesianTree(fromTables);
    fromCartesianTree.setBase(root);
    root.setLeft(fromCartesianTree);
    for (Row row : fromCartesianTree)
      System.out.println(row);
    System.out.println(plainSelect);
  }
  
  /**
   * Converts a list of data tables to a Cartesian product tree.
   * 
   * @param dataTables - list of data tables to convert
   * @return root of resulting Cartesian product tree
   */
  private ParseTree toCartesianTree(ArrayList<DataTable> dataTables) {
    // TODO: May need to change this function somewhat when adding other from types
    ParseTree root = null;
    ParseTree current = null;
    
    for (int i = 0; i < dataTables.size(); i++) {
      if (current == null) {
        if (i + 1 == dataTables.size()) {
          current = new TableNode(null, dataTables.get(i));
        } else {
          current = new CartesianNode(null);
          current.setLeft(new TableNode(current, dataTables.get(i)));
        }
        root = current;
      } else {
        if (i + 1 == dataTables.size()) {
          current.setRight(new TableNode(current, dataTables.get(i)));
        } else {
          ParseTree next = new CartesianNode(current);
          current.setRight(next);
          current = next;
          current.setLeft(new TableNode(current, dataTables.get(i)));
        }
      }
    }
    
    return root;
  }

  @Override
  public void visit(Union union) {}

  /* FromItemVisitor */
  
  @Override
  public void visit(Table table) {
    fromTables.add(TableManager.getTable(table.getName()));
  }

  @Override
  public void visit(SubSelect subSelect) {}

  @Override
  public void visit(SubJoin subJoin) {}

  /* SelectItemVisitor */
  
  /**
   * Global wildcards are resolved as table wildcards with the tables in the same order they appear
   * in the from close.  This function converts global wildcards to whole columns in the specified
   * order.
   * 
   * @param allColumns - global wildcard
   */
  @Override
  public void visit(AllColumns allColumns) {
    selectItems.remove(selectItemsIndex);
    for (DataTable fromTable : fromTables)
      tableWildcardToColumns(fromTable);
    selectItemsIndex--;
  }

  /**
   * Table wildcards are resolved in the same order the columns appear in the create table 
   * statement.  This functions converts table wildcards to whole columns in the specified order.
   * 
   * @param allTableColumns - table wildcard
   */
  @Override
  public void visit(AllTableColumns allTableColumns) {
    selectItems.remove(selectItemsIndex);
    DataTable fromTable = TableManager.getTable(allTableColumns.getTable().getName());
    tableWildcardToColumns(fromTable);
    selectItemsIndex--;
  }
  
  /**
   * Transforms selectItems table wildcards to whole columns.
   * 
   * @param fromTable - the table for which to transform
   */
  private void tableWildcardToColumns(DataTable fromTable) {
    for (Column column : fromTable.getSchema().getColumns()) {
      column.accept(this);
      expressions.add(column);
      SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
      selectExpressionItem.setExpression(column);
      selectItems.add(selectItemsIndex, selectExpressionItem);
      selectItemsIndex++;
    }
  }

  @Override
  public void visit(SelectExpressionItem selectExpressionItem) {
    Expression expression = selectExpressionItem.getExpression();
    expression.accept(this);
    expressions.add(expression);
  }
  
  /* ExpressionVisitor */

  @Override
  public void visit(NullValue nullValue) {}

  @Override
  public void visit(Function function) {
    for (Object o : function.getParameters().getExpressions()) {
      Expression expression = (Expression) o;
      expression.accept(this);
    }
  }

  @Override
  public void visit(InverseExpression inverseExpression) {}

  @Override
  public void visit(JdbcParameter jdbcParameter) {}

  @Override
  public void visit(DoubleValue doubleValue) {}

  @Override
  public void visit(LongValue longValue) {}

  @Override
  public void visit(DateValue dateValue) {}

  @Override
  public void visit(TimeValue timeValue) {}

  @Override
  public void visit(TimestampValue timestampValue) {}

  @Override
  public void visit(Parenthesis parenthesis) {
    parenthesis.getExpression().accept(this);
  }

  @Override
  public void visit(StringValue stringValue) {}

  @Override
  public void visit(Addition addition) {
    handleBinaryExpression(addition);
  }

  @Override
  public void visit(Division division) {
    handleBinaryExpression(division);
  }

  @Override
  public void visit(Multiplication multiplication) {
    handleBinaryExpression(multiplication);
  }

  @Override
  public void visit(Subtraction subtraction) {
    handleBinaryExpression(subtraction);
  }

  @Override
  public void visit(AndExpression andExpression) {
    handleBinaryExpression(andExpression);
  }

  @Override
  public void visit(OrExpression orExpression) {
    handleBinaryExpression(orExpression);
  }

  @Override
  public void visit(Between between) {}

  @Override
  public void visit(EqualsTo equalsTo) {
    handleBinaryExpression(equalsTo);
  }

  @Override
  public void visit(GreaterThan greaterThan) {
    handleBinaryExpression(greaterThan);
  }

  @Override
  public void visit(GreaterThanEquals greaterThanEquals) {
    handleBinaryExpression(greaterThanEquals);
  }

  @Override
  public void visit(InExpression inExpression) {
  }

  @Override
  public void visit(IsNullExpression isNullExpression) {
  }

  @Override
  public void visit(LikeExpression likeExpression) {
    handleBinaryExpression(likeExpression);
  }

  @Override
  public void visit(MinorThan minorThan) {
    handleBinaryExpression(minorThan);
  }

  @Override
  public void visit(MinorThanEquals minorThanEquals) {
    handleBinaryExpression(minorThanEquals);
  }

  @Override
  public void visit(NotEqualsTo notEqualsTo) {
    handleBinaryExpression(notEqualsTo);
  }

  /**
   * Makes sure a table is set for the given column.
   * 
   * @param column - given column
   */
  @Override
  public void visit(Column column) {
    if (column.getTable().toString().equals("null")) {
      for (DataTable fromTable : fromTables) {
        if (fromTable.getSchema().hasColumn(column)) {
          column.setTable(fromTable.getTable());
        }
      }
    }
  }

  @Override
  public void visit(CaseExpression caseExpression) {}

  @Override
  public void visit(WhenClause whenClause) {}

  @Override
  public void visit(ExistsExpression existsExpression) {}

  @Override
  public void visit(AllComparisonExpression allComparisonExpression) {}

  @Override
  public void visit(AnyComparisonExpression anyComparisonExpression) {}

  @Override
  public void visit(Concat concat) {
    handleBinaryExpression(concat);
  }

  @Override
  public void visit(Matches matches) {
    handleBinaryExpression(matches);
  }

  @Override
  public void visit(BitwiseAnd bitwiseAnd) {
    handleBinaryExpression(bitwiseAnd);
  }

  @Override
  public void visit(BitwiseOr bitwiseOr) {
    handleBinaryExpression(bitwiseOr);
  }

  @Override
  public void visit(BitwiseXor bitwiseXor) {
    handleBinaryExpression(bitwiseXor);
  }
  
  /**
   * Handles common operations on a BinaryExpression.
   * 
   * @param binaryExpression - BinaryExpression to handle
   */
  private void handleBinaryExpression(BinaryExpression binaryExpression) {
    binaryExpression.getLeftExpression().accept(this);
    binaryExpression.getRightExpression().accept(this);
  }
}
