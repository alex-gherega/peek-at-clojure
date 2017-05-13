(ns nearest-centroid-classifier.lean-core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

;; read & process data
(defn read-raw [path]
  (with-open [in-file (io/reader path)]
    (-> (csv/read-csv in-file) rest doall)))


(defn process [raw-data]
  (map-indexed #(hash-map :id %1 :data (vec (butlast %2)) :class (last %2)) raw-data))

;; filter class
(defn filter-class [data class-name]
  (filter #(= class-name (:class %)) data))

;; compute a centroid for a class
(defn compose-vectors [op v1 v2 & vecs]
  (vec (apply map op (into [v1 v2] vecs))))

(defn sum-vectors [v1 v2 & vecs]
  (apply compose-vectors + v1 v2 vecs))

(defn sum-with-+ [& args]
  (let [head (-> args first read-string)]
    (reduce #(+ %1 (read-string %2)) head (rest args))))

(defn compute-centroid [data class-name]
  (let [interes-data (map :data (filter-class data class-name))
        cardinality (count interes-data)]    
    {:class class-name
     :data (vec (map #(/ % cardinality)
                     (apply compose-vectors
                            sum-with-+
                            interes-data)))}))

;; prediction
(defn distance [v1 v2]
  (let [v1-v2 (map - v1 v2)
        squares (map #(* % %) v1-v2)]
    (apply + squares)))

(defn arg-min [distance-maps]
  (reduce #(if (< (:d %1) (:d %2))
             %1
             %2)
          distance-maps))

(defn predict [y centroids]
  (arg-min (map #(hash-map :d (distance y (:data %)) :class (:class %))
                centroids)))
