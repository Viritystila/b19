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
             (#(first %))
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

  (volume! :markorona 1.075)

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

(defn asc_2 [coll n input & args]
  ;(println coll)
  (let [is_n_vec      (vector? n)
        isfn          (fn? input)

        coll_length   (count coll)
        max_n         (if is_n_vec
                        (mod (apply max n) (+ 1 coll_length))
                        (mod n (+ 1 coll_length)))

        nth_element   (nth coll (mod max_n coll_length))
        input         (if isfn (apply input (conj args nth_element)) input)
        is_input_vec  (vector? input)
        bothvec       (and is_n_vec is_input_vec)
        min_length    (if bothvec
                        (min (count n) (count input))
                        0)
        nmap          (if is_n_vec
                        (map (fn [x] (mod x (+ 1 coll_length))) n)
                       n)
        ]
    (if bothvec
      (apply assoc coll (interleave nmap input) )
      (assoc coll max_n input))
    ))

;;;;;;;;;;;;
;;;tb303sn
;;;;;;;;;;;

(println (map find-note-name (chord :d2 :7sus2)))

(def d_7sus2 )

(do
  (trg :tb303sn tb303)

  (pause! :tb303sn)

  (trg :tb303sn
       tb303
       :in-trg
       (->  ["n e3" r r ["n d3" "n d4"]]
            (rep 8)
            (evr 2 asc 0  [r "n c4"])
            (evr 1 fst)
            (evr 4 rpl 1 ["n  e5"])
            (rpl 7 asc 2 ["n  d5"] nil)
            (rpl 1 asc_2 [1 2]  ["n d6" "n c6"] nil)
            ;(#(assoc (piv %) 1 [1 1 1 1 ]))
            ;(#(assoc (piv %) 1  (apply assoc (nth  % 1) (interleave [1 2] ["n d6" "n c6"] ) ) ))
                                        ;(#())
                                        ;(evr 1 ["n c2"])
            ;(seq)
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
       :in-release [0.1273]
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
       (rep [0] 4)
       )


  (volume! :tb303sn 1)

  )

(play! :tb303sn)

(fade-out! :tb303sn)

(stp :tb303sn)

;;;;;;;;;;;;;;;;
;; End tb303sn
;;;;;;;;;;;;;;;;
