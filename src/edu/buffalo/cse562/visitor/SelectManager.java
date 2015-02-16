package edu.buffalo.cse562.visitor;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
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
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.TableManager;

public class SelectManager implements
                          SelectVisitor,
                          FromItemVisitor,
                          SelectItemVisitor,
                          ExpressionVisitor {
  private ArrayList<DataTable> fromTables = new ArrayList<DataTable>();
  private List<SelectItem>     selectItems;
  private int                  selectItemsIndex;

  /* SelectVisitor */
  
  @SuppressWarnings("unchecked")
  @Override
  public void visit(PlainSelect plainSelect) {
    System.out.println(plainSelect);
    // Handle FROM relation-list
    plainSelect.getFromItem().accept(this);
    for (Object o : plainSelect.getJoins()) {
      Join join = (Join) o;
      join.getRightItem().accept(this);
    }
    
    // Handle SELECT target-list
    selectItems = plainSelect.getSelectItems();
    for (selectItemsIndex = 0; selectItemsIndex <= selectItems.size(); selectItemsIndex++) {
      selectItems.get(selectItemsIndex).accept(this);
    }
    plainSelect.setSelectItems(selectItems);
    System.out.println(plainSelect);
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
    
    for (DataTable fromTable : fromTables) {
      for (Column column : fromTable.getSchema().getColumns()) {
        column.accept(this);
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
        selectExpressionItem.setExpression(column);
        selectItems.add(selectItemsIndex, selectExpressionItem);
        selectItemsIndex += 1;
      }
    }
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
    
    for (Column column : fromTable.getSchema().getColumns()) {
      column.accept(this);
      SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
      selectExpressionItem.setExpression(column);
      selectItems.add(selectItemsIndex, selectExpressionItem);
      selectItemsIndex += 1;
    }
  }

  @Override
  public void visit(SelectExpressionItem selectExpressionItem) {
    Expression expression = selectExpressionItem.getExpression();
    expression.accept(this);
  }
  
  /* ExpressionVisitor */

  @Override
  public void visit(NullValue nullValue) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(Function function) {
    for (Object o : function.getParameters().getExpressions()) {
      Expression e = (Expression) o;
      e.accept(this);
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
    addition.getLeftExpression().accept(this);
    addition.getRightExpression().accept(this);
  }

  @Override
  public void visit(Division division) {
    division.getLeftExpression().accept(this);
  }

  @Override
  public void visit(Multiplication arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(Subtraction arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(AndExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(OrExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(Between arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(EqualsTo arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(GreaterThan arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(GreaterThanEquals arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(InExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(IsNullExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(LikeExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(MinorThan arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(MinorThanEquals arg0) {
    // TODO Auto-generated method =stub
    
  }

  @Override
  public void visit(NotEqualsTo arg0) {
    // TODO Auto-generated method stub
    
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
  public void visit(CaseExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(WhenClause arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(ExistsExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(AllComparisonExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(AnyComparisonExpression arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(Concat arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(Matches arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(BitwiseAnd arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(BitwiseOr arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(BitwiseXor arg0) {
    // TODO Auto-generated method stub
    
  }
}
