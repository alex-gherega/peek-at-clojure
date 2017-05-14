(ns ncc-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [nearest-centroid-classifier.core :as ncc]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn process-iris-data [a b c d]
  ;; (vec (map read-string [a b c d]))
  (into [] (map read-string) [a b c d]))

(defroutes app-routes
  (GET "/" [] "Hello World")

  (GET "/test" [a b c d] (let [test-data (process-iris-data a b c d)]
                           (str (ncc/predict test-data ncc/mius))))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
