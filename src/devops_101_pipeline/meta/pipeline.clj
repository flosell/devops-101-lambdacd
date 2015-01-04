(ns devops-101-pipeline.meta.pipeline
  (:use [lambdacd.execution]
        [lambdacd.core]
        [lambdacd.control-flow]
        [devops-101-pipeline.meta.steps]))

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
