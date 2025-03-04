;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.rv.constraints
  "Constraints solving functions that operate on a Constraint Description
  which is a map describing a constraint description containing the mappings:
  - :variables -> seq of LVars
  - :formula -> list describing a predicate expression composed of a mix of
    the LVars in :variables and Clojure functions."
  (:require [fogus.rv.core :as core]
            [fogus.rv.util :as util]
            clojure.core.unify))

(defn- cartesian-groups [vars]
  (let [tuples (util/cart (map :range vars))]
    (map #(map vector vars %) tuples)))

(def ^:private subst (clojure.core.unify/make-occurs-subst-fn core/lv?))

(defn- test-in-context [formula group]
  (let [formula' (subst formula (into {} group))]
    (eval formula')))

(defn- find1 [formula [group & more :as groupings]]
  (cond (nil? groupings) []
        (test-in-context formula group) group
        :else (recur formula more)))

(defn satisfy1
  "Accepts a map describing a constraint description containing the mappings:
  - :variables -> seq of LVars
  - :formula -> list describing a predicate expression composed of a mix of
    the LVars in :variables and Clojure functions

  This function will use the constraint description to calculate the first
  set of values for the LVars that satisfy the formula. The result is a map
  with mappings from LVar -> value. If there is no way to satisfy the formula
  then an empty map is the result.

  The first found result of this function is not guaranteed to be stable."
  [{:keys [variables formula :as c]}]
  (into {} (find1 formula (cartesian-groups variables))))

(defn satisfy*
  "Accepts a map describing a constraint description containing the mappings:
  - :variables -> seq of LVars
  - :formula -> list describing a predicate expression composed of a mix of
    the LVars in :variables and Clojure functions

  This function will use the constraint description to calculate the all of 
  the values for the LVars that satisfy the formula. The result is a seq of
  maps with mappings from LVar -> value. If there is no way to satisfy the
  formula then an empty seq is the result.

  The ordering of the results of this function is not guaranteed to be stable."
  [{:keys [variables formula :as c]}]
  (let [groupings (cartesian-groups variables)
        groupings* (take-while seq (iterate rest groupings))]
    (->> groupings*
         (map #(find1 formula %))
         (keep seq)
         set
         (map #(into {} %)))))
