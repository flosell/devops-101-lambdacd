(ns devops-101-pipeline.deploy-scripts
  (:require [lambdacd.util :as util]
            [clojure.java.io :as io]))

(defn copy-to [dir resource-name]
  (spit (str dir "/" resource-name) (slurp (io/resource (str "deployscripts/" resource-name)))))

(defn prepare-deploy-scripts []
  (let [basedir (util/create-temp-dir)]
    (copy-to basedir "app-server-template.json")
    (copy-to basedir "deploy-new-app-server.rb")
    (copy-to basedir "retire-old-app-server.rb")
    basedir))