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

(defn resource-config [package]
  (-> (ConfigurationBuilder.)
      (.setUrls (ClasspathHelper/forPackage package
                                                         (into-array ClassLoader `())))
      (.setScanners (into-array ResourcesScanner [(ResourcesScanner.)]))))

(defn all-resources-in [package]
  (let [all-resources (-> (Reflections. (resource-config package))
                          (.getResources (re-pattern ".*")))]
    (filter #(.contains % package) all-resources)))

(defn prepare-deploy-scripts []
  (let [all-resources (all-resources-in "deployscripts")
        basedir (util/create-temp-dir)]
    (doall (map (partial copy-to basedir) all-resources))
    basedir))

(defn deploy [ctx]
  (let [basedir (prepare-deploy-scripts)]
    (shell/bash ctx basedir
                "bash ./deploy-app.sh")))