(ns devops-101-pipeline.steps
  (:require [lambdacd.shell :as shell]
            [lambdacd.execution :as execution]
            [lambdacd.git :as git]
            [lambdacd.manualtrigger :as manualtrigger]
            [clojure.core.strint :as s]
            [clojure.java.io :as io]
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


(defn copy-to [dir resource-name]
  (spit (str dir "/" resource-name) (slurp (io/resource resource-name))))

(defn prepare-deploy-scripts []
  (let [basedir (util/create-temp-dir)]
    (copy-to basedir "app-server-template.json")
    (copy-to basedir "deploy-new-app-server.rb")
    (copy-to basedir "retire-old-app-server.rb")
   basedir))

(defn deploy [s3-address build-id ctx]
  (let [basedir (prepare-deploy-scripts)]
    (shell/bash ctx basedir
                (str "/usr/local/bin/ruby deploy-new-app-server.rb " s3-address " " build-id)
                (str "/usr/local/bin/ruby retire-old-app-server.rb " build-id))))

(defn deploy-step [{s3-address :s3-address build-id :build-id} ctx]
  (deploy s3-address build-id ctx))