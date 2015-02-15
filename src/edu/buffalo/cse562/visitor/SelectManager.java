package edu.buffalo.cse562.visitor;

import java.util.ArrayList;

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
  private ArrayList<Expression> expressions = new ArrayList<Expression>();
  private Expression            expression;
  private DataTable             fromTable;

  /* SelectVisitor */
  
  @Override
  public void visit(PlainSelect plainSelect) {
    plainSelect.getFromItem().accept(this);
    for (Object o : plainSelect.getSelectItems()) {
      SelectItem selectItem = (SelectItem) o;
      selectItem.accept(this);
    }
  }

  @Override
  public void visit(Union union) {
    // TODO Auto-generated method stub
  }

  /* FromItemVisitor */
  
  @Override
  public void visit(Table table) {
    fromTable = TableManager.getTable(table.getName());
  }

  @Override
  public void visit(SubSelect subSelect) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(SubJoin subjoin) {
    // TODO Auto-generated method stub
    
  }

  /* SelectItemVisitor */
  
  @Override
  public void visit(AllColumns allColumns) {
    AllTableColumns allTableColumns = new AllTableColumns(fromTable.getTable());
    visit(allTableColumns);
  }

  @Override
  public void visit(AllTableColumns allTableColumns) {
    for (Column column : fromTable.getSchema().getColumns()) 
      expressions.add(column);
  }

  @Override
  public void visit(SelectExpressionItem selectExpressionItem) {
    Expression expr = selectExpressionItem.getExpression();
    expr.accept(this);
    System.out.println(expression);
  }
  
  private Expression getWholeColumnExpression(Expression expression) {
    Column column;
    if (expression instanceof Column) {
      column = (Column) expression;
      return getWholeColumn(column);
    }
    return expression;
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
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(NotEqualsTo arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(Column column) {
    getWholeColumn(column);
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
  
  private Column getWholeColumn(Column column) {
    if (column.getTable().toString().equals("null")) column.setTable(fromTable.getTable());
    return column;
  }
}
