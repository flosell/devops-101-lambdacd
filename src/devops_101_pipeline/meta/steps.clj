(ns devops-101-pipeline.meta.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.support :as support]
            [lambdacd.steps.git :as git]))

(defn pipeline-working-directory []
  (System/getProperty "user.dir"))

(def devops-101-repo "git@github.com:flosell/devops-101-lambdacd.git") ;; change this if you want to make your own changes

(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx devops-101-repo "digitalocean"))

(defn use-digitalocean-branch [& _]
  {:revision "digitalocean" :status :success})

(defn manual-trigger [args ctx]
  (support/chain-steps args ctx
    [lambdacd.steps.manualtrigger/wait-for-manual-trigger
     use-digitalocean-branch]))

  (defn ^{:display-type :container} with-repo [& steps]
  (git/with-git devops-101-repo steps))

(defn build [args ctx]
  (shell/bash ctx (:cwd args)
              "set -x"
              "lein test"
              "lein uberjar"))

(defn deploy [args ctx]
  (shell/bash ctx (:cwd args)
              "bin/deploy-lambdacd.sh"))

(defn stop-to-restart [args ctx]
  (System/exit 0))