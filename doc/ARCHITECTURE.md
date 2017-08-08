# reinen-vernunft architecture concerns

## EAV tuple sets

If at all possible the reasoning functions provided herein will operate on EAV tuple sets.  EAV stands for Entity, Attribute, Value. An EAV tuple is simply a Clojure vector of three elements, such as:

    [100 :person/name "Gilbert"]

In the EAV tuple above the elements refer to the following:

* *Entity* - a unique id that refers to an entity in the tuple set
* *Attribute* - a keyword that names a common attribute 
* *Value* - the value of the attribute for the given entity



## Logic variables

TODO

