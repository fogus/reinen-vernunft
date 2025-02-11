;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.reinen-vernunft.core
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

(def ID_KEY :db.id)
(def ^:private db-ids (atom 0))

(defn map->tuples
  [entity]
  (let [id (get entity ID_KEY)
        id  (if id id (swap! db-ids inc))]
    (for [[k v] entity
          :when (not= k ID_KEY)]
      [id k v])))

(defn db->tuples
  [db]
  (set (mapcat map->tuples db)))

(defn tuples->db
  [tuples]
  )

(comment
  (-> #{{:person/name "Fred"
         :person/age 33
         :address/state "NY"}
        {:person/name "Ethel"
         :person/age 31
         :address/state "NJ"}
        {:person/name "Jimbo"
         :person/age 55
         :address/state "VA"
         :db.id -1000}}
      db->tuples)
  )
