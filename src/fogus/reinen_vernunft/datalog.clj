(ns fogus.reinen-vernunft.datalog
  "A modified version of Christophe Grand's 39loc Datalog
  implementation constrained to EAV-style tuples.")

(defn constrain [env [op & args]]
  (let [args (map #(let [v (env % %)] (if (set? v) % v)) args)]
    (if-some [free-var (->> args (filter symbol?) first)]
      (update env free-var (fnil conj #{}) (cons op args))
      (when (apply (case op not= not= = =) args) env))))

(defn bind [env p v]
  (let [p-or-v (env p p)]
    (cond
      (= p '_) env
      (= p-or-v v) env
      (symbol? p-or-v) (assoc env p v)
      (set? p-or-v) (reduce constrain (assoc env p v) p-or-v))))

(defn match [pattern fact env]
  (println :----> (map vector pattern fact))
  (when (= (count pattern) (count fact))
    (reduce (fn [env [p v]] (or (bind env p v) (reduced nil)))
            env
            (map vector pattern fact))))

(defn match-patterns [patterns dfacts facts]
  (reduce
    (fn [[envs denvs] pattern]
      (if (seq? pattern)
        [(->> @envs (keep #(constrain % pattern)) set delay)
         (->> denvs (keep #(constrain % pattern)) set)]
        [(-> #{} (into (for [fact facts env @envs] (match pattern fact env))) (disj nil) delay)
         (-> #{}
           (into (for [fact facts env denvs] (match pattern fact env)))
           (into (for [fact dfacts env denvs] (match pattern fact env)))
           (into (for [fact dfacts env @envs] (match pattern fact env)))
           (disj nil))]))
    [(delay #{{}}) #{}] patterns))

(defn match-rule [dfacts facts [head & patterns]]
  (for [env (second (match-patterns patterns dfacts facts))]
    (into [] (map #(env % %)) head)))

(defn saturate [facts rules]
  (loop [dfacts facts, facts #{}]
    (let [facts' (into facts dfacts)
          dfacts' (into #{} (comp (mapcat #(match-rule dfacts facts %)) (remove facts')) rules)]
      (cond->> facts' (seq dfacts') (recur dfacts')))))

(defn q* [facts query rules]
  (-> facts (saturate rules) (match-rule #{} query) set))

(defn q
  ([query db] (q query db '()))
  ([query db rules]
   (let [qq (->> query
                 (partition-by keyword?)
                 (partition 2)
                 (map (fn [[[k] v]] [k v]))
                 (into {})
                 ((juxt :find :where))
                 (apply list*))]
     (q* db qq rules))))

(comment

  (def fdb
    #{[-1002 :response/to -51]
      [-51 :emergency/type :emergency.type/flood]
      [-50 :emergency/type :emergency.type/fire]
      [-1002 :response/type :response.type/kill-electricity]
      [-1000 :response/to -50]
      [-1000 :response/type :response.type/activate-sprinklers]})

  (q* fdb
     '([?response] [_ :response/type ?response])
     '())

  (q* fdb
     '([?problem ?response]
       [?id :response/type   ?response]
       [?id :response/to     ?pid]
       [?pid :emergency/type ?problem])
     '())

  (q '[:find ?problem ?response
       :where
       [?id :response/type   ?response]
       [?id :response/to     ?pid]
       [?pid :emergency/type ?problem]]
     fdb)
)


