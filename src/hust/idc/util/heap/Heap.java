package hust.idc.util.heap;

import hust.idc.util.Sortable;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A collection designed for holding elements by organized them as a heap 
 * which promise that each element at root must be greater than elements at its children.
 * A User-defined {@link java.util.Comparator Comparator} determined the order.  
 * Besides basic {@link java.util.Queue Queue} operations,
 * heaps provide additional rebuild, replace, sort and increaseElement, decreseElement
 * operations, and the getter and setter of the comparator.  
 *
 * <p>Heaps necessarily promise the greatest element was held at the root. A heap could be
 * used as a priority queue, or to sort a collection in a typical order in the way of heap sort
 *
 * <p>The {@link #rebuild()} method to rebuild a heap,  it need be called when the comparator was reset to
 * promise the greatest element held at heap top.  
 * 
 * <p>The {@link #sort()} method to return an element list in descending order. Exactly the order of elements 
 * was only determined by the comparator, and independent with the typical implementation. 
 * 
 * <p>The {@link #replace()}, {@link #increaseElement()} and {@link #decreasElement()} methods 
 * to replace an element with a new element.  <tt>replace()</tt> method will compare the old element 
 * and the new one to determine whether <tt>increaseElement()</tt> or <tt>decreasElement()</tt> was 
 * exactly called.  <tt>increaseElement()</tt> mathod replace the old element with a greater one and 
 * adjust the heap. If the new element was less, an <tt>IllegalArgumentException</tt> was thrown.  
 * <tt>decreasElement()</tt> was similar.
 *
 * <p><tt>Heap</tt> implementations generally do not allow insertion
 * of <tt>null</tt> elements. Even in the implementations that permit it, 
 * <tt>null</tt> should not be inserted into a <tt>Heap</tt>, as <tt>null</tt> 
 * is also used as a special return value by the <tt>poll</tt> method to
 * indicate that the heap contains no elements.
 *
 * <p><tt>Heap</tt> implementations generally do not define
 * element-based versions of methods <tt>equals</tt> and
 * <tt>hashCode</tt> but instead inherit the identity based versions
 * from class <tt>Object</tt>, because element-based equality is not
 * always well-defined for heaps with the same elements but different
 * ordering properties and organized ways.
 *
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Wang Cong
 * @version 1.2.0, 08/10/13
 * @see	    Collection
 * @see	    Queue
 * @see	    Sortable
 * @see	    AbstractHeap
 * @see	    BinaryHeap
 * @see	    BinomialHeap
 * @see	    FibonacciHeap
 * @since 1.0
 * @param <E> the element type
 */
public interface Heap<E> extends Queue<E> {
	
	/**
	 * Return the comparator.
	 * 
	 * @return the comparator
	 */
	Comparator<? super E> comparator();
	
	
	/**
	 * Replace the old element with the new element. 
	 * 
	 * @param oldElement the old element to be replaced
	 * @param newElement the new element to replace the old one
	 * @return <tt>true</tt> if the element changed.
	 * 			
	 */
	boolean replace(E oldElement, E newElement);
	
	/**
	 * Replace the old element with the new element which is greater than the old one. 
	 * @param oldElement the old element to be replaced
	 * @param newElement the new element to replace the old one
	 * @return <tt>true</tt> if the element changed.
	 */
	boolean increaseElement(E oldElement, E newElement);

	/**
	 * Replace the old element with the new element which is less than the old one. 
	 * @param oldElement the old element to be replaced
	 * @param newElement the new element to replace the old one
	 * @return <tt>true</tt> if the element changed.
	 */
	boolean decreaseElement(E oldElement, E newElement);
	
	/**
	 * Adjust the heap to keep the order of element when an element changed.
	 * It works only if the reference of the element exactly contained in the heap. 
	 * @param element
	 * @return true only if the reference of the element contains in heap and its position was changed
	 * @throws NoSuchElementException the reference of the element not contains in the heap
	 */
	boolean heaplifyAt(E element) throws NoSuchElementException;
	
//	Collection<HeapEntry<E>> entrys();
//	
//	Collection<HeapEntry<E>> roots();
	
	/**
	 * 1.An Entry to access one and exactly one element using method <tt>set(E)</tt> and <tt>element()</tt>;
	 * 2.Get the relative position of the element in the heap and using <tt>parent</tt>, <tt>child</tt>, 
	 *   <tt>leftSibling</tt>, <tt>rightSibling</tt> method to access nearby element
	 * 3.Heaplify partially, using <tt>heaplifyUp</tt> method to bubble up a greater element to the top, 
	 *   that process will be called when to add an element into the heap or to increase the key of an element.
	 *   using <tt>heaplifyDown</tt> to take a less element down to the bottom, that process will be called 
	 *   when to decrease the key of an element.
	 * 4.using <tt>equals</tt> to check whether two index are point to the same position.
	 *   
	 * @author Wang Cong
	 *
	 * @param <E> Heap Element Type
	 */
	interface HeapEntry<E> {
		
		boolean isRoot();
		
		boolean isLeaf();
		
		E element();
		
		boolean set(E element);
		
		HeapEntry<E> parent();
		
		HeapEntry<E> leftSibling();
		
		HeapEntry<E> rightSibling();
		
		HeapEntry<E> child();
		
		Collection<HeapEntry<E>> children();
		
		boolean heaplifyUp();
		
		boolean heaplifyDown();
		
		int degree();

	}

}
