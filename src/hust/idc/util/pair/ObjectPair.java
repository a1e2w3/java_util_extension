package hust.idc.util.pair;

import java.io.Serializable;

/**
 * ObjectPair类，重载了equals和hashCode方法，可用于作为映射的Key；
 * 应确保类型A和B都可用做映射的Key，即重载了equals和hashCode方法
 * @author WangCong
 *
 * @param <T> 第一个参数的类型
 * @param <S> 第二个参数的类型
 */
public class ObjectPair<T, S> extends AbstractPair<T, S> implements Pair<T, S>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6086892824234304856L;
	T first;
	S second;
	
	public ObjectPair(){
		this(null, null);
	}
	
	public ObjectPair(T first, S second){
		super();
		this.first = first;
		this.second = second;
	}
	
	@Override
	public final T getFirst() {
		return first;
	}
	
	@Override
	public final S getSecond() {
		return second;
	}

	@Override
	public T setFirst(T first) {
		// TODO Auto-generated method stub
		T old = this.first;
		this.first = first;
		return old;
	}

	@Override
	public S setSecond(S second) {
		// TODO Auto-generated method stub
		S old = this.second;
		this.second = second;
		return old;
	}

}
