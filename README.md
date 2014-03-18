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

Package matrix provide a new kind of map that associate two different key types to a single value, one key is called 
row key and the other is column key. For example, if we need to maintain the access authorities to many documents of 
many different users, we could use a matrix to contain the authorities, let the user id be the row key and the doc id 
be the column key, the authority of the given user to the given document.  We have three kind implementations of 
matrix now. 

An HashMap maintain the data with a nested HashMap, so random access will be the fastest. The key of outter hashMap is 
the row key of the matrix, and the time complexity of random get all value of a row is O(1), too. But it is lack the 
ability of get all value of a specific column, so developers are better to let the row key was the more frequece accessed 
one. HashMatrix is suitable for a sparse matrix.  

LinkedMatrix is an implemention of cross list. each element in the matrix maintain an pointer to the right element in the 
same row and the lower element in the same column. It can provide the view of rows and columns in linear time. And the 
time of random access will be O(numofrow * numofcolumn). Compared with ArrayMatrix, it need less memory space when the 
matrix is sparse.  

ArrayMatrix implement matrix with a two-demensional array. It is efficient to a dense matrix. The performance of access 
operations are almost the same with LinkedMatrix, but its memory space are more compact. Similar with ArrayList, the 
capacity of array always greater than the matrix size, which the the number of values, and it grows automatically when 
new tuples are added into the matrix.
<<<<<<< HEAD
=======

Open source project links: https://github.com/a1e2w3/java_util_extension.git or https://a1e2w3@bitbucket.org/a1e2w3/java_util_extension.git
>>>>>>> java_util_extension@github/master
