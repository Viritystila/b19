(defproject b18 "0.1.0-SNAPSHOT"
    :description "B19"
    :url "https://github.com/Viritystila/b19"
    :license {:name "The Unlicense"
            :url "http://unlicense.org"}
    :dependencies [[org.clojure/clojure "1.10.1"]
                    [trigger "0.1.0-SNAPSHOT"]
                    [org.viritystila/cutter "0.0.1-SNAPSHOT"]
                    [rm-hull/markov-chains "0.1.1"]]
     :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)]
     :jvm-opts ^:replace [])
