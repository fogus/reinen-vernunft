# reinen-vernunft architecture concerns

## EAV tuple sets

If at all possible the reasoning functions provided herein will operate on EAV tuple sets.  EAV stands for Entity, Attribute, Value. An EAV tuple is simply a Clojure vector of three elements, such as:

    [100 :person/name "Gilbert"]

In the EAV tuple above the elements refer to the following:

* *Entity* - a unique id that refers to an entity in the tuple set
* *Attribute* - a keyword that names a common attribute 
* *Value* - the value of the attribute for the given entity

The id contained in the first slot of the tuple should be a unique value pertaining to a set of attributes associated with a single entity.  Any tuples in the EAV set with the same id will logically refer to the same entity:

    [100 :person/name "Gilbert"]
	[100 :person/age  42]

The id 100 thus pertains to a single person named Gilbert who's aged 42.  The id can be any type that allows mutual comparability.  

The entire collection of EAV tuples refering to one or more entities should be contained in a Clojure set.

    #{[100 :person/name "Gilbert"], [100 :person/age  42]}



### Why EAV tuple sets?



## Logic variables

TODO

