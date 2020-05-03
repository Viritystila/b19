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

  (load-all-SuperDirt-samples)


;;Muista vb!!!!

(defn add-tts-sample [name path nosamples]
  (println "Begin loading sample " name)
  (let [txt (generate-markov-text path nosamples)
        _ (add-sample name (string-to-buffer txt))]
    (println "Sample" name "loaded")
    txt))


(sta)

 ;;;;;;
 ;;;futu-luonto, 4000 + sphere mesh
 ;;;;;;

;;;Biisi1
;;vb, kick cs801 ja cs802

;;;Biisi2
;; sf1, smp1, bow2

;;;;;;;;
;;cutter
;;;;;;;
(stop-cutter)

(cutter.interface/start-cutter :fs "./default.fs" :vs "./default.vs" :gs "./default.gs")

(cutter.interface/start-cutter)

(cutter.cutter/request-mesh "../cutter/resources/sphere.dae")

(cutter.cutter/remove-mesh "2")

(cam "3" :iChannel1)
(set-cam "3" :fps 5)
(stop-cam "3")
(cut "/mnt/Varasto/biisit/Viritystila/videos/saaristomeri.mp4" :sm :start-frame 3500)

(cut "/mnt/Varasto/biisit/Viritystila/videos/spede.mp4" "sp" :start-frame 51900)


(buf "sp" :iChannel2)


(buf :sm :iChannel2)

(stop-buf :sm)

(cut "../videos/linko.mp4" "linko")
(buf :linko :iChannel3)
(stop-buf :linko)

(toggle-recording "/dev/video4")


(stop-cutter)

;;;;;;;;;;;;;
;;;Sync
;;;;;;;;;;;;

(trg :sync ping :in-trg [1 1])

(pause! :sync)

(play! :sync)

(set-pattern-duration (/ 1 (* 1 0.5625)))

(set-pattern-duration (/ 1 (* 1 1)))


(set-pattern-delay 0.2)


;;;;;;;;;;;;
;;End sync
q;;;;;;;;;;;


;;;;;;;;;;
;;Tick;;;;
;;;;;;;;;;


(do
  (trg :tick ping)
  (pause! :tick)
  (trg :tick ping :in-amp [0] :in-trg [(rep 1 10)])
  (play! :tick))

(stp :tick)


;;;;;;;;;;;;
;;End Tick;;
;;;;;;;;;;;;


;;;;;;;;
;;AALKAA
;;;;;;;;


;;;;;;;;;;;;
;;Markorona
;;;;;;;;;;;;

(do
  (def path "generalx2paradisedaqx2.txt")
  (def nosamples 20)
  (def mtxt (generate-markov-text path nosamples)))


(def split_mtxt (split_text mtxt))

(def split_mb (sentence_to_buffer split_mtxt ))

(def t_txt (parse_buffer_name split_mtxt))

(get-sample-id :choose)

t_txt

(do
  (trg :markorona smp)

  (pause! :markorona)

  (trg :markorona smp
       :in-trg
       (->   (seq t_txt)
             ;(rep 4)
             (evr 4 fll 8 )
             (evr 3 [r])
             ;;(#(evr % 2 (first %)))

             )
       :in-loop
       (-> (rep [0] 8)
            (evr 5  [1])
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

  (volume! :markorona 1.)

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
                    tx      (kval split_mb)
                    ]
                ;(println tx)
                                        ;(println (kval d_txt_inv) )
                (cutter.interface/write  tx  30  320 5 0.9 0.2 0.944 10 10 1)
                ))
            :markorona)

 (cutter.interface/write  "Sivulle"  30  320 5 0.9 0.2 0.944 10 10 1)

 (cutter.interface/write  "Suoraksi"  30  320 5 0.9 0.2 0.944 10 10 1)

 (cutter.interface/write  "Suoraan"  30  320 5 0.9 0.2 0.944 10 10 1)

 (cutter.interface/write  "Tempossa" 30  320 5 0.9 0.2 0.944 10 10 1)


(remove-event-handler :markorona)

;;;;;;;;;;;;;;;;
;;End Markorona
;;;;;;;;;;;;;;;;

