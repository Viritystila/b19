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

;;;;;;;;
;;cutter
;;;;;;;
;(def fs "/mnt/Varasto/biisit/Viritystila/cutter/resources/b19.fs")

;(def vs "/mnt/Varasto/biisit/Viritystila/cutter/resources/b19.vs")

;(cutter.interface/start-cutter :fs fs :vs vs )


(cutter.interface/start-cutter :fs "./default.fs" :vs "./default.vs")


(cam "3" :iChannel1)
(set-cam "3" :fps 1)
(stop-cam "3")
(cut "/mnt/Varasto/biisit/Viritystila/videos/saaristomeri.mp4" :sm 3500)

(buf :sm :iChannel2)

(stop-buf :sm)

(vid "../videos/bbb4k.mp4" :iChannel2)
(cut "../videos/bbb4k.mp4" "bbb" 5000)
(buf "bbb" :iChannel2)
;(stop-vid "../videos/bbb4k.mp4")

(cut "../videos/linko.mp4" "linko" 0)
(buf :linko :iChannel3)
(stop-buf :linko)

(toggle-recording "/dev/video4")


(stop-cutter)

;;;;;;;;;;;;;
;;;Sync
;;;;;;;;;;;;

(trg :sync ping :in-trg [1 1 1])

(pause! :sync)

(play! :sync)

(set-pattern-duration (/ 1 (* 1 0.5625)))

(set-pattern-delay 0.2)


;;;;;;;;;;;;
;;End sync
;;;;;;;;;;;


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
            (evr 2 [r])
            (evr 1 fst)
            (evr 8 rev)
            (ins 3  [ [(rep ["n c2" r r "n e3"] 16)] ["n d2"]   [ "n d3" "n d4"] r])
            (evr 2 rpl 1 ["n e3"] )

            (ins 1  ["n e3" ["n c2"r "n e4"] "nc5" [r "ne4" "nd3"]])
            (ins 2  [r [ r "n e4"] "ne3" [r "ne2" "nd3"]])
            ;(evr 9  [[(rep "n c4" 4)]  (fll [ "n e5" "n d3"] 4) r ["n e4"]]);
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

  (volume! :tb303sn 2.6)

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

(cut "../videos/txttv1.mp4" "teksti" 3400)
(buf :teksti :iChannel3)
(stop-buf :teksti)

(cut "../videos/futu_luonto.mp4" "fl" 2000)
(buf :fl :iChannel2)
(stop-buf :fl)

(cut "../videos/opi_pelaten.mp4" "op" 1000)
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
       :in-trg (-> [(rep 2 4)]
                   (rep 16)
                                        ;(evr 3 asc [1 2] [1 [r r 2 3]])
                   ;(evr 8 [2 r [2 r 2 2] [2 2]])
                   ;(evr 1 map-in scl 10.2)
                   ;(evr 4 map-in scl 10.3)
                                        ;(evr 1 (fn [x] (println x) x) )
                   ;(rpl 15 fst)
                   ;(rpl 15 acc)
                   ;(evr 5 [r])
                   ;(#([x] (every-pred)))
                   )
       :in-note  (-> (fll ["n f3" "nc3" ] 6)
                     (evr 2 ["n d4" "ne2"])
                     (evr 3  ["n e3" r r  "ng2"])
                     (rep 16)
                     (evr 3 asc 3 ["n e3" r r  "ng2"])
                     (#(ins %  14 (fll ["nc4"  "nf3"] 3) 15 ["nc4" "nf4"] nil))
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
       :in-osc1-level [1]
       :in-osc2-level [0]; (fll [1 0] 128)
       )




  (volume! :mooger1 2)

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
         :in-trg  (-> [(range 2 8 1)]
                   (rep 2)
                                        ;(evr 3 asc [1 2] [1 [r r 2 3]])
                   ;(evr 8 [4 r [5 r 1 2] [3 5]])
                   (evr 1 map-in scl 0.025)
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
         :in-trg (-> [1]
                     (rep 8)
                     (evr 2 (fn [x] [(rep x 16)]))
                     (evr 1 [(rep 1 8)]))
         :in-note (-> ["n e1"]
                      (rep 4)
                      (evr 4 asc 1 ["n d2" "nf2" r r])
                      (evr 1 ["nc1"]))
         :in-velocity [1000]
         :in-gate-select (-> [1]
                             (rep 8)
                             (evr 8 [0])))

    (volume! :vb 1))

(play! :vb)

(stp :vb)

(def vbbus (audio-bus-monitor (get-out-bus :vb)))

@vbbus

(on-trigger (get-trigger-id :tick :in-trg)
            (fn [val]
              (let []
                ;(println val)
                 (set-flt :iFloat2 @vbbus)
                ))
            :vb)

(remove-event-handler :vb)



;;;;;;;;;;;;;;;;;;;;;;
;;End Vintage bas;;;;;
;;;;;;;;;;;;;;;;;;;;;;


(cut "../videos/kotitietokone.mp4" "kt" 4420)
(buf :kt :iChannel3)
(stop-buf "kt")


(stop-buf "tb1")

(cut "../videos/toimittajarokotus.mp4" "tr" 0)
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
         :in-trg (-> [(rep '("~" 1) 3)]
                     (rep 8)
                     ;(evr 1 asc 0 [r 1] 1 [r 1])
                     ;(rpl 4 asc 1 [(rep 1 8)] nil)
                     (evr 3 asc 5 [1 r 1 1] nil)
                     (evr 8 [(rep  1 16)])
                     ;(evr 8 acc)
                     )
         :in-f1 [500]
         :in-f2 [2000]
         :in-f3 [(range 50 80 5)]
         )

    (volume! :kick 1))


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

(do (trg :cs801 cs80lead)
    (pause! :cs801)
    (trg :cs801 cs80lead
         :in-trg (-> [(rep 1 4)]
                     (rep 8))

         :in-gate-select (-> [0 1]
                            (fll 1))
         :in-freq (-> ["f c5"]
                      (rep 4)
                      (rpl 3 ["f d4"])
                      (rpl 4 ["f e5" ])
                      )
         )

    (volume! :cs801 1))


(play! :cs801)


(pause! :cs801)



;;;;;;;;;;;;;;;;;;;;
;;;End cs80lead;;;;;
;;;;;;;;;;;;;;;;;;;;



(cut "../videos/kansanparantaja.mp4" "kp" 4050)
(buf :kp :iChannel3)

(fps-buf "kp" 25)
(stop-buf "kp")
