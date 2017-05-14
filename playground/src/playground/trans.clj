(ns playground.trans)

(def v (vec (repeat 10000000 {:a 1 :b 2})))

(time (def r1 (vec (map :a v))))

(time (def r2 (loop [vt (transient []) v v] (if (-> v seq nil?) (persistent! vt) (recur (conj! vt (:a (first v))) (rest v))))))
