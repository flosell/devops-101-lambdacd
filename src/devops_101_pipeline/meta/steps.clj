(ns devops-101-pipeline.meta.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]))

(defn pipeline-working-directory []
  (System/getProperty "user.dir"))

(def devops-101-repo "file:///tmp/some-pipeline")

(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx devops-101-repo "master"))

(defn ^{:display-type :container} with-repo [& steps]
  (git/with-git devops-101-repo steps))

(defn build-pipeline [args ctx]
  (let [timestamp (System/currentTimeMillis)
        artifact-path (str (pipeline-working-directory) "/pipeline-" timestamp "-standalone.jar")]
    (shell/bash ctx (:cwd args)
                "set -x"
                "lein test"
                "lein ring uberjar"
                (str "mv -v target/*-standalone.jar " artifact-path))))

(defn stop-to-restart [args ctx]
  (System/exit 0))