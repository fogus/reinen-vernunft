(ns reinen-vernunft.productions-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.productions :as p]
            [fogus.reinen-vernunft.datalog :as d]))

(def productions
  '[{:antecedent   [[?id   :emergency/type :emergency.type/fire]]
     :consequent [[-1000 :response/type  :response.type/activate-sprinklers]
                  [-1000 :response/to    ?id]]}
    {:antecedent   [[?id   :emergency/type :emergency.type/flood]]
     :consequent [[-1002 :response/type  :response.type/kill-electricity]
                  [-1002 :response/to    ?id]]}])

(def all-facts #{[-50 :emergency/type :emergency.type/fire]
                 [-51 :emergency/type :emergency.type/flood]})

(def KB {:productions productions
         :facts all-facts})

(deftest test-unifications
  ""
  (testing "that the context seq is built with a single antecedent pattern."
    (is (= '[{?id -50}]
           (p/unifications '[[?id :emergency/type :emergency.type/fire]]
                              (:facts KB)
                              {})))

    (is (= '[{?id -50} {?id -1000000}]
           (p/unifications '[[?id :emergency/type :emergency.type/fire]]
                              (conj all-facts [-1000000 :emergency/type :emergency.type/fire])
                              {}))))
  
  (testing "that the context is built with multiple antecedent patterns"
    (is (= '[{?id -50, ?rid -5000000}]
           (p/unifications '[[?id :emergency/type :emergency.type/fire]
                                [?rid :response/to ?id]]
                              (conj all-facts [-5000000 :response/to -50])
                              {})))))

(deftest test-select-production
  (testing "that productions are selected as expected"
    (let [first-matching-production (comp first identity)]
      (is (= (first (:productions KB))
             (first (p/select-production first-matching-production KB)))))))

(deftest test-apply-production
  (testing "that a production applied to a KB causes expected assertions"
    (let [first-matching-production (comp first identity)
          [production binds]        (p/select-production first-matching-production KB)]
      (is (= #{[-51 :emergency/type :emergency.type/flood]
               [-50 :emergency/type :emergency.type/fire]
               [-1000 :response/type :response.type/activate-sprinklers]
               [-1000 :response/to -50]}
             (p/apply-production production (:facts KB) binds))))))

(deftest test-step
  (testing "that a single step occurs as expected"
    (let [first-matching-production (comp first identity)
          results (p/step first-matching-production KB)]
      (is (= #{[-1000 -50]}
             (d/q '[:find ?from ?to
                    :where 
                    [?to :emergency/type :emergency.type/fire]
                    [?from :response/to ?to]] 
                  results)))

      (is (= #{[:response.type/activate-sprinklers]}
             (d/q '[:find ?response
                    :where 
                    [_ :response/type ?response]] 
                  results))))))

(deftest test-cycle
  (testing "that the whole cycle occurs as expected"
    (let [results (p/cycle p/naive-qf
                           '{:productions [{:antecedent   [[?id :person/name ?n]
                                                           [?id :isa/human? true]]
                                            :consequent [[?id :isa/mortal? true]]}]
                             :facts #{[42 :person/name "Socrates"]
                                      [42 :isa/human? true]}})]
      (is (= #{[42 :isa/mortal? true] [42 :isa/human? true] [42 :person/name "Socrates"]}
             results)))
    (let [results (p/cycle p/naive-qf KB)]
      (is (= #{[:response.type/kill-electricity] [:response.type/activate-sprinklers]}
             (d/q '[:find ?response
                    :where
                    [_ :response/type ?response]]
                  results)))

      (is (= #{[:emergency.type/fire   :response.type/activate-sprinklers]
               [:emergency.type/flood  :response.type/kill-electricity]}
             (d/q '[:find ?problem ?response
                    :where
                    [?id :response/type   ?response]
                    [?id :response/to     ?pid]
                    [?pid :emergency/type ?problem]]
                  results))))))
