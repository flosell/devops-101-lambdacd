(ns devops-101-pipeline.pipeline
  (:use [lambdacd.execution]
        [lambdacd.core]
        [lambdacd.control-flow]
        [devops-101-pipeline.steps])
  (:require
        [lambdacd.util :as util]
        [clojure.tools.logging :as log]))



(def pipeline-def
  `(
    lambdacd.manualtrigger/wait-for-manual-trigger
     (with-repo
       commit-step)
  ))


(def home-dir (util/create-temp-dir))
(log/info "LambdaCD Home Directory is " home-dir)
(def config { :home-dir home-dir})

(def pipeline (mk-pipeline pipeline-def config))

(def app (:ring-handler pipeline))
(def start-pipeline-thread (:init pipeline))
