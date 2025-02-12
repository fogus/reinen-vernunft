(ns reinen-vernunft.datalog-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.datalog :as d]))

(deftest test-datalog-q*-no-rules
  (let [fkb #{[-1002 :response/to -51]
              [-51 :emergency/type :emergency.type/flood]
              [-50 :emergency/type :emergency.type/fire]
              [-1002 :response/type :response.type/kill-electricity]
              [-1000 :response/to -50]
              [-1000 :response/type :response.type/activate-sprinklers]}]
    (is (= #{[:response.type/kill-electricity] [:response.type/activate-sprinklers]}
           (d/q* fkb
                 '([?response] [_ :response/type ?response])
                 '())))

    (is (= #{[:emergency.type/fire :response.type/activate-sprinklers] [:emergency.type/flood :response.type/kill-electricity]}
           (d/q* fkb
                 '([?problem ?response]
                   [?id :response/type   ?response]
                   [?id :response/to     ?pid]
                   [?pid :emergency/type ?problem])
                 '())))

    (is (= #{[:response.type/activate-sprinklers :response/to :emergency.type/fire]
             [:response.type/kill-electricity :response/to :emergency.type/flood]}
           (d/q* fkb
                 '([?response :response/to ?problem]
                   [?id :response/type   ?response]
                   [?id :response/to     ?pid]
                   [?pid :emergency/type ?problem])
                 '())))

    (is (= #{[:response.type/activate-sprinklers :response/to :emergency.type/fire]
             [:response.type/kill-electricity :response/to :emergency.type/flood]}
           (d/q '[:find [?response :response/to ?problem]
                  :where
                  [?id :response/type   ?response]
                  [?id :response/to     ?pid]
                  [?pid :emergency/type ?problem]]
                fkb)))))

(deftest test-datalog-ops
  (let [nkb #{[0 :a/num 0]
              [1 :a/num 1]
              [2 :a/num 2]
              [3 :a/num 3]
              [4 :a/num 4]
              [5 :a/num 5]}]
    (is (= #{[0]}
           (d/q* nkb '([?num] [0 :a/num ?num]) '())))
    (is (= #{[0] [1] [2]}
           (d/q* nkb '([?num] [_ :a/num ?num] (< ?num 3)) '())))
    (is (= #{[1] [2]}
           (d/q* nkb '([?num] [_ :a/num ?num] (< ?num 3) (> ?num 0)) '())))
    (is (= #{[0]}
           (d/q* nkb '([?num] [_ :a/num ?num] (= ?num 0)) '())))
    (is (= #{[0] [1] [2] [3] [4]}
           (d/q* nkb '([?num] [_ :a/num ?num] (not= ?num 5)) '())))
    (is (= #{[0] [1] [2] [3] [4]}
           (d/q* nkb '([?num] [_ :a/num ?num] (<= ?num 4)) '())))
    (is (= #{[5] [1] [2] [3] [4]}
           (d/q* nkb '([?num] [_ :a/num ?num] (>= ?num 1)) '())))))

(deftest test-datalog-q*-with-rules
  (let [ekb {:facts #{[:homer :person/name "Homer"]
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
                      [:mona :relationship/mother :marge]}}
        anc-rules '[([?p :relationship/parent ?c] [?p :relationship/father ?c])
                    ([?p :relationship/parent ?c] [?p :relationship/mother ?c])
                    ([?gp :relationship/grand-parent ?c] [?gp :relationship/parent ?p] [?p :relationship/parent ?c])
                    ([?p :relationship/ancestor ?c] [?p :relationship/parent ?c])
                    ([?ancp :relationship/ancestor ?c] [?anc :relationship/ancestor ?c] [?ancp :relationship/parent ?anc])]
        sib-rules '[([?p :relationship/parent ?c] [?p :relationship/father ?c])
                    ([?p :relationship/parent ?c] [?p :relationship/mother ?c])
                    ([?c' :relationship/sibling ?c] [?p :relationship/parent ?c] (not= ?c ?c') [?p :relationship/parent ?c'])]]
    (is (= #{["Lisa"] ["Maggie"]}
           (d/q '[:find ?n
                  :where
                  [?s :relationship/sibling :bart]
                  [?s :person/name ?n]]
                ekb
                sib-rules)))
    (is (= #{["Abe"] ["Mona"]}
           (d/q '[:find ?n
                  :where
                  [?s :relationship/grand-parent :lisa]
                  [?s :person/name ?n]]
                ekb
                anc-rules)))))
