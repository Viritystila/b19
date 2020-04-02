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
(cutter.interface/start-cutter :fs "./default.fs" :vs "./default.vs" )

(cam "3" :iChannel1)
(set-cam "3" :fps 1)
(stop-cam "3")
(cut "../videos/saaristomeri.mp4" :sm 3500)
(buf :sm :iChannel2)
(stop-buf :sm)

(cut "../videos/linko.mp4" :linko 0)
(buf :linko :iChannel3)
(stop-buf :linko)

(toggle-recording "/dev/video4")
;(stop-cutter)

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


(cut "../videos/txttv1.mp4" "teksti" 5000)
(buf :teksti :iChannel3)
(stop-buf :teksti)

(cut "../videos/futu_luonto.mp4" "fl" 2000)
(buf :fl :iChannel2)
(stop-buf :fl)

(cut "../videos/opi_pelaten.mp4" "op" 100)
(buf :op :iChannel2)


;;;;;;;;;;;;;;;;;;
;;;Start mooger1;;
;;;;;;;;;;;;;;;;;;

(defsynth mooger1
  "Choose 0, 1, or 2 for saw, sin, or pulse"
  [in-trg 0
   in-trg-val 0
   in-note 60
   in-note-val 60
   in-amp 1
   in-amp-val 1
   in-osc1 1
   in-osc1-val 1
   in-osc2 1
   in-osc2-val 1
   in-cutoff 500
   in-cutoff-val 500
   in-attack 0.0022
   in-attack-val 0.0022
   in-decay 0.95
   in-decay-val 0.95
   in-sustain 0.4
   in-sustain-val 0.4
   in-release 0.3
   in-release-val 0.3
   in-fattack 0.22
   in-fattack-val 0.22
   in-fdecay 0.9
   in-fdecay-val 0.9
   in-fsustain 0.999
   in-fsustain-val 0.999
   in-frelease 0.001
   in-frelease-val 0.001
   in-osc1-level 0.5
   in-osc1-level-val 0.5
   in-osc2-level 0.5
   in-osc2-level-val 0.5
   in-gate-select 0
   in-gate-select-val 0
   ctrl-out 0
   out-bus 0]
  (let [gate           (in:kr in-trg)
        gate-val       (in:kr in-trg-val)
        trg-gate       (trig gate gate-val)
        gate           (select:kr (in:kr in-gate-select-val)  [trg-gate gate])
        note           (in:kr in-note-val)
        amp            (in:kr in-amp-val)
        osc1           (in:kr in-osc1-val)
        osc2           (in:kr in-osc2-val)
        cutoff         (in:kr in-cutoff-val)
        attack         (in:kr in-attack-val)
        decay          (in:kr in-decay-val)
        sustain        (in:kr in-sustain-val)
        release        (in:kr in-release-val)
        fattack        (in:kr in-fattack-val)
        fdecay         (in:kr in-fdecay-val)
        fsustain       (in:kr in-fsustain-val)
        frelease       (in:kr in-frelease-val)
        osc1-level     (in:kr in-osc1-level-val)
        osc2-level     (in:kr in-osc2-level-val)
        freq           (midicps note)
        osc-bank-1     [(saw freq) (sin-osc freq) (pulse freq)]
        osc-bank-2     [(saw freq) (sin-osc freq) (pulse freq)]
        amp-env        (env-gen (adsr attack decay sustain release) :gate gate)
        f-env          (env-gen (adsr fattack fdecay fsustain frelease) :gate gate)
        s1             (* osc1-level (select osc1 osc-bank-1))
        s2             (* osc2-level (select osc2 osc-bank-2))
        filt           (moog-ff (+ s1 s2) (* cutoff f-env) 3)]
    (out out-bus (pan2 (* amp amp-env filt)))))


(do
  (trg :mooger1 mooger1)
  (pause! :mooger1)
  (trg :mooger1 mooger1
       :in-trg (-> [(rep 2 4)]
                   (rep 16)
                                        ;(evr 3 asc [1 2] [1 [r r 2 3]])
                   (evr 8 [2 r [2 r 2 2] [2 2]])
                   (evr 1 map-in scl 0.2)
                   (evr 4 map-in scl 0.3)
                                        ;(evr 1 (fn [x] (println x) x) )
                   (rpl 15 fst)
                   (rpl 15 acc)
                   )
       :in-note  (-> (fll ["n f3" "nc3" ] 6)
                     (evr 2 ["n d4" "ne2"])
                     (evr 3  ["n e3" r r  "ng2"])
                     (rep 16)
                     (evr 3 asc 3 ["n e3" r r  "ng2"])
                     (#(ins %  14 (fll ["nc4"  "nf3"] 3) 15 ["nc4" "nf4"] nil)) )
       :in-attack [0.01]
       :in-decay [0.41]
       :in-sustain [0.51]
       :in-release [0.3]
       :in-fattack [0.01]
       :in-fdecay  [0.21]
       :in-fsustain [2.31]
       :in-frelease [0.051]
       :in-gate-select [0]
       :in-osc1 [0]
       :in-osc2 [1]
       :in-osc1-level [0.2]
       :in-osc2-level  (fll [1 0] 128))




  (volume! :mooger1 3)

  )

(play! :mooger1)

(stp :mooger1)

;;;;;;;;;;;;;;;;;
;;End Mooger1;;;;
;;;;;;;;;;;;;;;;;



;;;;;;;;;;;;;;;;;;
;;;;Start hat2;;;;
;;;;;;;;;;;;;;;;;;



(do (trg :hat2 hat2)
    (pause! :hat2)

    (trg :hat2 hat2
         :in-trg  (-> [[2 r] [2 3] r [r 3 4 5]]
                   (rep 16)
                                        ;(evr 3 asc [1 2] [1 [r r 2 3]])
                   (evr 8 [4 r [5 r 1 2] [3 5]])
                   (evr 1 map-in scl 0.01)
                   (evr 4 map-in scl 0.02)
                                        ;(evr 1 (fn [x] (println x) x) )
                   (rpl 15 fst)
                   (rpl 15 acc)
                   (rpl 15 map-in scl 0.001 nil)
                   (evr 1 #(mapv (fn [x] [x x]) %))
                   )
         :in-attack ":in-trg")



    (volume! :hat2 1)

    )


(play! :hat2)

(stp :hat2)
;;;;;;;;;;;;;;;
;; end hat2;;;;
;;;;;;;;;;;;;;;
