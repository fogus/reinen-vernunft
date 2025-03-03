# v0.0.5
- Added the ability to define entity enumerations by mapping a key to a set. These are
  expanded into [id k v] for each element in the set.
- Changed the name from reinen-vernunft to rv... please stop contacting me about the old name. ;)
  
# v0.0.4
- Added extra arity to both map->relation and table->kb to take id function, which can 
  be used to override the default :kb/id gen or existing key.
- Added a fogus.reinen-vernunft.fuzzy.soundex/encode function that implements the
  American soundex algorithm. https://en.wikipedia.org/wiki/Soundex

# v0.0.3
- Added fogus.reinen-vernunft.datalog ns based on cgrand's 39loc datalog
- Added map->relation and table->kb conversion functions in fogus.reinen-vernunft.core

# v0.0.2
- me.fogus release


