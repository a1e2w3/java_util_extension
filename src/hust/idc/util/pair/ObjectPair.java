package hust.idc.util.pair;

import java.io.Serializable;

/**
 * Pair类，重载了equals和hashCode方法，可用于作为映射的Key；
 * 应确保类型A和B都可用做映射的Key，即重载了equals和hashCode方法
 * Pair支持以深拷贝的方式clone，前提是类型A和B都是可序列化的，否则只做浅拷贝
 * @author WangCong
 *
 * @param <T> 第一个参数的类型
 * @param <S> 第二个参数的类型
 */
public class ObjectPair<T, S> extends AbstractPair<T, S> implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6086892824234304856L;
	private T first;
	private S second;
	
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
		return this.first = first;
	}

	@Override
	public S setSecond(S second) {
		// TODO Auto-generated method stub
		return this.second = second;
	}

}
