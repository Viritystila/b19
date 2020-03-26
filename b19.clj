(ns b19
  (:use [trigger.trigger]
        [trigger.synths]
        [trigger.algo]
        [trigger.speech]
        [trigger.samples]
        [trigger.trg_fx]
        [cutter.cutter]
        [cutter.video]
        [cutter.camera]
        [cutter.texturearray]
        [cutter.interface]
        [cutter.cutter_helper]
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

;;;;;;;;
;;cutter
;;;;;;;
(cutter.cutter/start :fs "./default.fs" :vs "./default.vs" )

(cam "3" :iChannel1)
(set-cam "3" :fps 1)
(stop-cam "3")
(cut "../videos/saaristomeri.mp4" :sm 3500)
(buf :sm :iChannel2)
(stop-buf :sm)

(toggle-recording "/dev/video4")
;(stop-cutter)

;;;;;;;;;;;;;
;;;Sync
;;;;;;;;;;;;

(trg :sync ping :in-trg [1])

(pause! :sync)

(play! :sync)

(set-pattern-duration (/ 1 (* 1 0.5625)))

(set-pattern-delay 1.01)


;;;;;;;;;;;;
;;End sync
;;;;;;;;;;;



;;;;;;;;
;;AALKAA
;;;;;;;;


;;;;;;;;;;;;
;;Markorona
;;;;;;;;;;;;

(do
  (def path "generalx2paradisedaqx2.txt")
  (def nosamples 20)
  (def txt (generate-markov-text path nosamples))
  ;(println txt)
  (def s_txt (clojure.string/split txt #" "))
  ;(println s_txt)
  (def s_txt (mapv (fn [x] (apply str (filter #(Character/isLetter %) x)) ) s_txt ))
  ;s_txt
  (def s_txt (remove (fn [x] (= (count x) 0) ) s_txt ))
  ;(println  (map count s_txt))
  (def b_txt (map (fn [x] (string-to-buffer x)) s_txt))
  b_txt

  (def d_txt (into (sorted-map) (mapv
                       (fn [x] (let [bf     (string-to-buffer x)
                                    bfstr  (str x)
                                    bfkw   (keyword bfstr)
                                    id     (:id bf)
                                    sid    (str id)
                                    kid    (keyword sid)]
                                (add-sample bfstr bf)
                                [kid bfstr])) s_txt)))
  (def t_txt (map vec (partition 4  (map (fn [x] (str "b " x))  s_txt)))))

(do
  (trg :markorona smp)

  (pause! :markorona)

  (trg :markorona smp
       :in-trg
       (->   t_txt
             )
       :in-loop
       (-> (rep [0] 8)

            (evr 5  [0])

            ;(evr 1 [1])
            )
       :in-buf ":in-trg"
       :in-amp [0.75]
       :in-start-pos
       (-> (rep [0] 8)
           (evr 5 [(range 0 1600 10)])
            )
       :in-step
       (->  (rep [2] 8)
            (evr 5 (fst  [(range -2.5 2.5 0.25) 2 2 1] 4))
            ;(evr 1 [-2])
            )
       )

  (volume! :markorona 2.75)

  (trg! :markorona :markoronae trg-fx-echo
        :in-amp ;(evr 6 [1] (rep 32 [0]))
        (-> (rep [0] 6)
             (evr 6 [1]))
        :in-decay-time [0.51]
        :in-delay-time [0.1])

  )

(play! :markorona)

(stp :markorona)

(on-trigger (get-trigger-val-id :markorona :in-trg)
            (fn [val]
              (let [ival    (int val)
                    sval    (str ival)
                    kval    (keyword sval)
                    tx      (kval d_txt)
                    ]
                ;(println val)
                                        ;(println (kval d_txt_inv) )
                (cutter.interface/write  tx  30  520 10 0.9 0.2 0.944 10 10 1)
                ;(osc/osc-send oc3 "/cutter/write"  tx  30  520 10 0.9 0.2 0.944 10 10 1)
                ))
            :markorona)

(remove-event-handler :markorona)

;;;;;;;;;;;;;;;;
;;End Markorona
;;;;;;;;;;;;;;;;

;;;;;;;;;;;;
;;;tb303sn
;;;;;;;;;;;
(do
  (trg :tb303sn tb303)

  (pause! :tb303sn)

  (trg :tb303sn
       tb303
       :in-trg
       (->  (fst ["n e2" r r ["n d3" "n d4"]])
             (rep 16)
             (evr 2  (fst ["n e2" ["n d3"  "n c2"] r r]))
             (rpl 8  (fst ["n e5" ["n d3"  "n c4"] r r]) )
             ;;(rpl 3  (fst [(rep "n e3" 2)  (rep "n c#3" 2)  (rep "n b2" 2)  (rep "n b1" 2)]))
             ;(evr 1 fst)
             ;(evr 4 rev)
             ;(evr 1 acc)
             (evr 3  [["n e3" "n a2" r r] [r "n d3" "n e3" "n d3"]])
             ;(evr 4  ["n d3" ["nd3" "nb2" "ne3" "na2"]])
             ;(evr 5  ["n a3" r r ["ne3" "na2" "nd3" "nc2"]])
             ;(evr 1 slw)
             (evr 8 slw)
             (evr 4 rev)
             ;(evr 7 fst)
             ;(evr 1 ["n c2"])
             )
       :in-amp [1]
       :in-note  ":in-trg"
       :in-gate-select (-> [1]
                           (rep 16)
                           (evr 4 [0]))
       :in-attack [0.0001]
       :in-decay [0.3919]
       :in-sustain [0.5]
       :in-release [0.1273]
       :in-r [0.9]
       :in-cutoff [600]
       :in-wave
       (rep [0] 4)
       )


  (volume! :tb303sn 1)

  )

(play! :tb303sn)

(fade-out! :tb303sn)


;;;;;;;;;;;;;;;;
;; End tb303sn
;;;;;;;;;;;;;;;;
