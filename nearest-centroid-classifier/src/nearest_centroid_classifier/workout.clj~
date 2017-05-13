(ns nearest-centroid-classifier.workout
  ;; TODO: prepare for workshop; remove the require bellow
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def test-data [5.1,3.5,1.4,0.2]) ;; it is setosa

; ..................................... DATA PROCESSING

(defn read-raw [...args]
  ;; this function should receive a single argument; a string denoting a valid path to a csv file;
  ;; it will use clojure.data.csv API to read the csv file into a Clojure data structure - a lazy sequence of vectors
  ;; return the above content as a sequence of vectors
  ....fn body)

(defn parse-elem [elem]
  (if (string? elem) elem (read-string elem)))

(defn parse-line [...args]
  ;; this fn will receive a single line denoting a csv line - i.e. a Clojure vector with strings;
  ;; the argument is a vector; make sure to preserve it
  ;; make sure strings remain string; use the pare-elem fn from above
  ;; the output should be a vector with Clojure data in it (i.e. "3" -> 3)
  ....fn body
  )

(defn process-line [...args]
  ;; this function receives as arguments:
  ;;     * an id-value which is a positive integer (0,1,2....)
  ;;     * data - the output from parse-line above 
  ;; it will output a map: {:id id-value :data data}
  ....fn body)

(defn process [...args]
  ;; we use this function to mainly walk a raw csv input
  ;; input argument is a vector of vectors; each vector is a csv line - a vector of strings
  ;; output should be an enriched sequence of Clojure datastructures depicting a csv line; we use the above process-line for this
  ;; use map-indexed
  ....fn body)

;; @alex TODO: use a future to show threading
(def input-data (process (take-last 120 (read-raw "resources/iris.csv"))))
;; should output something like:
;;
;; ({:id 0, :data [4.8 3.1 1.6 0.2 "setosa"]} {:id 1, :data [5.4 3.4 1.5 0.4 "setosa"]} {:id 2, :data [5.2 4.1 1.5 0.1 "setosa"]} .....)



;; ............................................ ALGORITHM

;; Training procedure: given labeled training samples {(xv1,y1) ... (xvn,yn)}
;; computed the per-class centroids miu-l =  1/|Cl| SUM xvi for i in Cl; Cl is the set of indices of samples belonging to class l ∈ Y

;; Prediction function: the class assigned to an observation xv is
;; y = arg min || miu-l - xv || for l ∈ Y

(defn sum-vectors [v1 v2 & vecs]
  ;; given any number n of vectors - but at least two are passed as arguments
  ;; apply the vector-sumation operation; make sure you return a vector
  ....fn body
  ;; (vec (apply map + v1 v2 vecs))
  )

(defn subb-vectors [v1 v2]
  ;; subbtract two vectors; similar to sum but only take two vectors
  ....fn body
  ;(vec (map - v1 v2))
  )

(defn class-pred [xm class-name]
  ;; write a predicate function
  ;; this should return true or false
  ;; it received an input map xm
  ;; {k1 v1 .... :data [interger interger interger .... class]|
  ;; you need to extract the vector value associated with :data keyword
  ;; get the last element from the associated vector
  ;; and compare it to the class-name argument
  ....fn body
  ;;(-> x :data last (= class-name))
  )

(defn filter-class [data class-name]
  ;; this function takes our enriched data and a class-name (e.g. "setosa")
  ;; and filters out all inputs from data that don't have the class equal to
  ;; class name
  ;; use the filter function and the class-pred predicate above to do this
  ....fn body
  ;;(filter (fn [x] (class-pred x class-name)) data)
  )

;; TODO: extract classes from csv; for now we'll hardcode them:
(def setosa-class "setosa")
(def versi-class "versicolor")
(def virginica-class "virginica")

(defn build-class-idx [filtered-data]
  ;; we use this function to get the indexes of the same class data
  (map :id filtered-data))

(def cl-setosa (build-class-idx (filter-class input-data setosa-class)))
(def cl-versi (build-class-idx (filter-class input-data versi-class)))
(def cl-virginica (build-class-idx (filter-class input-data virginica-class)))

(defn compute-centroid [data class-name]
  ;; write the function to compoute the centroid for a certain class in our data
  ;; the centroid is a vector
  
  (let [filtered-class  ...your-code;; first get the data filtered by class-name using filetr-class function we defined above
        class-vectors (map #(-> % :data butlast vec) ;; the vec call is very important to preserve the vectors
                           filtered-class) ;; we remove the class name from our data
        class-idx ...your-code ;; build the class indexes vector using build-class-idx
        norm ...your-code ;; count how many indexes we've got
        ]
    ;; compute miu vector for this class-name
    ;; * sum all vectors left in class-vectors using sum-vectors
    ;; * divide all elements inside the resulting vector by norm
    ;; * make-it a vector (using vec) and add at the end the class-name (using conj)
    ....fn body))

;;@alex TODO: use a promise to show multitrheading
(def miu-setosa (compute-centroid input-data setosa-class))
(def miu-versi (compute-centroid input-data versi-class))
(def miu-virginica (compute-centroid input-data virginica-class))

;; mius var will hold all centroids for our classes: [setosa-class versi-class virginica-class]
(def mius (map ...your-code ;;#(compute-centroid input-data %)
               [setosa-class versi-class virginica-class]))

; ....................... PREDICT
;; use euclidean norm: ||x - y|| = SUM after i=0..n (x[i] - y[i])^2
(defn compute-distance [miu input]
  (let [miu-vector ...your-code ;; use butlast to get the useful data
        miu-class ...your-code ;; use last to get class-name
        ]
    [...your-code
     ;; * subtract miu-vector and input
     ;; * raise to the power of 2 each element from the resulting vector
     ;; * sum all elements of the vector using reduce
     miu-class]))


(defn arg-min [distances]
  ;; use reduce with a lambda function over all elements in the distances to get
  ;; the lowest input
  
  ;; ALTERNATIVE: you coud use a sorting function with a specific comparator
  (reduce (fn [[d1 c1] [d2 c2]]
            ...your-code ;; write an appropriate if
            )
          distances))

(defn predict [input mius]
  ;; the predict function comes out naturaly by using arg-min function
  ;; over the computed distances betwen mius and input
  ;; * first: compute each distance from input to an element in mius
  ;;          using the compute-distance function above and map
  ;; * second: call arg-min on the result obatin in first
  ...your-code)

(defn test [] (predict test-data mius)) ;; should ouput [some-number-distance "setosa"]
