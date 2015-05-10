(ns devops-101-pipeline.deploy-scripts
  (:require [lambdacd.util :as util]
            [clojure.java.io :as io]
            [lambdacd.steps.shell :as shell])
  (:import (org.reflections.util ConfigurationBuilder ClasspathHelper)
           (org.reflections.scanners ResourcesScanner)
           (org.reflections Reflections)
           (java.net URL)))

(defn copy-to [dir resource-name]
  (let [res-file-name (last (.split resource-name "/"))]
    (spit (str dir "/" res-file-name) (slurp (io/resource resource-name)))))

(defn prepare-deploy-scripts [basedir]
  (copy-to basedir "deployscripts/demo-app.conf")
  (copy-to basedir "deployscripts/deploy-app.yml")
  (copy-to basedir "deployscripts/deploy-app.sh"))

(defn deploy [basedir ctx]
  (prepare-deploy-scripts basedir)
  (shell/bash ctx basedir
              "bash ./deploy-app.sh"))
