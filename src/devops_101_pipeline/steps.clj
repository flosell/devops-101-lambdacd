(ns devops-101-pipeline.steps
  (:require [lambdacd.shell :as shell]
            [lambdacd.execution :as execution]
            [lambdacd.git :as git]
            [lambdacd.manualtrigger :as manualtrigger]
            [clojure.core.strint :as s]))

(def devops-101-repo "git@github.com:flosell/devops-101.git")

(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx devops-101-repo "master"))

(defn ^{:display-type :container} with-repo [& steps]
  (git/with-git devops-101-repo steps))

(defn commit-step [{cwd :cwd revision :revision} & _]
  (let [app-folder (str cwd "/part-four/application")
        jar-file (s/<< "application-~{revision}-standalone.jar")]
    (shell/bash app-folder
                "lein test"
                "lein uberjar"
                (s/<< "mv --verbose target/uberjar/application*-standalone.jar ~{jar-file}"))))