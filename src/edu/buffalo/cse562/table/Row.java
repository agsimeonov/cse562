package edu.buffalo.cse562.table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;

/**
 * Represents a row tuple of LeafValue data elements.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Row implements Serializable {
  private static final long serialVersionUID = 3237297077216578415L;
  private LeafValue[]       values;
  private ObjectInputStream stream;

  /** 
   * Creates an empty row tuple of LeafValue data elements. 
   * 
   * @param size - row size
   */
  public Row(int size) {
    this.values = new LeafValue[size];
  }

  /**
   * Creates a new row that is the concatenation of two given row.
   * 
   * @param left - left row for concatenation
   * @param right - right row for concatenation
   */
  public Row(Row left, Row right) {
    this.values = new LeafValue[left.values.length + right.values.length];
    int i = 0;
    for (; i < left.values.length; i++)
      this.values[i] = left.values[i];
    for (int j = 0; j < right.values.length; j++, i++)
      this.values[i] = right.values[j];
  }
  
  /**
   * Sets a value.
   * 
   * @param index - index of value to set
   * @param value - given value to set
   */
  public void setValue(int index, LeafValue value) {
    this.values[index] = value;
  }
  
  /**
   * Acquires a value.
   * 
   * @param index - given index for row value
   * @return desired row value for the given column, null if the column does not exist
   */
  public LeafValue getValue(int index) {
    return this.values[index];
  }
  
  /**
   * Sets an input stream associated with the row.
   * 
   * @param stream - input stream associated with the row
   */
  public void setStream(ObjectInputStream stream) {
    this.stream = stream;
  }
  
  
  /**
   * Acquires the input stream associated with the row.
   * 
   * @return input stream associated with the row
   */
  public ObjectInputStream getStream() {
    return this.stream;
  }
  
  @Override
  public int hashCode() {
    Integer hash = 0;
    
    for (int i = 0; i < values.length; i++) {
      LeafValue value = values[i];
      
      try {
        if (value instanceof LongValue) {
          hash = Objects.hash(hash, Long.valueOf(value.toLong()));
        } else if (value instanceof DoubleValue) {
          hash = Objects.hash(hash, Double.valueOf(value.toDouble()));
        } else if (value instanceof StringValue) {
          hash = Objects.hash(hash, value.toString());
        } else if (value instanceof DateValue) {
          long time = ((DateValue) value).getValue().getTime();
          hash = Objects.hash(hash, Long.valueOf(time));
        }
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }
    }
    
    return hash;
  }
  
  @Override
  public boolean equals(Object object) {
    return this.hashCode() == object.hashCode();
  }

  @Override
  public String toString() {
    String rowString = "";

    for (int i = 0; i < values.length; i++) {
      try {
        LeafValue value = values[i];
        
        if (value == null) {
          // Do nothing
        } else if (value instanceof LongValue) {
          rowString += Long.toString(value.toLong());
        } else if (value instanceof DoubleValue) {
          rowString += Double.toString(value.toDouble());
        } else if (value instanceof StringValue) {
          String stringValue = value.toString();
          rowString += stringValue.substring(1, stringValue.length() - 1);
        } else {
          rowString += value.toString();
        }
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }

      if (i != values.length - 1) rowString += "|";
    }

    return rowString;
  }
  
  /**
   * Used during serialization. Tiny, no reflection overhead, however not very flexible.
   * 
   * @param out - the data output stream used for serialization
   * @throws IOException
   */
  public void writeOut(DataOutputStream out) throws IOException {
    for (LeafValue value : values) {
      try {
        if (value instanceof DateValue) out.writeLong(((DateValue) value).getValue().getTime());
        else if (value instanceof DoubleValue) out.writeDouble(value.toDouble());
        else if (value instanceof LongValue) out.writeLong(value.toLong());
        else out.writeUTF(value.toString());
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Used during deserialization. Tiny, no reflection overhead, however not very flexible.
   * 
   * @param in - the data input stream used for deserialization
   * @param types - used to determine leaf value types in the output row
   * @return the next row in the data input stream
   */
  public static Row readIn(DataInputStream in, ArrayList<String> types) {
    Row row = new Row(types.size());
    
    for (int index = 0; index < types.size(); index++) {
      try {
        switch (types.get(index).toLowerCase()) {
          case "int":
            row.setValue(index, new LongValue(in.readLong()));
            break;
          case "decimal":
            row.setValue(index, new DoubleValue(in.readDouble()));
            break;
          case "date":
            Date date = new Date(in.readLong());
            row.setValue(index, new DateValue("'" + date.toString() + "'"));
            break;
          default:
            row.setValue(index, new StringValue(in.readUTF()));
            break;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return row;
  }

  /**
   * Used during serialization.
   * 
   * @param stream - the object output stream used for serialization
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.writeInt(values.length);
    for (LeafValue value : values) {
      try {
        if (value == null) {
          stream.writeObject(null);
        } else if (value instanceof LongValue) {
          stream.writeObject(new Long(value.toLong()));
        } else if (value instanceof DoubleValue) {
          stream.writeObject(new Double(value.toDouble()));
        } else if (value instanceof DateValue) {
          DateValue date = (DateValue) value;
          stream.writeObject(date.getValue());
        } else {
          stream.writeObject(value.toString());
        }
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Used during deserialization.
   * 
   * @param stream - the object input stream used for deserialization
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
    int size = stream.readInt();
    values = new LeafValue[size];
    for (int i = 0; i < size; i++) {
      Object object = stream.readObject();
      
      if (object == null) {
        values[i] = null;
      } else if (object instanceof Long) {
        Long value = (Long) object;
        values[i] = new LongValue(value);
      } else if (object instanceof Double) {
        Double value = (Double) object;
        values[i] = new DoubleValue(value);
      } else if (object instanceof Date) {
        Date value = (Date) object;
        values[i] = new DateValue("'" + value.toString() + "'");
      } else {
        String value = (String) object;
        values[i] = new StringValue(value);
      }
    }
  }
}
