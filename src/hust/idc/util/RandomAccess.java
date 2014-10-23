package hust.idc.util;

import java.util.List;

public abstract class RandomAccess<E> {
	
	public abstract E get(int index);
	
	public abstract E set(int index, E value);
	
	public int minIndex(){
		return 0;
	}
	
	public abstract int maxIndex();
	
	public void swap(int index1, int index2){
		if (index1 == index2)
			return ;
		this.set(index2, this.set(index1, this.get(index2)));
	}
	
	public static <E> RandomAccess<E> getInstance(final E[] array){
		return new RandomAccess<E>() {

			@Override
			public E get(final int index) {
				// TODO Auto-generated method stub
				return array[index];
			}

			@Override
			public E set(int index, E value) {
				// TODO Auto-generated method stub
				E old = array[index];
				array[index] = value;
				return old;
			}

			@Override
			public int maxIndex() {
				// TODO Auto-generated method stub
				return array.length;
			}
			
		};
	}
	
	public static <E> RandomAccess<E> getInstance(final List<E> list){
		return new RandomAccess<E>() {

			@Override
			public E get(final int index) {
				// TODO Auto-generated method stub
				return list.get(index);
			}

			@Override
			public E set(int index, E value) {
				// TODO Auto-generated method stub
				return list.set(index, value);
			}

			@Override
			public int maxIndex() {
				// TODO Auto-generated method stub
				return list.size();
			}
			
		};
	}

}
