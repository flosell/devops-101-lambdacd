(ns devops-101-pipeline.pipeline
  (:use [lambdacd.steps.control-flow]
        [devops-101-pipeline.steps]))

(def pipeline-def
  `(
     (either
       lambdacd.steps.manualtrigger/wait-for-manual-trigger
       wait-for-repo)
     (with-repo
        commit-step
        deploy-step)))
