(ns yadalean01.core
  (:require
    [clojure.java.io :as io]
    [yada.yada :as yada]
    [yada.body :refer [render-map]]
    [cheshire.core :as json]
    [yada.json-html]
    (:gen-class)))
;introduces defmethod render-map "text/html" for
;https://github.com/juxt/yada/blob/a8b2742e69d729c3f175d04f3a58f928c11a04be/ext/json-html/src/yada/json_html.clj#L10

(def json-home
  "The JSON Home document for this component
   See https://mnot.github.io/I-D/json-home/"
  (->> "json-home.edn" io/resource slurp read-string))

(defmethod render-map "application/json-home" [m _]
  (str (json/encode m {:escape-non-ascii true, :pretty false}) "\n"))

(defmethod render-map "application/hal+json" [m _]
  (str (json/encode m {:escape-non-ascii true, :pretty true}) "\n"))

(def index-resource
  (yada/resource {
                  :methods {
                            :get {
                                  :produces ["text/html"
                                             "application/json-home"
                                             "application/hal+json"
                                             "application/json"
                                             "application/edn"]
                                  :response json-home}}}))

(def startup
  (yada/listener
    ["/" index-resource]
    {:port 3000}))

(defn -main
  "start the server on 3000"
  [& args] (aleph.netty/wait-for-close (:server startup)))
