(ns reinen-vernunft.datalog-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.datalog :as d]))

(deftest test-datalog-q*-no-rules
  (let [fdb #{[-1002 :response/to -51]
              [-51 :emergency/type :emergency.type/flood]
              [-50 :emergency/type :emergency.type/fire]
              [-1002 :response/type :response.type/kill-electricity]
              [-1000 :response/to -50]
              [-1000 :response/type :response.type/activate-sprinklers]}]
    (is (= #{[:response.type/kill-electricity] [:response.type/activate-sprinklers]}
           (d/q* fdb
                 '([?response] [_ :response/type ?response])
                 '())))

    (is (= #{[:emergency.type/fire :response.type/activate-sprinklers] [:emergency.type/flood :response.type/kill-electricity]}
           (d/q* fdb
                 '([?problem ?response]
                   [?id :response/type   ?response]
                   [?id :response/to     ?pid]
                   [?pid :emergency/type ?problem])
                 '())))))

(deftest test-datalog-ops
  (let [ndb #{[0 :a/num 0]
              [1 :a/num 1]
              [2 :a/num 2]
              [3 :a/num 3]
              [4 :a/num 4]
              [5 :a/num 5]}]
    (is (= #{[0] [1] [2]}
           (d/q* ndb '([?num] [_ :a/num ?num] (< ?num 3)) '())))
    (is (= #{[1] [2]}
           (d/q* ndb '([?num] [_ :a/num ?num] (< ?num 3) (> ?num 0)) '())))
    (is (= #{[0]}
           (d/q* ndb '([?num] [_ :a/num ?num] (= ?num 0)) '())))
    (is (= #{[0] [1] [2] [3] [4]}
           (d/q* ndb '([?num] [_ :a/num ?num] (not= ?num 5)) '())))
    (is (= #{[0] [1] [2] [3] [4]}
           (d/q* ndb '([?num] [_ :a/num ?num] (<= ?num 4)) '())))
    (is (= #{[5] [1] [2] [3] [4]}
           (d/q* ndb '([?num] [_ :a/num ?num] (>= ?num 1)) '())))))

(deftest test-datalog-q*-with-rules
  (let [edb #{[:homer :person/name "Homer"]
              [:bart :person/name "Bart"]
              [:lisa :person/name "Lisa"]
              [:marge :person/name "Marge"]
              [:maggie :person/name "Maggie"]
              [:abe :person/name "Abe"]
              [:mona :person/name "Mona"]
              [:homer :relationship/father :bart]
              [:marge :relationship/mother :bart]
              [:homer :relationship/father :lisa]
              [:marge :relationship/mother :lisa]
              [:homer :relationship/father :maggie]
              [:marge :relationship/mother :maggie]
              [:abe :relationship/father :homer]
              [:mona :relationship/mother :marge]}
        anc-rules '[([?p :relationship/parent ?c] [?p :relationship/father ?c])
                    ([?p :relationship/parent ?c] [?p :relationship/mother ?c])
                    ([?gp :relationship/grand-parent ?c] [?gp :relationship/parent ?p] [?p :relationship/parent ?c])
                    ([?p :relationship/ancestor ?c] [?p :relationship/parent ?c])
                    ([?ancp :relationship/ancestor ?c] [?anc :relationship/ancestor ?c] [?ancp :relationship/parent ?anc])]
        sib-rules '[([?p :relationship/parent ?c] [?p :relationship/father ?c])
                    ([?p :relationship/parent ?c] [?p :relationship/mother ?c])
                    ([?c' :relationship/sibling ?c] [?p :relationship/parent ?c] (not= ?c ?c') [?p :relationship/parent ?c'])]]
    (is (= #{["Lisa"] ["Maggie"]}
           (d/q* edb
                 '([n] [s :relationship/sibling :bart] [s :person/name n])
                 sib-rules)))))
