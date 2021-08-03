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

(comment

  (update-keys name {:a 1 :b 2})
  (update-vals inc  {:a 1 :b 2})

  (update-keys2 name {:a 1 :b 2})
  (update-vals2 inc  {:a 1 :b 2})

)
