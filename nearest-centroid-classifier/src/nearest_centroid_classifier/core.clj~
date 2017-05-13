(ns nearest-centroid-classifier.core
  ;; TODO: prepare for workshop; remove the require bellow
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def test-data [5.1,3.5,1.4,0.2]) ;; it is setosa

(defn read-raw [path]
  (with-open [in-file (io/reader path)]
    (doall
     (rest (csv/read-csv in-file)))))

(defn parse-elem [elem]
  (if (-> elem read-string symbol?) elem (read-string elem)))

(defn parse-line [raw-line]
  (vec (map parse-elem raw-line)))

(defn process-line [id parsed-line]
  {:id id :data parsed-line})

(defn process [raw]
  (map-indexed #(process-line %1 (parse-line %2)) raw))

;; @alex TODO: use a future to show threading
(def input-data (process (take-last 120 (read-raw "resources/iris.csv"))))

;; ............................................ ALGORITHM

;; Training procedure: given labeled training samples {(xv1,y1) ... (xvn,yn)}
;; computed the per-class centroids miu-l =  1/|Cl| SUM xvi for i in Cl; Cl is the set of indices of samples belonging to class l ∈ Y

;; Prediction function: the class assigned to an observation xv is
;; y = arg min || miu-l - xv || for l ∈ Y

(defn sum-vectors [v1 v2 & vecs]
  (vec (apply map + v1 v2 vecs)))

(defn subb-vectors [v1 v2]
  (vec (map - v1 v2)))

(defn class-pred [x class-name]
  (-> x :data last (= class-name)))

(defn filter-class [data class-name]
  (filter (fn [x] (class-pred x class-name)) data))

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
  (let [filtered-class (filter-class data class-name)
        class-vectors (map #(-> % :data butlast vec)
                           filtered-class)
        class-idx (build-class-idx filtered-class)
        norm (count class-idx)]
    ;; compute miu
    (conj (vec (map #(/ % norm) (apply sum-vectors class-vectors))) class-name)))

;;@alex TODO: use a promise to show multitrheading
(def miu-setosa (compute-centroid input-data setosa-class))
(def miu-versi (compute-centroid input-data versi-class))
(def miu-virginica (compute-centroid input-data virginica-class))

(def mius (map #(compute-centroid input-data %) [setosa-class versi-class virginica-class]))

; ....................... PREDICT
;; use euclidean norm
(defn compute-distance [miu input]
  (let [miu-vector (butlast miu)
        miu-class (last miu)]
    [(reduce + (map #(* % %) (subb-vectors miu input))) miu-class]))


(defn arg-min [distances]
  (reduce (fn [[d1 c1] [d2 c2]]
            (if (<= d1 d2)
              [d1 c1]
              [d2 c2]))
          distances))

(defn predict [input mius]
  (arg-min (map #(compute-distance % input) mius)))

(defn test [] (predict test-data mius))
