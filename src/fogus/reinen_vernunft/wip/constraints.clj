;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

;; WIP

(ns fogus.reinen-vernunft.wip.constraints
  "A simple constraints solver."
  (:require [fogus.reinen-vernunft.core :as core]
            [fogus.reinen-vernunft.util :as util]
            [clojure.core.unify         :as unify]
            [fogus.evalive              :as live]))

(defrecord variable   [name domain])
(defrecord constraint [variables formula])
(defrecord cpair      [name value])

(defn get-all-pairs [c]
  (let [vars     (:variables c)
        varnames (map :name vars)
        tuples   (util/cart (map :domain vars))]
    (map #(map ->cpair varnames %) tuples)))

(defn test-pair [f p]
  (cond (= p []) (live/evil {} f)
        :else (let [current-pair    (first p)
                    remaining-pairs (rest p)]
                (test-pair (unify/subst f {(:name current-pair) (:value current-pair)})
                           remaining-pairs))))

(defn find-sat [c]
  (letfn [(go [f ps]
              (cond (nil? ps) []
                    (test-pair f (first ps)) (first ps)
                    :else (go f (rest ps))))]
    (let [formula (:formula c)
          pairs   (get-all-pairs c)]
      (go formula pairs))))

