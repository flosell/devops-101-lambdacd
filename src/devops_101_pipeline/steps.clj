(ns devops-101-pipeline.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]
            [clojure.core.strint :as s]
            [devops-101-pipeline.deploy-scripts :as deploy-scripts]))

(def devops-101-repo "https://github.com/flosell/devops-101.git")

(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx devops-101-repo "master"))

(defn ^{:display-type :container} with-repo [& steps]
  (git/with-git devops-101-repo steps))

(defn commit-step [{cwd :cwd revision :revision} ctx]
  (let [app-folder (str cwd "/part-four/application")
        shell-result (shell/bash ctx app-folder
                                 "set -x"
                                 "lein test"
                                 "lein uberjar"
                                 "mv target/uberjar/*-standalone.jar ../../app.jar")]
    shell-result))


(defn deploy-step [{cwd :cwd} ctx]
  (deploy-scripts/deploy cwd ctx))


(defn smoke-test [args ctx]
  (shell/bash ctx "/"
              "while [ `curl --write-out %{http_code} --silent --output /dev/null http://app-server:8080/` != 200 ]; do echo waiting; sleep 1; done"))