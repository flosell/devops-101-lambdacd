(ns devops-101-pipeline.core
  (:use compojure.core)
  (:require [devops-101-pipeline.meta.pipeline :as metapipeline]
            [devops-101-pipeline.pipeline :as pipeline]
            [ring.server.standalone :as ring-server]
            [lambdacd.util :as uti]
            [lambdacd.core :as lambdacd]
            [hiccup.core :as h]
            [hiccup.page :as page]
            [lambdacd.util :as util]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import (java.io File)))

(defn ensure-dir [parent dirname]
  (let [d (io/file parent dirname)]
    (.mkdirs d)
    d))

  (defn- index []
  (h/html
    (page/html5
      [:head
       [:title "Pipelines"]]
      [:body
       [:h1 "Pipelines"]
       [:ul
        [:li [:a {:href "pipeline/"} "DevOps 101 Demo Pipeline"]]
        [:li [:a {:href "meta/"} "Meta-Pipeline"]]]])))

(defn mk-routes [meta-routes pipeline-routes]
  (routes
    (context "/meta" [] meta-routes)
    (context "/pipeline" [] pipeline-routes)

    (GET "/" [] (index))))

(defn -main [& args]
  (let [home-dir (if (not (empty? args)) (first args) (util/create-temp-dir))
        meta-home-dir (ensure-dir home-dir "meta")
        pipeline-home-dir (ensure-dir home-dir "pipeline")
        meta-pipeline (lambdacd/mk-pipeline metapipeline/pipeline-def {:home-dir meta-home-dir})
        pipeline (lambdacd/mk-pipeline pipeline/pipeline-def {:home-dir pipeline-home-dir})]
    (log/info "home-dir is" home-dir)
    ((:init meta-pipeline))
    ((:init pipeline))
    (ring-server/serve (mk-routes (:ring-handler meta-pipeline) (:ring-handler pipeline)))))