(ns playground.concurent)

;; threads: launch a thread or stuff in paralel
;; pcalls
;; pvalues
;; pmap
;; future - block
;; promise - block
;; delay - block
;; doall, dorun, dotimes/doseq, repeat, repeatedly



;; atoms

(def hidrogen (atom {:id "H" :atomic-no "1"}))

(defn run-atoms []
  (let [thrd (fn [nid] (future
                         (do (Thread/sleep 300)
                             (swap! hidrogen #(assoc % :id nid)))))
        
        exec #(do (prn @hidrogen)
                  (thrd %) ;; update atom in another thread
                  (Thread/sleep 200)
                  (prn @hidrogen))]
    (time (dotimes [run 10] (exec run)))))

;; refs

(def a-ref (ref [1 2 1])) ;; second line of pascal triangle

(defn update-a-ref []
  (prn "Current value of a-ref: " @a-ref)
  ;; (ref-set a-ref [1 3 3 1])

  (dosync (ref-set a-ref [1 4 6 4 1])
          (commute a-ref (fn [current-state] [1 5 10 10 5 1])))
          (alter a-ref (fn [current-state] (conj current-state 10)))

  ;; TRYOUT IN DIFF THREADS:
  ;; (future (dorun (apply pcalls (dotimes [id 20] (do (Thread/sleep 100)
  ;;                                                   (prn "PCALLS: " id " " @a-ref))))))
  ;; (future
  ;;   (dosync
  ;;    (prn "DOSYNC-1: " @a-ref)
  ;;    (ref-set a-ref [1 3 3 1])
  ;;    (Thread/sleep 1000)
  ;;    (prn "DOSYNC-2: "@a-ref)))
  )

(defn gen-refs[nvecs nitems]
  ;; TODO: 
  ;;
  ;;
  ;; (vec (map (comp ref vec)
  ;;                          (partition nitems (range (* nvecs nitems)))))
  )

(defn run-refs [nvecs nitems nthreads niters]
  (let [vec-refs (gen-refs nvecs nitems)
        swap #(let [v1 (rand-int nvecs)
                    v2 (rand-int nvecs)
                    i1 (rand-int nitems)
                    i2 (rand-int nitems)]
                (dosync
                 (let [temp (nth @(vec-refs v1) i1)]
                   (alter (vec-refs v1) assoc i1 (nth @(vec-refs v2) i2))
                   (alter (vec-refs v2) assoc i2 temp))))
        report #(do
                 (prn (map deref vec-refs))
                 (println "Distinct:"
                          (count (distinct (apply concat (map deref vec-refs))))))]
    (report)
    (dorun (apply pcalls (repeat nthreads #(dotimes [_ niters] (swap)))))
    (report)))




;; agents
(def secret-agent (agent {:id 007 :name :james-bond}))

;; agents have theyr own thread-pool
(import java.util.concurrent.Executors)
(set-agent-send-executor! (Executors/newFixedThreadPool 10))

(defn thread-pool-test []
  (dotimes [_ 20] (send secret-agent #(do (Thread/sleep 100)
                                          (assoc % :id (-> % :id inc))))))
;; mesages are interleved

;; send actions get executed in the sent order

(defn relay [x i]
  (when (:next x)
    (send (:next x) relay i))
  (when (and (zero? i) (:report-queue x))
    (.put (:report-queue x) i))
  x)

(defn run-agents [m n]
  (let [q (new java.util.concurrent.SynchronousQueue)
        hd (reduce (fn [next _] (agent {:next next}))
                   (agent {:report-queue q}) (range (dec m)))]
    (doseq [i (reverse (range n))]
      (send hd relay i))
    (.take q)))




