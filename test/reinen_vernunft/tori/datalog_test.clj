(ns reinen-vernunft.tori.datalog-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.tori.datalog :as d]))

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

(deftest test-datalog-q*-with-rules
  (let [edb #{[:father :bart :homer]
              [:mother :bart :marge]
              [:father :lisa :homer]
              [:mother :lisa :marge]
              [:father :maggie :homer]
              [:mother :maggie :marge]
              [:father :homer :abe]
              [:mother :homer :mona]}
        rules '[([:parent c p] [:father c p])
                ([:parent c p] [:mother c p])
                ([:grand-parent c gp] [:parent p gp] [:parent c p])
                ([:ancestor c p] [:parent c p])
                ([:ancestor c ancp] [:ancestor c anc] [:parent anc ancp])]]
    (is (= #{[:lisa] [:maggie]}
           (d/q* edb
                 '([s] [:sibling :bart s])
                 '[([:parent c p] [:father c p])
                   ([:parent c p] [:mother c p])
                   ([:sibling c c'] [:parent c p] (not= c c') [:parent c' p])])))))
