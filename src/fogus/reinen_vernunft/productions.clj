;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.reinen-vernunft.productions
  "The simplest possible production rules system that uses a set
   of EAV tuples as its knowledge base."
  (:require [clojure.core.unify :as u]
            [clojure.set :as s])
  (:refer-clojure :exclude [cycle]))

;; Productions are represented as maps having two privileged keys:
;; {:antecedent ... 
;;  :consequent ...}

;; The :antecedent key in the production map contains a sequence of EAV 3-tuples 
;; with logical variables at key locations for the purpose of pattern matching.
;; These patterns refer to facts in the knowledge base.
;;
;; [[?id :person/name     "Fogus"]
;;  [?id :language/speaks ?lang]]
;;
;; The antecedent describes the patterns that must be present in the EAV
;; set in order for the production to activate. The antecedent is also known as
;; the left-hand-side (LHS) of the production.

;; When a production activates, the structure in the :consequent key in the production
;; map is applied to the knowledge base to potentially create new facts.
;; The consequent also contains a sequence of EAV 3-tuples with logical 
;; variables at key locations.  However, the tuples describe new facts 
;; with values bound to embedded logic variables as defined within the 
;; context of a production activation.

;; A production set is just a data structure defines as such:
;;
;; 1. A production set is simply a vector of production definitions
;; 2. A production definition is a map containing :antecedent and :consequent keys
;; 3. An antecedent is a vector of EAV 3-tuples representing patterns in data
;; 4. An EAV 3-tuple is a vector of three elements: id, attribute, value
;; 5. A consequent is a vector of EAV 3-tuples representing new attribute assertions

;; A fact base is a set of EAV 3-tuples.

;; A knowledge base is a map with two keys in it
;; {:productions <a production set> 
;;  :facts <a fact base>}

;; The production productions sytem implemented herein is a a four stage system:
;;
;; 1. Antecedent unifications
;; 2. Production selection
;; 3. Consequent substitutions and assertion
;; 4. System quiessence

;; Stage 1: Unifications

(defn unifications 
  "Walks through all of the clauses in an implied antecedent and matches 
   each against every fact provided.  Returns a seq of contexts representing
   all of the bindings established by the antecedent unifications across all
   facts provided."
  [[clause & more :as clauses] facts context]
  (if clause
    (let [bindings (keep #(u/unify clause % context) facts)]
      (mapcat #(unifications more facts %) bindings))
    [context]))

;; Stage 2: Production selection

(defn select-production
  "Builds a sequence of bindings paired with each production and then uses a selection 
   function to execute one of the productions that matched."
  [selection-strategy {:keys [productions facts]}]
  (let [possibilities 
        (for [production    productions
              bindings (unifications (:antecedent production) facts {})]
          [production bindings])]
    (selection-strategy possibilities)))


;; Stage 3: Consequent substitutions and assertion

(defn apply-production [production facts context]
  (let [new-facts (set (for [rhs (:consequent production)]
                         (u/subst rhs context)))]
    (s/union new-facts facts)))

;; Stage 3a: Single substitution and assertion

(defn step 
  "Takes a set of productions and facts and returns a new fact base based on the application of single production."
  ([kb] (step rand-nth kb))
  ([choice-fn kb]
   (when-let [[production binds] (select-production choice-fn kb)]
     (apply-production production (:facts kb) binds))))

;; Stage 3b: Repeated substitution and assertion

(defn states 
  "Will apply the result of one production firing to the fact base and feed 
   the result forward into the next firing."
  [kb] 
  (iterate #(step (assoc kb :facts %))
           (set (:facts kb))))

(defn cycle 
  "Feeds the results of states into a function qf that is responsible for 
   detecting when production firings have stopped and returns an augmented
   fact set."
  [qf kb]
  (qf (states kb)))

;; Stage 4: System quiessence

(defn naive-qf 
  "Takes the last environment in a long sequence of states in the hope that 
   the sequence was long enough that all of the productions fired in creating it."
  [states]
  (last (take 256 states)))

