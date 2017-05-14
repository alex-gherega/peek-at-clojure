(ns nearest-centroid-classifier.t-core
  ;; TODO: prepare for workshop; remove the require bellow
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

;; when you see a reducing pattern transform with transducers
(def test-data [5.1,3.5,1.4,0.2]) ;; it is setosa

(defn read-raw [path]
  (with-open [in-file (io/reader path)]
    (doall
     (rest (csv/read-csv in-file)))))

(defn parse-elem [elem]
  (if (-> elem read-string symbol?) elem (read-string elem)))

(defn process-line [id parsed-line]
  {:id id :data (butlast parsed-line) :class (last parsed-line)})

(defn process [raw]
  ;; let's be lazy;  one could parse now the data using parse-line above
  (map-indexed #(process-line %1 %2) raw))

;; @alex TODO: use a future to show threading
(def input-data (process (take-last 120 (read-raw "resources/iris.csv"))))

;; ............................................ ALGORITHM

;; Training procedure: given labeled training samples {(xv1,y1) ... (xvn,yn)}
;; computed the per-class centroids miu-l =  1/|Cl| SUM xvi for i in Cl; Cl is the set of indices of samples belonging to class l ∈ Y

;; Prediction function: the class assigned to an observation xv is
;; y = arg min || miu-l - xv || for l ∈ Y

(defn sum-vectors [v1 v2 & vecs]
  ;; given many vectors do usual sumation
  (let [+ (fn [& x] (reduce #(+ %1 (read-string %2))
                            (-> x first read-string)
                            (rest x)))]
    (vec (apply map + v1 v2 vecs))))

;; transducerized:
(defn sum-tvectors [v1 v2 & vecs]
  (let [+ (fn [& x] (transduce (map read-string) + x))]
    (vec (apply map + v1 v2 vecs))))

(defn subb-vectors [v1 v2]
  (vec (map - v1 v2)))

(defn filter-class [data class-name]
  (filter (fn [x] (= (:class x) class-name))
          data))

(defn filter-tclass [class-name]
  (filter (fn [x] (= (:class x) class-name))))

;; TODO: extract classes from csv; for now we'll hardcode them:
(def setosa-class "setosa")
(def versi-class "versicolor")
(def virginica-class "virginica")

(defn build-class-idx [filtered-data]
  (map :id filtered-data))

(def cl-setosa (build-class-idx (filter-class input-data setosa-class)))
(def cl-versi (build-class-idx (filter-class input-data versi-class)))
(def cl-virginica (build-class-idx (filter-class input-data virginica-class)))

(defn compute-centroid [data class-name]
  (let [interes-data (filter-class data class-name)
        ;; with transducers:
        ;; interes-data (eduction (filter-tclass class-name) data)
        interes-data (map :data interes-data)
        cardinality (count interes-data)]
    ;; compute miu
    {:class class-name
     :miu (vec (map #(/ % cardinality)
                    (apply sum-vectors interes-data)))}))

;;@alex TODO: use a promise to show multitrheading
(def miu-setosa (compute-centroid input-data setosa-class))
(def miu-versi (compute-centroid input-data versi-class))
(def miu-virginica (compute-centroid input-data virginica-class))

(def mius (map #(compute-centroid input-data %) [setosa-class versi-class virginica-class]))

; ....................... PREDICT
;; use euclidean norm
(defn compute-distance [miu input]
  (let [miu-vector (:miu miu)
        miu-class (:class miu)]
    [(reduce + (map #(* % %) (subb-vectors miu-vector input))) miu-class]))

(def squaring-xform (map #(* % %)))

(defn compute-tdistance [miu input]
  (let [miu-vector (:miu miu)
        miu-class (:class miu)]
    [(transduce  squaring-xform + (subb-vectors miu-vector input)) miu-class]))

(defn arg-min [distances]
  (reduce (fn [[d1 c1] [d2 c2]]
            (if (<= d1 d2)
              [d1 c1]
              [d2 c2]))
          distances))

(defn predict [input mius]
  (arg-min (map #(compute-tdistance % input) mius)))

(defn test [] (predict test-data mius))
