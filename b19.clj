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
(cutter.cutter/start :fs "./default.fs" :vs "./default.vs" )

(cam "3" :iChannel1)
(set-cam "3" :fps 1)
(stop-cam "3")
(cut "../videos/saaristomeri.mp4" :sm 3500)
(buf :sm :iChannel2)

(cut "../videos/linko.mp4" :linko 0)

(buf :linko :iChannel3)

(stop-buf :sm)

(toggle-recording "/dev/video5")
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

;; (defn split_text [text]
;;   (let [ s_txt  (clojure.string/split text #" ")
;;          s_txt  (mapv (fn [x] (apply str (filter #(Character/isLetter %) x)) ) s_txt )
;;         s_txt  (remove (fn [x] (= (count x) 0) ) s_txt )]
;;     (into [] s_txt)))

;; (defn sentence_to_buffer
;;   ([text]
;;    (let [b_txt  (map (fn [x] (string-to-buffer x)) text)
;;          d_txt  (into (sorted-map) (mapv
;;                                     (fn [x] (let [bf     (string-to-buffer x)
;;                                                  bfstr  (str x)
;;                                                  bfkw   (keyword bfstr)
;;                                                  id     (:id bf)
;;                                                  sid    (str id)
;;                                                  kid    (keyword sid)]
;;                                              (add-sample bfstr bf)
;;                                              [kid bfstr])) text))]
;;      d_txt)))


;; (defn parse_buffer_name [txt]
;;    (mapv vec (partition 4  (map (fn [x] (str "b " x))  txt))))

(do
  (def path "generalx2paradisedaqx2.txt")
  (def nosamples 20)
  (def mtxt (generate-markov-text path nosamples)))


(def split_mtxt (split_text mtxt))

split

split_mtxt

(def split_mb (sentence_to_buffer split_mtxt ))

split_mb

(def t_txt (parse_buffer_name split_mtxt))

(get-sample (keyword  "kakka"))

(get-sample-id :choose)

t_txt

(do
  (trg :markorona smp)

  (pause! :markorona)

  (trg :markorona smp
       :in-trg
       (->   t_txt
             (rep 4)
             (evr 4 fll 3 )
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
            ;(rpl 7 asc [1 2]  [r "n d4" "n c5" r] nil)
            (ins 1  ["n e3" ["n c2"r "n e4"] "nc5" [r "ne4" "nd3"]])
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