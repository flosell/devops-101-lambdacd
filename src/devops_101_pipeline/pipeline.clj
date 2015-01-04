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
     (either
       lambdacd.manualtrigger/wait-for-manual-trigger
       wait-for-repo)
     (with-repo
        commit-step)
     deploy-step
  ))
