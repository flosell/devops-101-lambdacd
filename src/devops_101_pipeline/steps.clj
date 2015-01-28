(ns devops-101-pipeline.steps
  (:require [lambdacd.shell :as shell]
            [lambdacd.execution :as execution]
            [lambdacd.git :as git]
            [lambdacd.manualtrigger :as manualtrigger]
            [clojure.core.strint :as s]
            [clojure.java.io :as io]
            [devops-101-pipeline.deploy-scripts :as deploy-scripts]
            [lambdacd.util :as util]))

(def devops-101-repo "https://github.com/flosell/devops-101.git")

(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx devops-101-repo "master"))

(defn ^{:display-type :container} with-repo [& steps]
  (git/with-git devops-101-repo steps))

(defn commit-step [{cwd :cwd revision :revision} ctx]
  (let [app-folder (str cwd "/part-four/application")
        timestamp (System/currentTimeMillis)
        build-id (s/<< "~{revision}-~{timestamp}")
        jar-file (s/<< "application-~{build-id}-standalone.jar")
        s3-address (s/<< "s3://devops-101-lambdacd/~{jar-file}")
        shell-result (shell/bash ctx app-folder
                                 "set -x"
                                 "lein test"
                                 "lein uberjar"
                                 (s/<< "mv -v target/uberjar/application*-standalone.jar ~{jar-file}")
                                 (s/<< "aws s3 cp ~{jar-file} ~{s3-address}"))]
    (assoc shell-result :s3-address s3-address :build-id build-id)))


(defn deploy-step [{s3-address :s3-address build-id :build-id} ctx]
  (deploy-scripts/deploy s3-address build-id ctx))