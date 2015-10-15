package csc426;

public class Position {
	public final int line;
    public final int column;

    public Position(int line, int column) 
    {
        this.line = line;
        this.column = column;
    }

    public String toString() 
    {
        return String.valueOf(this.line) + ":" + this.column;
    }

    public int hashCode() 
    {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.column;
        result = 31 * result + this.line;
        return result;
    }

    public boolean equals(Object obj) 
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Position other = (Position)obj;
        if (this.column != other.column) {
            return false;
        }
        if (this.line != other.line) {
            return false;
        }
        return true;
    }
}
