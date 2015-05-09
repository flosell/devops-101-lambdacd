(ns devops-101-pipeline.meta.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.support :as support]
            [lambdacd.steps.manualtrigger :as manualtrigger]
            [lambdacd.steps.git :as git]))

(def devops-101-repo "https://github.com/flosell/lambdacd.git") ;; change this if you want to make your own changes
(def devops-101-branch "digitalocean")
(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx devops-101-repo devops-101-branch))

(defn use-digitalocean-branch [& _]
  {:revision devops-101-branch :status :success})

(defn manual-trigger [args ctx]
  (support/chain-steps args ctx
    [manualtrigger/wait-for-manual-trigger
     use-digitalocean-branch]))

  (defn ^{:display-type :container} with-repo [& steps]
  (git/with-git devops-101-repo steps))

(defn build [args ctx]
  (shell/bash ctx (:cwd args)
              "lein uberjar"))

(defn deploy [args ctx]
  (shell/bash ctx (:cwd args)
              "bin/deploy-lambdacd.sh"))
