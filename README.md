java_util_extension
===================

An extension of package java.util in jdk, contains three object container: pair, matrix and heap.  

A pair contains two element and provide the getter and setter methods. It can be used to package two object temporarily 
instead of creating a new java class. It will be useful when a method needs two return values, or we want to create a 
view of two attribute of an object like the name and age of a person.  

Package hust.idc.util.heap contains several implementations of heap for java include BinaryHeap, BinomialHeap and 
FibonacciHeap.  An extension of class "Collection" in jdk which can be used as priority queue or sorted collection. The 
remove operation in iterator of BinaryHeap and BinomialHeap have not been implemented yet, since the operation will 
change the heap structure and it is hard to keep the iterate order.  

A HashMap collaborate with a heap can implement a map sorted by values. The HashMap provide the random access and the 
Heap maintain the order of keys. The following code gives an example.

	public class MapSortedByValues<K, V> extends AbstractMap<K, V> implements Queue<K> {
		Map<K, V> map;
		Heap<K> heap;
		final Comparator<K> keyComp = new Comparator<K> {
			public int compare(K o1, K o2){
				return valueComp.compare(map.get(o1), map.get(o2));
			}
		}
		final Comparator<V> valueComp;
	
		public MapSortedByValues(Comparator<V> comparator){
			this.valueComp = comparator;
			heap = new BinomialHeap<K>(this.keyComp);
			map = new HashMap<K, V>();
		}
	
	}


Package matrix provide a new kind of map that associate two different key types to a single value, one key is called 
row key and the other is column key. For example, if we need to maintain the access authorities to many documents of 
many different users, we could use a matrix to contain the authorities, let the user id be the row key and the doc id 
be the column key, whose value is the authority of the given user to the given document.  We have three kind implementations of 
matrix now. 

An HashMatrix maintain the data with a two-dimensional array, the position of an entry was decided by hash code of row key 
and column key, the row key decide the row in array and the column key decide the column. So random access will be the 
fastest. In version 1.0, we use a nest hash map to implement the hash matrix, the array implementation was in the plan. 
The key of outer hashMap is the row key of the matrix, and the time complexity of random get all value of a row 
is O(1), too. But it is lack the ability of get all value of a specific column, so developers are better to let the row 
key was the more frequent accessed one. HashMatrix is suitable for a sparse matrix.  

LinkedMatrix is an implementation of cross list. each element in the matrix maintain an pointer to the right element in the 
same row and the lower element in the same column. It can provide the view of rows and columns in linear time. And the 
time of random access will be O(numOfRow + numOfColumn). Compared with ArrayMatrix, it need less memory space when the 
matrix is sparse, and it doesn't need continuous memory for each row or column.  

ArrayMatrix implement matrix with a two-dimensional array. It is efficient to a dense matrix. The performance of access 
operations are almost the same with LinkedMatrix, but its memory space are more compact. Similar with ArrayList, the 
capacity of array always greater than the matrix size, which the the number of values, and it grows automatically when 
new tuples are added into the matrix.

Open source project links: https://github.com/a1e2w3/java_util_extension.git 
or https://a1e2w3@bitbucket.org/a1e2w3/java_util_extension.git
