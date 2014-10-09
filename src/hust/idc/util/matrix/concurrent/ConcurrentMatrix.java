package hust.idc.util.matrix.concurrent;

import hust.idc.util.matrix.Matrix;
import hust.idc.util.pair.Pair;

/**
 * A {@link hust.idc.util.matrix.Matrix} providing additional atomic
 * <tt>putIfAbsent</tt>, <tt>remove</tt>, and <tt>replace</tt> methods.
 *
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a
 * {@code ConcurrentMap} as a key or value
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that object from
 * the {@code ConcurrentMap} in another thread.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 2.0
 * @author Cong Wang
 * @param <RK> the type of row keys maintained by this matrix
 * @param <CK> the type of column keys maintained by this matrix
 * @param <V> the type of mapped values
 */
public interface ConcurrentMatrix<RK, CK, V> extends Matrix<RK, CK, V> {
	/**
     * If the specified key pair is not already associated
     * with a value, associate it with the given value.
     * This is equivalent to
     * <pre>
     *   if (!matrix.containsKey(row, column))
     *       return matrix.put(row, column, value);
     *   else
     *       return matrix.get(row, column);</pre>
     * except that the action is performed atomically.
     *
     * @param row row key with which the specified value is to be associated
     * @param column column key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         <tt>null</tt> if there was no mapping for the key pair.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with the key,
     *         if the implementation supports null values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this map
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified keys or value is null,
     *         and this matrix does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified keys
     *         or value prevents it from being stored in this matrix
     *
     */
	V putIfAbsent(RK row, CK column, V value);
	
	/**
     * If the specified key pair is not already associated
     * with a value, associate it with the given value.
     * This is equivalent to
     * <pre>
     *   return putIfAbsent(keyPair.getFirst(), keyPair.getSecond(), value);</pre>
     * except that the action is performed atomically.
     *
     * @param keyPair key pair with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         <tt>null</tt> if there was no mapping for the key pair.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with the key,
     *         if the implementation supports null values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this map
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified keys or value is null,
     *         and this matrix does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified keys
     *         or value prevents it from being stored in this matrix
     *
     */
	V putIfAbsent(Pair<? extends RK, ? extends CK> keyPair, V value);
	
	/**
     * Removes the entry for a key pair only if currently mapped to a given value.
     * This is equivalent to
     * <pre>
     *   if (matrix.containsKey(row, column) && matrix.get(row, column).equals(value)) {
     *       matrix.remove(row, column);
     *       return true;
     *   } else return false;</pre>
     * except that the action is performed atomically.
     *
     * @param row row key with which the specified value is associated
     * @param column column key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return <tt>true</tt> if the value was removed
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this matrix
     * @throws ClassCastException if the key or value is of an inappropriate
     *         type for this matrix
     *         (<a href="../Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key or value is null,
     *         and this matrix does not permit null keys or values
     *         (<a href="../Collection.html#optional-restrictions">optional</a>)
     */
	boolean remove(Object row, Object column, Object value);
	
	/**
     * Removes the entry for a key pair only if currently mapped to a given value.
     * This is equivalent to
     * <pre>
     *   return remove(keyPair.getFirst(), keyPair.getSecond, value);</pre>
     * except that the action is performed atomically.
     *
     * @param keyPair key pair with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return <tt>true</tt> if the value was removed
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this matrix
     * @throws ClassCastException if the keys or value is of an inappropriate
     *         type for this matrix
     *         (<a href="../Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified keys or value is null,
     *         and this matrix does not permit null keys or values
     *         (<a href="../Collection.html#optional-restrictions">optional</a>)
     */
	boolean remove(Pair<?, ?> keyPair, Object value);
	
	/**
     * Replaces the entry for a key pair only if currently mapped to a given value.
     * This is equivalent to
     * <pre>
     *   if (matrix.containsKey(row) && matrix.get(row, column).equals(oldValue)) {
     *       matrix.put(row, column, newValue);
     *       return true;
     *   } else return false;</pre>
     * except that the action is performed atomically.
     *
     * @param row row key with which the specified value is associated
     * @param column column key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return <tt>true</tt> if the value was replaced
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this matrix
     * @throws ClassCastException if the class of a specified key or value
     *         prevents it from being stored in this matrix
     * @throws NullPointerException if a specified key or value is null,
     *         and this matrix does not permit null keys or values
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this matrix
     */
    boolean replace(RK row, CK column, V oldValue, V newValue);
    
    /**
     * Replaces the entry for a key pair only if currently mapped to a given value.
     * This is equivalent to
     * <pre>
     *   return replace(keyPair.getFirst(), keyPair.getSecond(), oldValue, newValue);</pre>
     * except that the action is performed atomically.
     *
     * @param keyPair key pair with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return <tt>true</tt> if the value was replaced
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this matrix
     * @throws ClassCastException if the class of a specified key or value
     *         prevents it from being stored in this matrix
     * @throws NullPointerException if a specified key or value is null,
     *         and this matrix does not permit null keys or values
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this matrix
     */
    boolean replace(Pair<? extends RK, ? extends CK> keyPair, V oldValue, V newValue);

    /**
     * Replaces the entry for a key pair only if currently mapped to some value.
     * This is equivalent to
     * <pre>
     *   if (matrix.containsKey(row, column)) {
     *       return matrix.put(row, column, value);
     *   } else return null;</pre>
     * except that the action is performed atomically.
     *
     * @param row row key with which the specified value is associated
     * @param column column key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key pair, or
     *         <tt>null</tt> if there was no mapping for the key pair.
     *         (A <tt>null</tt> return can also indicate that the matrix
     *         previously associated <tt>null</tt> with the key,
     *         if the implementation supports null values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this matrix
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this matrix
     * @throws NullPointerException if the specified key or value is null,
     *         and this matrix does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this matrix
     */
    V replace(RK row, CK column, V value);
    
    /**
     * Replaces the entry for a key only if currently mapped to some value.
     * This is equivalent to
     * <pre>
     *   return replace(keyPair.getFirst(), keyPair.getSecond(), value);</pre>
     * except that the action is performed atomically.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         <tt>null</tt> if there was no mapping for the key.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with the key,
     *         if the implementation supports null values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this map
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified key or value is null,
     *         and this map does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     */
    V replace(Pair<? extends RK, ? extends CK> keyPair, V value);

}
