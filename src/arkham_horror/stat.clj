(ns arkham-horror.stat)

(defn make [stat-name min max direction]
  {:name (clojure.string/capitalize (name stat-name))
   :min min
   :max max
   :direction direction})

(defn describe [stat]
  (clojure.string/join " "
    (cons (format "%-5s" (stat :name))
          (map #(if (= (stat :value) %)
                  (str "<" % ">")
                  (str " " % " "))
               (if (= (stat :direction) :ascending)
                 (range (stat :min) (inc (stat :max)))
                 (range (stat :max) (dec (stat :min)) -1))))))

(defn extract [s]
  #((% s) :value))
(def speed (extract :speed))
(def sneak (extract :sneak))
(def fight (extract :fight))
(def will  (extract :will))
(def lore  (extract :lore))
(def luck  (extract :luck))

(defn infuse [s]
  #(assoc-in %1 [s :value] %2))
(def set-speed (infuse :speed))
(def set-sneak (infuse :sneak))
(def set-fight (infuse :fight))
(def set-will  (infuse :will))
(def set-lore  (infuse :lore))
(def set-luck  (infuse :luck))

(defn shift [stat delta]
  (update-in stat [:value] #(->> (+ % delta)
                                 (min (stat :max))
                                 (max (stat :min)))))
(defn slider [ascending descending]
  #(merge-with shift %1 {ascending %2
                         descending (- %2)}))
(def speed-sneak-slider (slider :speed :sneak))
(def fight-will-slider  (slider :fight :will))
(def lore-luck-slider   (slider :lore :luck))

(defn maximum-sanity [investigator]
  (investigator :maximum-sanity))

(defn maximum-stamina [investigator]
  (investigator :maximum-stamina))

(defn get-smaller [{sanity :maximum-sanity
                    stamina :maximum-stamina}]
  (if (> sanity stamina)
    :maximum-sanity
    :maximum-stamina))

(defn rig-fight [investigator value]
  (assoc-in investigator [:fight :value] value))
