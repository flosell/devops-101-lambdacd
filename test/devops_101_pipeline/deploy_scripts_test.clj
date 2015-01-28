(ns devops-101-pipeline.deploy-scripts-test
  (:require [clojure.test :refer :all]
            [devops-101-pipeline.deploy-scripts :refer :all]
            [devops-101-pipeline.deploy-scripts]
            [clojure.java.io :as io]))

(defn- file-contains? [f s]
  (.contains (slurp f) s))

(deftest prepare-deploy-scripts-test
  (testing "that it puts the deploy-scripts from the classpath into a folder where we can execute them"
    (let [dir (prepare-deploy-scripts)
          template (io/file dir "app-server-template.json")
          deploy-new-app-server (io/file dir "deploy-new-app-server.rb")]
      (is (.exists template))
      (is (file-contains? template "AWSTemplateFormatVersion"))
      (is (.exists deploy-new-app-server))
      (is (file-contains? deploy-new-app-server "#!/usr/bin/ruby")))))