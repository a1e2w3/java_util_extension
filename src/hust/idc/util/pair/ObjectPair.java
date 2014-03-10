package hust.idc.util.pair;

import java.io.Serializable;

/**
 * Pair�࣬������equals��hashCode��������������Ϊӳ���Key��
 * Ӧȷ������A��B��������ӳ���Key����������equals��hashCode����
 * Pair֧��������ķ�ʽclone��ǰ��������A��B���ǿ����л��ģ�����ֻ��ǳ����
 * @author WangCong
 *
 * @param <T> ��һ�����������
 * @param <S> �ڶ������������
 */
public class ObjectPair<T, S> extends AbstractPair<T, S> implements Pair<T, S>, Serializable, Cloneable {
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
