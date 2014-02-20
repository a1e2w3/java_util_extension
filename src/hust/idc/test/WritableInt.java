package hust.idc.test;

public class WritableInt implements Comparable<WritableInt> {
	private int value;
	
	public WritableInt(int value){
		this.set(value);
	}
	
	public int get(){
		return value;
	}
	
	public void set(int value){
		this.value = value;
	}
	
	public static WritableInt newInstance(int value){
		return new WritableInt(value);
	}

	@Override
	public int compareTo(WritableInt o) {
		// TODO Auto-generated method stub
		if(o == null || value > o.value)
			return 1;
		else if(value == o.value)
			return 0;
		else
			return -1;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WritableInt other = (WritableInt) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return value + "";
	}

}
