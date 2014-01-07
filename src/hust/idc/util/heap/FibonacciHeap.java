package hust.idc.util.heap;

import hust.idc.util.EmptyIterator;
import hust.idc.util.Mergeable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FibonacciHeap<E> extends AbstractHeap<E> implements Mergeable<FibonacciHeap<? extends E>> {
	private FibonacciHeapNode entry;
	private Set<FibonacciHeapNode> nodes;
	
	private static final double Ln_Phi = Math.log((Math.sqrt(5d) + 1) / 2);

	public FibonacciHeap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Collection<? extends E> elements) {
		super(elements);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(E element, Comparator<? super E> comparator) {
		super(element, comparator);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(E element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean rebuild() {
		// TODO Auto-generated method stub
		FibonacciHeap<E> newHeap = new FibonacciHeap<E>(this.getComparator());
		Iterator<E> iterator = this.iterator();
		while(iterator.hasNext()){
			newHeap.offer(iterator.next());
		}
		newHeap.consolidate(null);
		this.clear();
		this.entry = newHeap.entry();
		this.nodes = newHeap.nodes;
		return false;
	}

	@Override
	protected FibonacciHeap<E> copy() {
		// TODO Auto-generated method stub
		FibonacciHeap<E> newHeap = new FibonacciHeap<E>(this.getComparator());
		if(this.entry == null){
			return newHeap;
		}
		newHeap.entry = copyHeap(this.entry(), newHeap.new FibonacciHeapNode(null));
//		newHeap.entry = copySubHeap(this.entry(), newHeap.new FibonacciHeapNode(null));
//		FibonacciHeapNode current = this.entry().rightSibling();
//		while(current != this.entry()){
//			FibonacciHeapNode newRoot = copySubHeap(current, newHeap.new FibonacciHeapNode(null));
//			newHeap.insertSubHeap(null, newRoot);
//			current = current.rightSibling();
//		}
		newHeap.nodes = newHeap.getNodeSet(newHeap.entry(), null);
//		System.out.println("new heap size : " + newHeap.size());
		return newHeap;
	}
	
	private Set<FibonacciHeapNode> getNodeSet(FibonacciHeapNode root, Set<FibonacciHeapNode> nodeSet){
		if(root == null)
			return null;
		if(nodeSet == null)
			nodeSet = new HashSet<FibonacciHeapNode>();
		FibonacciHeapNode current = root, next = null;
		nodeSet.add(current);
		while(current != null){
			next = current.child();
			if(next != null){
				current = next;
				nodeSet.add(current);
				continue;
			} 
			next = current.rightSibling();
			FibonacciHeapNode entry = current.parent() == null ? this.entry() : current.parent().child();
			if(next != entry){
				current = next;
				nodeSet.add(current);
				continue;
			} else {
				FibonacciHeapNode ancestor = current.parent();
				boolean found = false;
				while(ancestor != null){
					next = ancestor.rightSibling();
					entry = ancestor.parent() == null ? this.entry() : ancestor.parent().child();
					if(next != entry){
						current = next;
						nodeSet.add(current);
						found = true;
						break;
					}
					ancestor = ancestor.parent();
				}
				if(found){
					continue;
				}
				current = null;
			}
		}
		
		return nodeSet;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if(e == null)
			return false;
		FibonacciHeapNode newNode = new FibonacciHeapNode(e);
		this.entry = this.insertSubHeap(null, newNode);
		if(this.nodes == null){
			this.nodes = new HashSet<FibonacciHeapNode>();
		}
		this.nodes.add(newNode);
		return true;
	}

	@Override
	public E poll() {
		return this.poll(true);
	}
	
	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		try{
			@SuppressWarnings("unchecked")
			FibonacciHeapNode node = (FibonacciHeapNode) this.indexOf((E) o);
			if(this.nodes != null)
				this.nodes.remove(node);
			return this.removeNode(node);
		} catch(Exception e){
			return false;
		}
	}
	
	private E poll(boolean consolidate){
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return null;
		FibonacciHeapNode poll = this.entry();
		if(this.entry().leftSibling() == this.entry())
			this.entry = null;
		else
			this.entry = this.entry().leftSibling();
		
		//remove peek node
		this.cutSubHeap(poll);
		this.nodes.remove(poll);
		
		if(poll.child() != null){
			FibonacciHeapNode child = poll.child();
			child.parent = null;
			FibonacciHeapNode current = child.rightSibling();
			while(current != child){
				current.parent = null;
				current = current.rightSibling();
			}
			
			if(this.entry() == null)
				this.entry = child;
			else{
				current = child.leftSibling();
				child.left = this.entry();
				current.right = this.entry().rightSibling();
				this.entry.right.left = current;
				this.entry.right = child;
			}
		}
		
		// merge root list
		if(consolidate)
			this.entry = this.consolidate(null);
		
		this.entry = this.peekNode();
		return poll.element();
	}
	
	public void consolidate(){
		this.consolidate(null);
		this.entry = this.peekNode();
	}

	private FibonacciHeapNode consolidate(FibonacciHeapNode parent){
		FibonacciHeapNode entry = parent == null ? this.entry() : parent.child();
		if(entry == null)
			return entry;
		if(entry.rightSibling() == entry)
			return entry;
		
		int size = this.size();
//		System.out.println("size : " + size);
		int maxDegree = size == 0 ? 1 : (int) (Math.log(size) / Ln_Phi) + 2;
	    FibonacciHeapNode y = null;
	    ArrayList<FibonacciHeapNode> array = new ArrayList<FibonacciHeapNode>(maxDegree);
	    ArrayList<FibonacciHeapNode> rootList = new ArrayList<FibonacciHeapNode>();
	    for(int i = 0; i < maxDegree; ++i)
	    	array.add(null);
	    FibonacciHeapNode current = entry.rightSibling();
	    rootList.add(entry);
	    while(current != entry){
	    	rootList.add(current);
	    	current = current.rightSibling();
	    }

	    //合并相同度的根节点，使每个度数的二项树唯一
	    for(int i = 0; i < rootList.size(); ++i){
	    	current = rootList.get(i);
	    	if(current.parent() != parent)
	    		continue;
	    	while(array.get(current.degree()) != null){
	    		y = array.get(current.degree());
	    		if(this.compare(y, current) > 0){
	    			//swap current and y
	    			FibonacciHeapNode temp = current;
	    			current = y;
	    			y = temp;
	    		}
	    		if(y == entry)
	    			entry = current;
	    		this.fibonacciLink(current, y);
	    		array.set(current.degree() - 1, null);
	    	}
	    	array.set(current.degree(), current);
	    }
	    
	    return entry;
	}
	
	private void cutSubHeap(FibonacciHeapNode root){
		if(root == null)
			return ;
		if(root.leftSibling() == root){
			if(root.parent() == null){
				this.entry = null;
			}
			else{
				root.parent.child = null;
				root.parent.degree = 0;
			}
			return ;
		} else if(root.parent() != null){
			if(root.parent.child == root){
				root.parent.child = root.leftSibling();
			}
			--root.parent.degree;
		}
		
		root.left.right = root.rightSibling();
		root.right.left = root.leftSibling();
		if(root == this.entry()){
			this.entry = root.leftSibling();
			this.entry = this.peekNode();
		}
		
		root.left = root;
		root.right = root;
	}
	
	private FibonacciHeapNode insertSubHeap(FibonacciHeapNode parent, FibonacciHeapNode root){
		FibonacciHeapNode entry = parent == null ? this.entry() : parent.child();
		if(root == null)
			return entry;
		
		root.parent = parent;
		if(entry == null){
			if(parent == null){
				this.entry = root;
				return this.entry();
			}
			else{
				parent.child = root;
				root.left = root.right = root;
				parent.degree = 1;
				return parent.child();
			}
		}
		
		root.left = entry;
		root.right = entry.rightSibling();
		entry.right.left = root;
		entry.right = root;
		
		if(parent != null){
			++parent.degree;
		} else {
			if(this.compare(root, this.entry()) > 0){
				this.entry = root;
				return this.entry();
			}
		}
		return entry;
	}
	
	private void fibonacciLink(FibonacciHeapNode parent, FibonacciHeapNode child){
		if(parent == null || child == null)
			return ;

		this.cutSubHeap(child);
		
//		child.parent = parent;
//		FibonacciHeapNode entryChild = parent.child();
//		if(entryChild == null){
//			parent.child = child;
//			child.left = child.right = child;
//			parent.degree = 1;
//		} else {
//			child.left = entryChild;
//			child.right = entryChild.right;
//			entryChild.right.left = child;
//			entryChild.right = child;
//			++parent.degree;
//		}
		this.insertSubHeap(parent, child);
		child.mark = false;
	}
	
	private FibonacciHeapNode peekNode(){
		if(this.isEmpty())
			return null;
		FibonacciHeapNode peek = this.entry(), current = this.entry().leftSibling();
		while(current != this.entry()){
			if(this.compare(peek, current) < 0){
				peek = current;
			}
			current = current.leftSibling();
		}
		return peek;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return this.entry() == null ? null : this.entry().element();
	}

	@Override
	public boolean increaseElement(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if(result > 0){
			throw new IllegalArgumentException("new element is less than old element");
		} else if(result == 0){
			if(oldElement.equals(newElement))
				return false;
		}
		FibonacciHeapNode index = (FibonacciHeapNode) this.replaceNotHeaplify(oldElement, newElement);
		if(index == null)
			return false;
		
		if(result < 0){
			FibonacciHeapNode parent = index.parent();
			if(parent == null)
				return true;
			else if(this.compare(parent, index) > 0)
				return true;
			
			this.cutSubHeap(index);
			this.insertSubHeap(null, index);
			index.mark = false;
			this.cascadeCut(parent);
		}
		return true;
	}
	
	private void cascadeCut(FibonacciHeapNode node){
		FibonacciHeapNode parent = node.parent();
		if(parent != null){
			if(node.mark == false){
				node.mark = true;
			} else {
				this.cutSubHeap(node);
				this.insertSubHeap(null, node);
				node.mark = false;
				this.cascadeCut(parent);
			}
		}
	}

	@Override
	protected AbstractHeapIndex indexOf(E element) {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return null;
		
		AbstractHeapIndex index = this.find(element, this.entry());
		if(index != null)
			return index;
		FibonacciHeapNode root = this.entry().rightSibling();
		while(root != this.entry()){
			index = this.find(element, root);
			if(index != null)
				return index;
			root = root.rightSibling();
		}
		return null;
	}

	@Override
	protected FibonacciHeapNode entry() {
		// TODO Auto-generated method stub
		return this.entry;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return this.emptyIterator();
		return new FibonacciHeapIterator();
	}

	@Override
	public int size(){
		return (this.nodes == null || this.entry() == null) ? 0 : this.nodes.size();
	}
	
//	@Override
//	public void clear() {
//		// TODO Auto-generated method stub
//		if(this.nodes != null){
//			this.nodes.clear();
//			this.nodes = null;
//		}
//		this.entry = null;
//	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.entry == null || this.nodes == null || this.nodes.isEmpty();
	}

	@Override
	public boolean union(FibonacciHeap<? extends E> otherElem){
		// TODO Auto-generated method stub
		if(otherElem == null)
			return false;
		FibonacciHeapNode newEntry = copyHeap(otherElem.entry(), this.new FibonacciHeapNode(null));
		// this.nodes might be null
		this.nodes = this.getNodeSet(newEntry, this.nodes);
		return this.unionInternal(null, newEntry);
	}
	
	private static <E> FibonacciHeap<E>.FibonacciHeapNode copyHeap(FibonacciHeap<? extends E>.FibonacciHeapNode entry, FibonacciHeap<E>.FibonacciHeapNode newEntry){
		if(entry == null || newEntry == null)
			return null;
		FibonacciHeap<E>.FibonacciHeapNode newNode = newEntry.copyNode();
		newEntry = copySubHeap(entry, newEntry);
		
		FibonacciHeap<? extends E>.FibonacciHeapNode current = entry.rightSibling();
		while(current != entry){
			FibonacciHeap<E>.FibonacciHeapNode newCurrent = copySubHeap(current, newNode.copyNode());
			newCurrent.left = newEntry;
			newCurrent.right = newEntry.rightSibling();
			newEntry.right.left = newCurrent;
			newEntry.right = newCurrent;
			
			current = current.rightSibling();
		}

		// shallow copy of element, 
		// if want to deep copy the element, clone the element in method
		// FibonacciHeapNode.element() or FibonacciHeapNode.set()
		newEntry.set(entry.element());
		newEntry.degree = entry.degree();
		return newEntry;
	}
	
	private static <E> FibonacciHeap<E>.FibonacciHeapNode copySubHeap(FibonacciHeap<? extends E>.FibonacciHeapNode root, FibonacciHeap<E>.FibonacciHeapNode newRoot){
		if(root == null || newRoot == null)
			return null;
		newRoot.parent = null;
		if(root.child() != null){
			newRoot.child = copySubHeap(root.child(), newRoot.copyNode());
			newRoot.child.parent = newRoot;
			FibonacciHeap<? extends E>.FibonacciHeapNode current = root.child().rightSibling();
			FibonacciHeap<E>.FibonacciHeapNode newCurrent = newRoot.child();
			while(current != root.child()){
				FibonacciHeap<E>.FibonacciHeapNode newChild = copySubHeap(current, newRoot.copyNode());
				newChild.parent = newRoot;
				newChild.left = newCurrent;
				newChild.right = newCurrent.rightSibling();
				newCurrent.right.left = newChild;
				newCurrent.right = newChild;
				newCurrent = newChild;
				current = current.rightSibling();
			}
		}

		// shallow copy of element, 
		// if want to deep copy the element, clone the element in method
		// FibonacciHeapNode.element() or FibonacciHeapNode.set()
		newRoot.set(root.element());
		newRoot.degree = root.degree();
		newRoot.mark = root.mark;
		return newRoot;
	}

	private boolean unionInternal(FibonacciHeapNode parent, FibonacciHeapNode otherEntry) {
		// TODO Auto-generated method stub
		// merge root list
		if(otherEntry == null)
			return false;
		
		if(this.entry() == null){
			if(parent == null){
				this.entry = otherEntry;
			} else{
				parent.child = otherEntry;
			}
		} else {
			FibonacciHeapNode entry = parent == null ? this.entry() : parent.child();
			FibonacciHeapNode current = otherEntry.rightSibling();
			current.parent = parent;
			if(parent!= null)
				++parent.degree;
			while(current != otherEntry){
				current.parent = entry.parent();
				if(parent!= null)
					++parent.degree;
				current = current.rightSibling();
			}
			
			FibonacciHeapNode tail = otherEntry.leftSibling();
			otherEntry.left = entry;
			tail.right = entry.rightSibling();
			entry.right.left = tail;
			entry.right = otherEntry;
		}
		
		return true;
	}
	
	private boolean removeNode(FibonacciHeapNode node){
		if(node == null)
			return false;

		this.cutSubHeap(node);
		
		if(node.child() != null){
			// insert node's children to root list
			FibonacciHeapNode child = node.child();
			child.parent = null;
			FibonacciHeapNode current = child.rightSibling();
			while(current != child){
				current.parent = null;
				current = current.rightSibling();
			}
			
			if(this.entry() == null)
				this.entry = child;
			else{
				current = child.leftSibling();
				child.left = this.entry();
				current.right = this.entry().rightSibling();
				this.entry.right.left = current;
				this.entry.right = child;
			}
		}
		
		if(node.parent != null){
			this.cascadeCut(node.parent());
		}
		this.consolidate(null);
		this.entry = this.peekNode();
		return true;
	}
	
//	@Override
//	public String toString(){
//		return this.sort().toString() + " / " + super.toString();
//	}
	
	private class FibonacciHeapNode extends AbstractHeapIndex{
		private E element;
		private FibonacciHeapNode parent, child, left, right;
		private int degree = 0;
		private boolean mark = false;
		
		private FibonacciHeapNode(E element){
			this.element = element;
			this.parent = null;
			this.child = null;
			this.left = this;
			this.right = this;
		}
		
		private FibonacciHeapNode copyNode(){
			return new FibonacciHeapNode(this.element());
		}

		@Override
		public boolean exists() {
			// TODO Auto-generated method stub
			FibonacciHeapNode root = this.getRoot();
			FibonacciHeapNode rootCur = FibonacciHeap.this.entry();
			while(rootCur != root && rootCur.rightSibling() != FibonacciHeap.this.entry()){
				rootCur = rootCur.rightSibling();
			}
			return root == rootCur;
		}
		
		private FibonacciHeapNode getRoot(){
			FibonacciHeapNode ancestor = this;
			while(ancestor.parent() != null){
				ancestor = ancestor.parent();
			}
			return ancestor;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			return this.element;
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			if(element == null)
				return false;
			this.element = element;
			return true;
		}

		@Override
		public FibonacciHeapNode leftSibling() {
			// TODO Auto-generated method stub
			return this.left;
		}

		@Override
		public FibonacciHeapNode rightSibling() {
			// TODO Auto-generated method stub
			return this.right;
		}

		@Override
		public FibonacciHeapNode child() {
			// TODO Auto-generated method stub
			return this.child;
		}

		@Override
		public FibonacciHeapNode parent() {
			// TODO Auto-generated method stub
			return this.parent;
		}

		@Override
		public boolean heaplifyUp() {
			// TODO Auto-generated method stub
			boolean modified = super.heaplifyUp();
			if(modified || this.parent() == null){
				FibonacciHeap.this.entry = FibonacciHeap.this.peekNode();
			}
			return modified;
		}

		@Override
		public boolean heaplifyDown() {
			// TODO Auto-generated method stub
			boolean modified = super.heaplifyDown();
			if(modified || this.parent() == null){
				FibonacciHeap.this.entry = FibonacciHeap.this.peekNode();
			}
			return modified;
		}

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			return this.degree;
		}

//		@Override
//		public int hashCode() {
//			// TODO Auto-generated method stub
//			return this.element == null ? 0 : this.element.hashCode();
//		}
		
	}
	
	private class FibonacciHeapIterator implements Iterator<E>{
		private Iterator<FibonacciHeapNode> nodeIt;
		private FibonacciHeapNode current = null;
		
		public FibonacciHeapIterator() {
			// TODO Auto-generated constructor stub
			if(FibonacciHeap.this.nodes == null){
				this.nodeIt = new EmptyIterator<FibonacciHeapNode>();
			} else{
				this.nodeIt = FibonacciHeap.this.nodes.iterator();
			}
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return nodeIt != null && nodeIt.hasNext();
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			this.current = nodeIt.next();
			return this.current.element();
		}

		@Override
		public void remove(){
			if(this.current == null)
				throw new IllegalStateException();
			nodeIt.remove();
			FibonacciHeap.this.removeNode(this.current);
			this.current = null;
		}
		
	}

}
