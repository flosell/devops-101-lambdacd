(ns devops-101-pipeline.meta.pipeline
  (:use [lambdacd.execution]
        [lambdacd.core]
        [lambdacd.control-flow]
        [devops-101-pipeline.meta.steps])
  (:require
        [lambdacd.util :as util]
        [clojure.tools.logging :as log]))



(def pipeline-def
  `(
     (either
       lambdacd.manualtrigger/wait-for-manual-trigger
       wait-for-repo)
     (with-repo
       build-pipeline)
     lambdacd.manualtrigger/wait-for-manual-trigger
     stop-to-restart
  ))


(def home-dir (util/create-temp-dir))
(log/info "LambdaCD Home Directory is " home-dir)
(def config { :home-dir home-dir})

(def pipeline (mk-pipeline pipeline-def config))

(def app (:ring-handler pipeline))
(def start-pipeline-thread (:init pipeline))
