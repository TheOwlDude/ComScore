# ComScore

Java Solution to ComScore programming challenge

Two classes have Main.

Import         
======

USAGE: storeFolder importFile1 importFile2 . . .

Allows adding/updating data to the data store. The folder supplied on the command line must exist, the class will not create it.

I have named each record kept in the store a 'Viewing' (someone viewed something). Viewings occupy a fixed number of bytes 
in the store. I was led to this representation by seeing that the specification limited the size of all the variable 
string fields. In order to simplify fixing string sizes I made the assumption that all strings are restricted to ASCII 
values <= 127. The variable length string fields are padded with spaces to 64 bytes. I use trim() in the program to restore the 
strings to the correct length. This is not most efficient but it is simple.

The store implementation is inefficient both in reading and writing. When loading new data the program reads the entire existing
store to find duplicate keys. Having a key index would improve this efficiency but seemed quite difficult for the nature of
this project. The store is also entirely rewritten when loading new data. Using a Random Access File it would be possible to
only overwrite records that have changed. Doing it the way I did though allowed me to use the same reader for both Import
and Query operations which reduced the amount of work I had to do. Since this is just an exercise I thought it would be OK.



Query
=====

USAGE: storeFolder -s selectList [-o orderList] [-f filterCondition]

Prints to the screen the results of the supplied query. The command line differs slightly from the specification. It requires the 
path to the store folder be supplied as the first argument.

I only implemented through 2.1 omitting aggregation and advanced filtering. Even if I had completed the aggregation I still would
have omitted the -g flag as the fields from the select list not including group functions should be the same as the list supplied
with -g.


Thanks for this challenge, I enjoyed working on it.


