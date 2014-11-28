(ns arkham-horror.stat)

(defn make [min value max]
  {:min min :value value :max max})

(defn extract [s]
  #((% s) :value))
(def speed (extract :speed))
(def sneak (extract :sneak))
(def fight (extract :fight))
(def will  (extract :will))
(def lore  (extract :lore))
(def luck  (extract :luck))

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
