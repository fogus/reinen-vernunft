;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.reinen-vernunft.core
  "Most functions in reinen-vernunft work off of one or more of the following root
  concepts:

  - Entity: a hashmap with a :kb/id key mapped to a unique value and namespaced keys
  - Table: a set of hashmaps or Entities
  - Fact: a vector triple in the form [entity-id attribute value]
  - Relation: a set of Facts pertaining to a particular Entity
  - LVar: a symbol naming a logic variable that can bind to any value
  - Ground: a concrete value
  - Query: a set of Facts containing a mix of LVars and Grounds  
  - Rules: a set of Facts describing synthetic relations
  - Production: a pair of: antecedent query and consequent Facts
  - KB: a set of Relations about many Entities and possibly containing Productions  
  "
  (:import java.io.Writer))

;; Logic variables

(deftype LVar [id]
  Object
  (equals [this other]
    (if (instance? LVar other) 
      (= (.-id this)
         (.-id other))
      false))
  (toString [this]
    (str "_." id)))

(def lv? #(instance? LVar %))

(defmethod print-method LVar [lvar ^Writer writer]
  (.write writer (str lvar)))

(def ID_KEY :kb/id)
(def ^:private db-ids (atom 0))

(defn- use-or-gen-id [entity]
  (if-let [id (get entity ID_KEY)]
    id
    (swap! db-ids inc)))

(defn map->relation
  "Converts a map to a set of tuples for that map, applying a unique
  :kb/id if the map doesn't already have a value mapped for that key."
  ([entity]
   (map->relation use-or-gen-id entity))
  ([idfn entity]
   (let [id (idfn entity)]
     (for [[k v] entity
           :when (not= k ID_KEY)]
       [id k v]))))

(defn table->kb
  "Converts a Table into a KB, applying unique :kb/id to maps without a
  mapped identity value."
  ([table] (table->kb use-or-gen-id table))
  ([idfn table]
   {:facts (set (mapcat #(map->relation idfn %) table))}))

