package hust.idc.util.heap;

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
public interface HeapIndex<E> {
	boolean exists();
	
	E element();
	
	boolean set(E element);
	
	HeapIndex<E> parent();
	
	HeapIndex<E> leftSibling();
	
	HeapIndex<E> rightSibling();
	
	HeapIndex<E> child();
	
	boolean heaplifyUp();
	
	boolean heaplifyDown();
	
	int degree();

}