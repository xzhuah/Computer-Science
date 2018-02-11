# Data structure and Algorithm

## Binary Search Tree

## Hash

## Analysis of Algorithm (Maximum Contiguous Subarray Problem)

Given a array of number, how to find a subarray whose sum if the maximum in all possible contiguous subarray.

### Analysis from a computer science pespective

#### What do we expect?
As a human, when we read the whole array for one time, we should be able to give the maximum contiguous. Unlike sorting, you need to read the whole array and then sort the element one by one. Sorting requires more than one time of traverse. Therefore, we expect a O(n) algorithm.

#### What to pay attention to?
Now, we have only one chance to read the array. While reading, what can be the sign of a starting point of a maximum contiguous subarray? What can be the sign of ending point? Since we only have one chance to go through all the elements, we must find out those "sign"

#### Finding the sign

What kinds of subarray can be the maximum? Can a negative number be the first or last number of it? Clearly no since when you remove them, the sum of the remaining part becomes larger. 

Can the sum of the first two numbers less than 0? Again no if the array is longer than 2. 

#### Intuitive algorithm

Let's read the array from left to right, kind in mind that the sum of the first serval numbers of our answer can be less than 0. So, we keep counting the sum of all the number we have read, once we found this sum become less than 0, forget all the things we have read. Meanwhile, keep recording the maximum sum we met so far. Intuitivly, we will get out answer


#### Writing out the code

Computer scientist will write down their pseudocode to record their idea and allow other to review and further prove. Here I prefer to write down python code since it is direcly runnable.

	def max_sub(arr):
	    if(not isinstance(arr,list)):
	        raise ValueError()
	
	    max_sub = min(0,arr[0])
	    curr_sum = arr[0]
	    for i in range(1,len(arr)):
	        curr_sum += arr[i]
	        if (curr_sum > max_sub):
	            max_sub = curr_sum
	        elif (curr_sum < 0):
	            curr_sum = 0
	    return max_sub
	
#### proof the correctness and complexity

## Heap and Heapsort

### Heap-order property

The value of a node is at least the value of its parent--Min-heap

#### Insert

Always insert to the end, and then pop up by swapping
![](https://i.imgur.com/F58r13T.png)
O(log(n))

#### Extract-Min:
The root must have the minimum value, so we always extract the root and then copy the last element to the root, the pop down the root by swapping with its child

![](https://i.imgur.com/YifxIjl.png)

Heapsort is a sorting algorithm using the heap, the time complexity is obviously O(nlog(n))


### Binomial Heaps

### Binomial Trees
![](https://i.imgur.com/1VLa7wK.png)

* A binomial tree of order 0 is a single node
* A binomial tree of order k has a root node whose children are roots of binomial trees of orders k−1, k−2, ..., 2, 1, 0 (in this order).

### Binomial heap

a set of binomial tree satisfy that

* Each binomial tree in a heap obeys the minimum-heap property: the key of a node is greater than or equal to the key of its parent.
* There can only be either one or zero binomial trees for each order, including zero order.

#### Merge tree

to merge two tree with same order, compare their root and choose the one with smaller value to be the root of the merged tree. Link the other tree to that root to form a higher order tree. O(1)

#### Insert value

Insert a order 0 tree and then merge until there is no two tree with the same order

#### find minimum

Search through the root of all the tree O(log(n))

#### merge heap

merge all trees in two heap until there is not two same-order tree, O(log(n))

### Fibonacci Heaps



## Quick sort

## Sorting

## AVL Tree

## Binary Search Tree Algorithm

## Randomized Algorithm

## Divide and Conquer: Polynomial Multiplication

## Deterministic Linear Time Selection

## FFT

## Greedy Algorithm

## Huffman Encoding

## Graph

### BFS and DFS

### Topological Sort

### MST and Dijkstra

### Union Find

## Dynamic programming

### Chain Matrix Multiplication

### Odds and Ends

### Shortest Path

### Longest Common Subsequences and Substrings

## Maximum Flow

## Hashing

## Closest Pairs