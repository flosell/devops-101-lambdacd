(ns devops-101-pipeline.core
  (:use compojure.core)
  (:require [devops-101-pipeline.meta.pipeline :as metapipeline]
            [devops-101-pipeline.pipeline :as pipeline]
            [ring.util.response :as resp]
            [hiccup.core :as h]
            [hiccup.page :as page]))

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

(def app
  (routes
    (context "/meta" [] metapipeline/app)
    (context "/pipeline" [] devops-101-pipeline.pipeline/app)

    (GET "/" [] (index))))

(defn start-pipeline-thread []
  (metapipeline/start-pipeline-thread)
  (pipeline/start-pipeline-thread))