;;;;;;;;;;;;
;;;tb303sn
;;;;;;;;;;;


(println (mapv find-note-name (chord :d2 :7sus2)) )

(def d2_7sus2    [(chord :d2 :7sus2)] )

d2_7sus2

(do
  (trg :tb303sn tb303)

  (pause! :tb303sn)

  (trg :tb303sn
       tb303
       :in-trg
       (->  [["n e2"]  [ "n d1" "n d3"] r r]
            (rep 8)
            (evr 3 asc 0  [r r  "n c4" r] nil)
            ;(evr 2 [r])
            ;(evr 1 fst)
            ;(evr 8 rev)
            ;(ins 3  [ [(rep ["n c2" r r "n e3"] 16)] ["n d2"]   [ "n d3" "n d4"] r])
            ;(evr 2 rpl 1 ["n e3"] )

            ;(ins 1  ["n e3" ["n c2"r "n e4"] "nc5" [r "ne4" "nd3"]])
            ;(ins 2  [r [ r "n e4"] "ne3" [r "ne2" "nd3"]])
                                        ;(evr 9  [[(rep "n c4" 4)]  (fll [ "n e5" "n d3"] 4) r ["n e4"]]);
            (evr 3 fst)
            ;(rpl 8 acc)
             )
       :in-amp [1]
       :in-note  ":in-trg"
       :in-gate-select (-> [1]
                           (rep 16)
                           ;(evr 4 [0])
                           )
       :in-attack [0.0001]
       :in-decay [0.3919]
       :in-sustain [0.5]
       :in-release (-> [0.21273]
                       (rep 8)
                       (evr 2  mpa  (fn [x] (* 2 x)) nil)
                       )
       :in-r [0.9]
       :in-cutoff (-> [500 5000]
                      (#(range (first  %) (last %) 10))
                      (#(conj (reverse %) %))
                      (#(flatten %))
                      (#(partition 8 % ))
                      (#(map vec %))
                      ;(evr 2 (fst [(range 500 5000 100)] 16))
                      (evr 1 fst 8)
                      )
       :in-wave
       (fll [0 1] 32)
       )

  (volume! :tb303sn 1.06)

  (trg! :tb303sn :tb3030sne trg-fx-chorus
        :in-rate [0.1]
        :in-depth [2])

  )

(play! :tb303sn)

(fade-out! :tb303sn)

(stp :tb3030sne)

(stp :tb303sn)

(lss)


(on-trigger (get-trigger-val-id :tb303sn :in-trg)
            (fn [val]
              (let [ival    (int val)
                    ]
                ;(println val)
                 (set-flt :iFloat1 val)
                ))
            :tb303sn)

(remove-event-handler :tb303sn)



;;;;;;;;;;;;;;;;
;; End tb303sn
;;;;;;;;;;;;;;;;

(* (* 2 60) 24)

(cut "../videos/txttv1.mp4" "teksti" :start-frame 3400)
(buf :teksti :iChannel3)
(stop-buf :teksti)

(cut "../videos/futu_luonto.mp4" "fl" :start-frame 2000)
(buf :fl :iChannel2)
(stop-buf :fl)

(cut "../videos/opi_pelaten.mp4" "op" :start-frame 1000)
(buf :op :iChannel2)
(stop-buf :op)

(cutter.interface/write  "Viritystila"  30  320 5 0.9 0.2 0.944 10 10 1)

;;;;;;;;;;;;;;;;;;
;;;Start mooger1;;
;;;;;;;;;;;;;;;;;;


(do
  (trg :mooger1 mooger)
  (pause! :mooger1)
  (trg :mooger1 mooger
       :in-trg (-> [(rep 2 16)]
                   (rep 16)
                   ;                     (evr 3 asc [1 2] [1 [r r 2 3]])
                   (evr 4 (fst [2 r [2 r 2 2] [2 2]]))
                   ;(evr 1 map-in scl 10.2)
                   ;(evr 4 map-in scl 10.3)
                                        ;(evr 1 (fn [x] (println x) x) )
                   ;(rpl 15 fst)
                   ;(rpl 15 acc)
                   (evr 5 acc)
                   ;(#([x] (every-pred)))
                   )
       :in-note  (-> (fll ["n f3" "nc3" ] 6)
                     (evr 2 ["n d5" "ne3"])
                     (evr 3  ["n e4" r r  "ng4"])
                     ;(rep 16)
                     ;(evr 3 asc 3 ["n e3" r r  "ng2"])
                     ;(#(ins %  14 (fll ["nc4"  "nf3"] 3) 15 ["nc4" "nf4"] nil))
                     )
       :in-attack [0.001]
       :in-decay [0.41]
       :in-sustain [0.251]
       :in-release [0.123]
       :in-fattack [0.0021]
       :in-fdecay  [0.21]
       :in-fsustain [0.31]
       :in-frelease [0.21]
       :in-gate-select [1]
       :in-osc1 [2]
       :in-osc2 [2]
       :in-osc1-level [0]
       :in-osc2-level [1]; (fll [1 0] 128)
       )




  (volume! :mooger1 1.5)

  )

(play! :mooger1)

(stp :mooger1)




(on-trigger (get-trigger-val-id :mooger1 :in-note)
            (fn [val]
              (let [ival    (midi->hz val)
                    ]
                ;(println val)
                 (set-flt :iFloat1 ival)
                ))
            :mooger1)

(remove-event-handler :mooger1)

;;;;;;;;;;;;;;;;;
;;End Mooger1;;;;
;;;;;;;;;;;;;;;;;



;;;;;;;;;;;;;;;;;;
;;;;Start hat2;;;;
;;;;;;;;;;;;;;;;;;



(do (trg :hat2 hat2)
    (pause! :hat2)

    (trg :hat2 hat2
         :in-trg  (-> [(range 2 8 0.5)]
                   (rep 2)
                                        ;(evr 3 asc [1 2] [1 [r r 2 3]])
                   ;(evr 8 [4 r [5 r 1 2] [3 5]])
                   ;(evr 1 map-in scl 0.025)
                   (evr 2 map-in scl 0.05)
                                        ;(evr 1 (fn [x] (println x) x) )
                   ;(rpl 15 fst)
                   ;(rpl 15 acc)
                   ;(rpl 15 map-in scl 0.001 nil)
                   (evr 2 #(mapv (fn [x] [(rep x 8)]) %))
                   (concat  [(rep [r] 2)])
                   )
         :in-attack ":in-trg")

    (volume! :hat2 1)

    )

(play! :hat2)

(stp :hat2)
;;;;;;;;;;;;;;;
;; end hat2;;;;
;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;
;;;Start Vintage bass;;
;;;;;;;;;;;;;;;;;;;;;;;


(do (trg :vb vintage-bass)
    (pause! :vb)
    (trg :vb vintage-bass
         :in-trg (-> '([1 "~" [1 1] 1]
                       [["~" 1] "~" "~" 1 ]
                       [["~" 1] "~" 1 1 ]
                       [["~" [1 1]] "~" "~" "~" ])
                     (rep 16)
                     (evr 6 fst)
                     (evr 16 asc 1 [1 r])
                     )
         :in-note (-> (seq [["n eb1" "n g2" [r r  "neb2" "nc2"] "nbb2"]
                            ["n eb1" r ["nbb2" "nb2"] "neb2"]])
                      (rep 8)
                      (evr 4 asc 1 ["n c3" "ng4" r r])
                      (evr 8 asc 1 ["n bb3" "ng4" "neb2" r])
                      ;(evr 6 fst)
                      ;(evr 1 ["nc1"])
                      )
         :in-velocity  (map vec (partition 1 (range 1000 3000 100)))
         :in-gate-select (-> [1]
                             (rep 8)
                             ;(evr 8 [0 1 1 0])
                             ))

    (volume! :vb 0.125)

    )

(play! :vb)

(stp :vb)

(def vbbus (audio-bus-monitor (get-out-bus :vb)))

@vbbus

(on-trigger (get-trigger-id :tick :in-trg)
            (fn [val]
              (let []
               ; (println val)
                 (set-flt :iFloat2 @vbbus)
                ))
            :vb)

(remove-event-handler :vb)



;;;;;;;;;;;;;;;;;;;;;;
;;End Vintage bas;;;;;
;;;;;;;;;;;;;;;;;;;;;;


(cut "../videos/kotitietokone.mp4" "kt" :start-frame 4420)
(buf :kt :iChannel3)
(stop-buf "kt")


(stop-buf "tb1")

(cut "../videos/toimittajarokotus.mp4" "tr" )
(buf :tr :iChannel2)

(stop-buf "tr")

(fps-buf :tr 25)

(cam "0" :iChannel1)

(stop-cam "0")

(cutter.interface/write "Viritystila"   30  320 5 0.9 0.2 0.944 10 10 0)


;;;;;;;;;;;;;;;;;
;;;Start kick;;;;
;;;;;;;;;;;;;;;;;



(do (trg :kick kick)
    (pause! :kick)

    (trg :kick kick
         :in-trg (-> [(rep '("~" 1) 4)]
                     (rep 16)
                     ;(evr 1 asc 0 [r 1] 1 [r 1])
                     (evr 8 asc 1 [(rep 1 8)] nil)
                     (evr 3 asc 5 [1 r 1 1] nil)
                     (evr 16 [[1] [(rep  1 4)] [1 [r r 1 1]] [1 r r 1]])
                     ;(evr 8 acc)
                     )
         :in-f1 [500]
         :in-f2 [3000]
         :in-f3 [(range 50 80 5)]
         )

    (volume! :kick 0.01)

    )


(play! :kick)


(stp :kick)

(on-trigger (get-trigger-id :kick :in-trg)
            (fn [val]
              (let [ival   (int (* 1900 val))
                    ]
                                       ;(println val)
                (i-buf :tr ival)
                                        ;(set-flt :iFloat2 @vbbus)

                ;; (cutter.interface/write
                ;;  (str "Corona " (* (rand-int 10) ival))
                ;;  (* (rand 15) 20)  (* (rand 2) ival) 5 0.9 0.2 0.944 10 10 true)

                ))
            :kick)

(remove-event-handler :kick)

(rand 10)

;;;;;;;;;;;;;;;;;
;;;;End kick;;;;;
;;;;;;;;;;;;;;;;;




;;;;;;;;;;;;;;;;;;;;
;;;Start cs80lead1;;
;;;;;;;;;;;;;;;;;;;;

(println (mapv find-note-name (chord :c3 :minor7)) )


;;keskivaihe loppu
(do (def c1  (-> ["f c2"]
                      (rep 4)
                      (rpl 3 ["f g2"])
                      (rpl 4 ["f eb2" ])
                      (evr 2 rep 4)
                      ))
    (def c2   (-> ["f bb1"]
                      (rep 8)
                      (rpl 3 ["f c2"])
                      (rpl 4 ["f g3" ])
                      (rpl 1 ["f eb2"])
                      (rpl 0 ["f bb2"])
                      (rpl 7 ["f g3"])
                      (evr 2 rep 4)
                      (evr 2 asc 1 "f bb3")
                      (evr 3 asc 1 "f eb2")
                      )))


(do (def c1  (-> ["f g2"]
                      (rep 8)
                      ))
    (def c2   (-> ["f bb1"]
                      (rep 8)
                      (rpl 3 ["f c3"])
                      (rpl 4 ["f c2" ])
                      (evr 2 rep 4)
                      (evr 2 asc 1 "f bb3")
                      (evr 4 asc 1 "f eb4")
                      )))



(do (def c1  (->  '(["f g2"]
                    ["f c4"]
                    ["f eb4"])
                      (rep 1)
                      ))
    (def c2   (-> ["f bb1"]
                      (rep 16)
                      (rpl 3 ["f c3"])
                      (rpl 4 ["f c2" ])
                      (evr 2 rep 4)
                      (evr 2 asc 1 "f bb3")
                      (evr 4 asc 1 "f eb4")
                      (evr 8 asc 1 "f c3")
                      (evr 9 asc 1 "f eb5")
                      (evr 10 asc 1 "f eb5")
                      (evr 11 asc 1 "f c4")
                      )))




(do (trg :cs801 cs80lead)
    (trg :cs802 cs80lead)
    (pause! :cs801)
    (pause! :cs802)

    (do
      (trg :cs801 cs80lead
           :in-trg (-> [(rep 1 8)]
                       (rep 2)
                       (evr 2 [(rep 1 16)])
                       )

           :in-gate-select (-> [1]
                               (fll 16))
           :in-freq c1
           )

      (trg :cs802 cs80lead
           :in-trg (-> [(rep 1 32)]
                       (rep 2)
                       ;(evr 2 [r])
                       )

           :in-gate-select (-> [0]
                               (fll 1))
           :in-freq c2
           ))

    (volume! :cs801 0.25)
    (volume! :cs802 0.25)
    )


(do
  (play! :cs801)
  (play! :cs802))

(pause! :cs801)
(pause! :cs802)


(def csbus (audio-bus-monitor (get-out-bus :cs801)))

@csbus


(on-trigger (get-trigger-id :tick :in-trg)
            (fn [val]
              (let []
                ;(println val)
                 (set-flt :iFloat2 @csbus)
                ))
            :cs801)

(remove-event-handler :cs801)


(stp :cs801)
(stp :cs802)

;;;;;;;;;;;;;;;;;;;;
;;;End cs80lead;;;;;
;;;;;;;;;;;;;;;;;;;;

(stop-buf "tr")

(cut "../videos/kansanparantaja.mp4" "kp" :start-frame 4050)
(buf :kp :iChannel3)

(fps-buf "kp" 10)
(stop-buf "kp")

(remove-mesh "2")

(cut "../videos/eclipse2.mp4" "ec2" :start-frame 200)
(buf "ec2" :iChannel2)

(stop-buf "ec2")
;;;;;;;;;;;;;;;
;;;Start png1;;
;;;;;;;;;;;;;;;

(do (trg :png1 ping)
    (pause! :png1)
    (trg :png1 ping
         :in-trg [(rep 1 8)]
         :in-attack [0.125]
         :in-note (->  ["n eb4"]
                       (rep 8)
                       (piv)
                       (asc 8 (rep ["n bb3"] 8))
                       (flatten)
                       (vec)
                       (#(map vec (partition 1 %))))

         )

    (volume! :png1 0.25)

    )


(play! :png1)

(pause! :png1)

(on-trigger (get-trigger-val-id :png1 :in-note)
            (fn [val]
              (let [ival    (midi->hz val)
                    ]
                ;(println ival)
                 (set-flt :iFloat1 ival)
                ))
            :png1)

(remove-event-handler :png1)


;;;;;;;;;;;;;;;;
;;;Stop png1;;;;
;;;;;;;;;;;;;;;;



;;;;;;;;;;;;;;;;
;;;Start tom1;;;
;;;;;;;;;;;;;;;;

(do (trg :tom1 tom)
    (pause! :tom1)


    (trg :tom1 tom
         :in-trg (-> [(rep 1 4)]
                     (rep 24)
                     (evr 3 asc 0 [ 1 [1 1]])
                     (evr 6 asc 1 [1 r [(rep 1 4)] r])
                     (evr 12 fst)
                     (evr 24 acc))
         :in-freq ["f g2"]
         :in-attack [0.25])

    )

(play! :tom1)


(stp :tom1)



;;;;;;;;;;;;;;;
;;;Stop tom1;;;
;;;;;;;;;;;;;;;



(cut "../videos/futu_luonto.mp4" "fl2" :start-frame 0)
(buf :fl2 :iChannel2)
(stop-buf :fl2)

(fps-buf :fl2 10)

(cut "../videos/soviet1.mp4" "sv1" :start-frame 17400)
(buf :sv1 :iChannel3)

(stop-buf :sv1)

(fps-buf :sv1 10)

(set-flt :iFloat1 0.03)
(set-flt :iFloat2 0.04)
;;;;;;;;;;;;;;;;;
;;Start sf1;;;;;;
;;;;;;;;;;;;;;;;;

(do
  (trg :sf1 simple-flute)
  (pause! :sf1)

  (trg :sf1 simple-flute
       :in-trg (-> [1 1 1 1]
                   (rep 32)
                   (evr 4 (rep ["~"] 4)))
       (-> [1 1 1 [1 1]]
           (rep 32)
           (evr 4 (rep ["~"] 2)))
       (-> [(rep 8 1)]
           (rep 32)
           (evr 4 (rep ["~"] 2))
           (evr 3 acc))
       :in-freq (-> ["f bb2"]
                    (rep 32)
                    (evr 2 ["f eb1"])
                    (rpl 0 ["f c1" "fg3"])
                    (evr 6 asc 0 ["f bb2" "fbb1"]))
        (-> ["f eb2"]
                    (rep 32)
                    (evr 2 ["f bb1" "feb3"])
                    (rpl 0 ["f g2" "fc2"])
                    (evr 6 asc 0 ["f bb2" "fbb1"]))
        (-> ["f eb2"]
            (rep 32)
            (evr 2 ["f bb2"])
            (rpl 0 ["fc2"])
            (evr 2 asc 0 ["f bb3"]))
       :in-gate-select [1])

  (trg! :sf1 :sf1c trg-fx-chorus
        :in-rate (-> [0.741]
                     (rep 32)
                     (evr 7 [14]))
         (-> [0.8419]
                     (rep 32)
                     (evr 7 [2]))
        )
  (volume! :sf1 11)
  )


(play! :sf1)

(stp :sf1c)

;;;;;;;;;;;;;;;;;
;;End sf1;;;;;;;;
;;;;;;;;;;;;;;;;;

(do
  (trg :smp1 smp)
  (pause! :smp1)
  (trg :smp1 smp
       :in-trg
       (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 1 fst)
           )
       (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 1 fst)
           (evr 6 asc 0 [r "b bd1"])
           (evr 6 asc 1 ["b sn1" r])
           (evr 12 asc 0 fst)
           )
       (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 4 asc 1 (fn [x] [x "b ho0"]))
           (evr 1 fst)
           (evr 6 asc 1 (fn [x] [(fll ["b bd0" "b bass1"] 8)]))
           (evr 9 asc 1 (rep "b bass1" 1))
           (evr 11 asc 3 (fll ["bhh0" "bho1"] 8))
           (evr 4 asc 0 (fn [x] [r r r x ]))
           (evr 3 asc 1 (fn [x] [x r "bsn1" x ]))
           )
       (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 1 fst)
           (evr 1 asc 0 [r "b bd1"] )
           (evr 3 asc 1 [(rep "b hh0" 2)] )
           (evr 4 asc 1 (fn [x] [x "b sn1"]))
           (evr 13 asc 2 (rep ["b bass15"] 32))
           (evr 8 slw)
           (evr 4 asc 0 (fn [x] [r r r x ]))
           (evr 3 asc 1 (fn [x] [x r "bsn1" x ]))
           )
        (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 1 asc 0 [r "b bd1"] )
           (evr 4 asc 0 ["b bass15"])
           (evr 3 asc 0 (fn [x] [(fst x 8)]))
           (evr 3 asc 1 [(rep "b hh0" 2)] )
           (evr 1 fst)
           (evr 3 slw)
           (evr 4 asc 0 (fn [x] [r r r x ]))
           (evr 3 asc 1 (fn [x] [x "b bd0" "bsn1" x ]))
           )
  (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 1 asc 0 [r "b bd1"] )
           (evr 4 asc 0 [(rep  "b bass15" 4)])
           (evr 3 asc 0 (fn [x] [(fst x 8)]))
           (evr 3 asc 1 [(rep "b hh2" 2)] )
           (evr 1 fst)
           (evr 3 slw)
           (evr 4 asc 0 (fn [x] [r r r x ]))
           (evr 2 asc 1 (fn [x] (fll [x "b bd0" "bsn1" x ] 6)))
           )
  (-> ["b bd1" "b hh0"]
           (rep 32)
           (evr 1 asc 0 [r "b bd1"] )
           (evr 4 asc 0 [(rep  "b bass15" 4)])
           (evr 3 asc 0 (fn [x] [(fst x 8)]))
           (evr 3 asc 1 [(rep "b hh2" 2)] )
           (evr 1 fst)
           (evr 4 asc 0 (fn [x] [r r "bbass15" x ]))
           (evr 2 asc 1 (fn [x] (fll [x "b bd0" "bsn1" "bsn0" ] 6)))
           (evr 1 fst)
           (evr 1 acc)
           (evr 1 slw)
           )
  (->  ["b bd1" "b hh0"]
       (rep 32)
       (evr 2 fst)
       (evr 5 fst 4)
       (evr 7 fst 8)
       (evr 9 acc)
       (evr 17 fst 12)
       (evr 19 fst 16)
       (evr 20 fst 32)
       (evr 27 fst 64)

       (evr 29 fst 128)
       (evr 30 fst 128)
       (evr 31 fst 128)
       (evr 32 fst 128)
       (evr 3 sfl))
       :in-buf ":in-trg")

  (trg! :smp1 :smp1e trg-fx-feedback-distortion
        :in-noise-rate [0.04]
        :in-out-select (-> [1]
                           (rep 128)
                           (evr 64 [0])))

    (volume! :smp1 0.25)
  )


(play! :smp1)



(on-trigger (get-trigger-id :smp1 :in-trg)
            (fn [val]
              (let [ival   (int (* 2000 val))
                    ]
                     ;;                  (println val)
                (i-buf :sv1 ival)

                ))
            :smp1)

(remove-event-handler :smp1)


;;;;;;;;;;;;;;
;;;Stop smp1;;
;;;;;;;;;;;;;;


(cut "../videos/pakkanen.mp4" "pak1" :start-frame 1030)
(buf "pak1" :iChannel2)


(cut "../videos/tserno4.mp4" "ts4" :start-frame 1625 :length 100)
(buf "ts4" :iChannel3)

(stop-buf "ts4")


;;;;;;;;;;;;;;;
;;;Start bow1;;
;;;;;;;;;;;;;;;

(do
  (trg :bow1 bowed)
  (pause! :bow1)

  (trg :bow1 bowed
       :in-trg (->  [r]
                    (rep 128)
                    (evr 4 (rep  [(rep 0.001 4)] 2))
                    (evr 16 [0.1])
                    (evr 32  (rep  [(rep 0.001 4)] 2)))
       (->  [0.01 0.01]
                    (rep 128)
                    (evr 4 (rep  [(rep 0.001 32)] 4)) )
       :in-note (-> '(["n bb1"] ["n eb2"] ["n g2"])
                    (rep 32)
                    (evr 8 fll 16)
                    )
       (-> '(["n bb3"] ["n eb2"] ["n bb1"])
           (rep 16)
           (evr 8 fll 8)
           )
       (-> '(["n bb3"] ["n c2"])
           (rep 16)
           (evr 2 fll 8))
       (-> '(["n eb4"] ["n bb3"])
           (rep 16)
           (evr 2 fll 128)
           )
       :in-gate-select [1]
       :in-bow-slope  [0.2]
       :in-bow-offset [0.01]
       :in-vib-freq  ["f eb3"]
       :in-attack [0.01]
       :in-decay  [0.05]
       :in-sustain [5.1]
       :in-release [2.2])


  (volume! :bow1 0.25))

(play! :bow1)




(def bow1bus (audio-bus-monitor (get-out-bus :bow1)))

@bow1bus


(on-trigger (get-trigger-id :tick :in-trg)
            (fn [val]
              (let []
                ;(println val)
                (set-flt :iFloat2 @bow1bus)
                (set-flt :iFloat1 @bow1bus)

                (cutter.interface/write  "Sivulle"  (* (rand 10) 160)
                                         (* @bow1bus 3520) 5 0.9 0.2 0.944 10 10 true)

                ))
            :bow1)

(remove-event-handler :bow1)

;;;;;;;;;;;;;
;;;End bow1;;
;;;;;;;;;;;;;


  (cutter.interface/write  "Viritystila"  30
                           320 5 0.9 0.2 0.944 10 10 true)

(sta)
