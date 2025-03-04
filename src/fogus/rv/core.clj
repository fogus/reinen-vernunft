;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.rv.core
  "Most functions in rv work off of one or more of the following root
  concepts:

  - Entity: a hashmap with a :kb/id key mapped to a unique value and namespaced keys
  - Table: a set of hashmaps or Entities
  - Fact: a vector triple in the form [entity-id attribute value]
  - Relation: a set of Facts pertaining to a particular Entity
  - LVar: a logic variable that can bind to any value in its :range
  - Ground: a concrete value
  - Query: a set of Facts containing a mix of LVars and Grounds  
  - Rules: a set of Facts describing synthetic relations
  - Production: a pair of: antecedent query and consequent Facts
  - KB: a set of Relations about many Entities and possibly containing Productions  
  - Constraint Description: a set of LVars and a Formula describing the domain of their values
  - Formula: a list describing a predicate expression of mixed LVars and clojure functions"
  (:import java.io.Writer))

;; Logic variables

(defrecord LVar [domain range]
  Object
  (toString [this]
    (if range
      (str "?" domain "::" range)
      (str "?" domain))))

(def lv? #(instance? LVar %))

(defmethod print-method LVar [lvar ^Writer writer]
  (.write writer (str lvar)))

(def ID_KEY :kb/id)

(def ^:private use-or-gen-id
  (let [next-id (atom 0)]
    (fn [entity]
      (if-let [id (get entity ID_KEY)]
        id
        (swap! next-id inc)))))

(defn- set->tuples
  [id k s]
  (for [v s] [id k v]))

(defn map->relation
  "Converts a map to a set of tuples for that map, applying a unique
  :kb/id if the map doesn't already have a value mapped for that key.

  Relation values that are sets are expanded into individual tuples
  per item in the set with the same :kb/id as the entity and the
  attribute that the whole set was mapped to.  

  An idfn is a function of map -> id and if provided is used to
  override the default entity id generation and any existing :kb/id
  values."
  ([entity]
   (map->relation use-or-gen-id entity))
  ([idfn entity]
   (let [id (idfn entity)]
     (reduce (fn [acc [k v]]
               (if (= k ID_KEY)
                 acc
                 (if (set? v)
                   (concat acc (set->tuples id k v))
                   (conj acc [id k v]))))
             []
             (seq entity)))))

(defn table->kb
  "Converts a Table into a KB, applying unique :kb/id to maps without a
  mapped identity value.

  See map->relation for more information about how the entities in the
  table are converted to relations.

  An idfn is a function of map -> id and if provided is used to
  override the default entity id generation and any existing :kb/id
  values."
  ([table] (table->kb use-or-gen-id table))
  ([idfn table]
   {:facts (set (mapcat #(map->relation idfn %) table))}))

