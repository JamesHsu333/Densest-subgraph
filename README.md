# Implementation of Exploiting Efficient Densest Subgraph Discovering Methods for Big Data

## Introduction
Dense subgraph estimates the comparison between the number of edges of the
subgraph and the number of vertices of the subgraph

Densest subgraph problem is that of finding a subgraph of maximum density

Densest subgraph has lots of real application such as
* Spam detection in the Web graph
* Correlation mining
* Bioinformatics
* Mining Twitter data

## Common Algorithms to solve DSP
* Goldberg’s algorithm
* Greedy Algorithm

## Bottleneck of current algorithm
* The previous algorithms ignore the connectivity of the returned densest subgraph
* Returned subgraph may consist of several isolated connected components that maximize its density
* There are lacking of efficient algorithms for massive graphs, especially considering datasets become increasingly larger in this era of Big Data
* Another problem is that all the exact algorithms, are in-memory algorithms
which are not suitable for big data
* The applicability of current algorithms to different kinds of graphs has not
been discussed
* For example, some of the natural graphs have community structures and some others do not have community structures

## Solver
For more detail of this paper, please refer to ```doc/```
