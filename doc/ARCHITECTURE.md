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

An EAV tuple set can be thought of as representing a relational database:

    #{[100 :person/name   "Gilbert"]
	  [100 :person/age    42]
	  [200 :person/name   "Zippy"]
	  [300 :person/name   "Queequeg"]
	  [100 :person/friend 200]}

In the EAV tuple set above, the `:person/friend` attribute establishes a link between two entities via their ids.

### Why EAV tuple sets?

Aside from the simplicity of the data, the relational nature of the EAV tuple set is robust enough to model fairly complex domains.  In addition, there are existing libraries that can directly query EAV tuple sets:

* [DataScript](https://github.com/tonsky/datascript)
* [Datomic](http://www.datomic.com)

To name only a couple.

## Logic variables

TODO

