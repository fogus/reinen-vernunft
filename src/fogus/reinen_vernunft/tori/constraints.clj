;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.reinen-vernunft.tori.constraints
  "A simple constraints solver."
  (:require [fogus.reinen-vernunft.core :as core]
            [fogus.reinen-vernunft.util :as util]
            [clojure.core.unify         :as unify]))

(defrecord variable   [name domain])
(defrecord constraint [variables formula])
(defrecord cpair      [name value])

(def cnstr (->constraint [(->variable :x [0 1])
                          (->variable :y [0 1])
                          (->variable :z [0 1])]
                         '(= (+ x y) z)))

(defn get-all-pairs [c]
  (let [vars     (:variables c)
        varnames (map :name vars)
        tuples   (util/cart (map :domain vars))]
    (map #(map ->cpair varnames %) tuples)))

(defn test-pair [f p]
  (cond (= p []) nil))

(comment

  (get-all-pairs cnstr)

)

