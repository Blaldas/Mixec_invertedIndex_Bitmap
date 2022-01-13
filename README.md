# Mixec_invertedIndex_Bitmap

This repository contains code to a data cube structure the mixes both inverted index and bitmap arrays.

Both inverted indexes and bitmap arrays are classic data structes in data cubes
So, wich one of this is better?

Well, two metrics are used to assess any data structure:
  - time complexity
  - memory complexity
  
  
## Time Complexity

Since the most relevant operation in a data cube is intersection, let's see this data structures' intersection time complexity.

### Bitmap

Two bitmaps in the same data cube have allways the same size.
The same position in both bitmaps is related to the same tuple.
The time complexity of a bitmap intersection is O(*n*), being *n* the number of tuples in the data cube.

### Inverted Index

Two inverted indexes may have different sizes.
The same TID (Tuple ID) can be found in different positions of two data cubes.
The time complexity of a inverted index intersection is arround O(*2n), being *n* the number of tuples in the data cube.

### Result

As it can be seen from time complexities, processing bitmap arrays is arround twice as fast as processing inverted indexes.

## Memory Complexity

Data cubes can be processed without being stored in memory. The most memory demanding operation in a data cube is, most of the times, its storage.
Let us assume a data cube with *D* dimensions, having each dimenion *C* cardinality. Besides, the data cube has *n* tuples.

### Bitmap

A bitmap array is composed by one bit for each tuple, therefore, has *n* bits.
A dimention is composed for a bitmap for each attribute value, resulting in *C* bitmaps.
A data cube has *D* dimenions.
The total memory consumed is *D*×*C*×*n* bits.

### Inverted Index

A TID is stored using integers. Let us assume an integer being 4 bytes/32 bits, as it is common nowerdays
Different inverted index arrays have different sizes, however a dimenions only stores onde each TID.
A data cube has *D* dimenions.
The tomal memory consumed is *D*×*n*×32 bits.

### Result

An interesting conclusion is obtained when trying to create a relation between both memory complexity formulas, let us see:

*D*×*n*×32 = *D*×*C*×*n*

32 = *C*

What does it mean?
Well, it means that, if the cardinality of a dimension is bellow the number 32, then bitmap arrays have lower memory consumption.
When the cardinality of a dimensions is equal to 32, both structures have the same memory consumption.
When the cardinality of a dimension is above 32, then inverted indexes have lower memory consumption.

...tbc





