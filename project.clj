(defproject devops-101-pipeline "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :dependencies [[lambdacd "0.1.0-alpha11"]
                           [org.clojure/clojure "1.5.1"]
                           [org.clojure/tools.logging "0.3.0"]
                           [org.slf4j/slf4j-api "1.7.5"]
                           [ch.qos.logback/logback-core "1.0.13"]
                           [ch.qos.logback/logback-classic "1.0.13"]
                           [org.clojure/core.incubator "0.1.3"]
                           [ring-server "0.3.1"]
                           [org.reflections/reflections "0.9.9"]]
            :main devops-101-pipeline.core
            :profiles {:uberjar {:aot [devops-101-pipeline.core]}}
            :plugins [[lein-ring "0.8.13"]])
