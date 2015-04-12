(ns devops-101-pipeline.core
  (:use compojure.core)
  (:require [devops-101-pipeline.meta.pipeline :as metapipeline]
            [devops-101-pipeline.pipeline :as pipeline]
            [ring.server.standalone :as ring-server]
            [lambdacd.core :as lambdacd]
            [hiccup.core :as h]
            [hiccup.page :as page]
            [lambdacd.util :as util]
            [lambdacd.ui.ui-server :as ui]
            [lambdacd.runners :as runners]
            [lambdacd-cctray.core :as cctray]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import (java.io File))
  (:gen-class))

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

(defn mk-routes [meta-routes pipeline-routes cctray-pipeline-handler]
  (routes
    (context "/meta" [] meta-routes)
    (context "/pipeline" [] pipeline-routes)
    (GET "/cctray/pipeline.xml" [] cctray-pipeline-handler)
    (GET "/" [] (index))))

(defn -main [& args]
  (let [home-dir (if (not (empty? args)) (first args) (util/create-temp-dir))
        meta-home-dir (ensure-dir home-dir "meta")
        pipeline-home-dir (ensure-dir home-dir "pipeline")
        meta-pipeline (lambdacd/assemble-pipeline metapipeline/pipeline-def {:home-dir meta-home-dir})
        pipeline (lambdacd/assemble-pipeline pipeline/pipeline-def {:home-dir pipeline-home-dir})
        cctray-pipeline-handler (cctray/cctray-handler-for pipeline/pipeline-def (:state pipeline))]
    (log/info "home-dir is" home-dir)
    (runners/start-one-run-after-another meta-pipeline)
    (runners/start-one-run-after-another pipeline)
    (ring-server/serve
      (mk-routes (ui/ui-for meta-pipeline) (ui/ui-for pipeline) cctray-pipeline-handler)
      {:open-browser? false
       :port 8080})))