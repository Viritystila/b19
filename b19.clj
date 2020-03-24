(ns b19
  (:use [trigger.trigger]
        [trigger.synths]
        [trigger.algo]
        [trigger.speech]
        [trigger.samples]
        [trigger.trg_fx]
        [overtone.core])
  (:require  [overtone.osc :as osc]))

 (future
  (println "Begin loading SuperDirt samples")
  (load-all-SuperDirt-samples)
  (println "Samples loaded"))

;;Muista vb!!!!

(defn add-tts-sample [name path nosamples]

  (println "Begin loading sample " name)
  (let [txt (generate-markov-text path nosamples)
        _ (add-sample name (string-to-buffer txt))]
    (println "Sample" name "loaded")
    txt))

(def oc (osc/osc-client "localhost" 44100))


(set-pattern-duration (/ 1 (* 1 0.5625)))

(set-pattern-delay 1.0)
