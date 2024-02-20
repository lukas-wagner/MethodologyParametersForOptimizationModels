package systemParameterExtraction;

import java.util.*;

public class ValueWithIndex implements Comparable<ValueWithIndex> {
    double value;
    int index;

    public ValueWithIndex(double value, int index) {
        this.value = value;
        this.index = index;
    }

    @Override
    public int compareTo(ValueWithIndex other) {
        return Double.compare(this.value, other.value);
    }

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}