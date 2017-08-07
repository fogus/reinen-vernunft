(ns fogus.reinen-vernunft.rules
  "The simplest possible production rules system that uses a set
   of EAV tuples as its knowledge base."
  (:require [clojure.core.unify :as u]
            [clojure.set :as s]))

;; Rules are represented as maps having two privileged keys:
;; {:antecedent ... 
;;  :consequent ...}

;; The :antecedent key in the rule map contains a sequence of EAV 3-tuples 
;; with logical variables at key locations for the purpose of pattern matching.
;; These patterns refer to facts in the knowledge base.
;;
;; [[?id :person/name     "Fogus"]
;;  [?id :language/speaks ?lang]]
;;
;; The antecedent describes the patterns that must be present in the EAV
;; set in order for the rule to activate. The antecedent is also known as
;; the left-hand-side (LHS) of the rule.

;; When a rule activates, the structure in the :consequent key in the rule
;; map is applied to the knowledge base to potentially create new facts.
;; The consequent also contains a sequence of EAV 3-tuples with logical 
;; variables at key locations.  However, the tuples describe new facts 
;; with values bound to embedded logic variables as defined within the 
;; context of a rule activation.

;; A rule set is just a data structure defines as such:
;;
;; 1. A rule set is simply a vector of rule definitions
;; 2. A rule definition is a map containing :antecedent and :consequent keys
;; 3. An antecedent is a vector of EAV 3-tuples representing patterns in data
;; 4. An EAV 3-tuple is a vector of three elements: id, attribute, value
;; 5. A consequent is a vector of EAV 3-tuples representing new attribute assertions

;; A fact base is a set of EAV 3-tuples.

;; A knowledge base is a map with two keys in it
;; {:rules <a rule set> 
;;  :facts <a fact base>}

;; The production rules sytem implemented herein is a a four stage system:
;;
;; 1. Antecedent unifications
;; 2. Rule selection
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

;; Stage 2: Rule selection

(defn select-rule [selection-strategy {:keys [rules facts]}]
  (let [possibilities 
        (for [rule    rules
              bindings (unifications (:antecedent rule) facts {})]
          [rule bindings])]
    (selection-strategy possibilities)))

