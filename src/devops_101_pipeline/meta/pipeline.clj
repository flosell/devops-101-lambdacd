(ns devops-101-pipeline.meta.pipeline
  (:use [lambdacd.core]
        [lambdacd.steps.control-flow]
        [devops-101-pipeline.meta.steps]))

(def pipeline-def
  `(
     (either
       manual-trigger
       wait-for-repo)
     (with-repo
       build
       deploy)
     lambdacd.steps.manualtrigger/wait-for-manual-trigger
     stop-to-restart
  ))
