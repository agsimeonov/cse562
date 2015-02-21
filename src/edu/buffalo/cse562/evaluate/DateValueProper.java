package edu.buffalo.cse562.evaluate;

import net.sf.jsqlparser.expression.DateValue;

/**
 * A date value that fixes bugs with evaluation of the original date value.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class DateValueProper extends DateValue {

  /**
   * Initialize the original date value.
   * 
   * @param value - a date string in the form "'YYYY-MM-DD'"
   */
  public DateValueProper(String value) {
    super(value);
  }
  
  /**
   * Returns proper long (time) value, as the original date value does not work for evaluation!
   * 
   * @return the proper long (time) value
   */
  @Override
  public long toLong() {
    return this.getValue().getTime();
  }
}
