(ns arkham-horror.investigator
  (:require [arkham-horror.stat :as stat]))

(defn devoured? [investigator]
  (->> [:maximum-sanity :maximum-stamina]
       (map investigator)
       (not-any? zero?)))

(defn reduce-max-sanity-or-stamina [investigator]
  (update-in investigator [(stat/get-smaller investigator)] dec))

(defn focus [investigator deltas]
  {:pre [(<= (apply + (vals deltas))
             (investigator :focus))]}
  (-> investigator
      (stat/speed-sneak-slider (or (deltas :speed-sneak) 0))
      (stat/fight-will-slider  (or (deltas :fight-will)  0))
      (stat/lore-luck-slider   (or (deltas :lore-luck)   0))))

(defn make
  ([investigator config]
     (make (merge config {:sneak (- 4 (config :speed))
                          :will  (- 5 (config :fight))
                          :luck  (- 6 (config :lore))})))
  ([config] {:speed (stat/make 0 (config :speed) 3)
             :sneak (stat/make 0 (config :sneak) 3)
             :fight (stat/make 2 (config :fight) 5)
             :will  (stat/make 2 (config :will)  5)
             :lore  (stat/make 4 (config :lore)  7)
             :luck  (stat/make 4 (config :luck)  7)
             :focus (config :focus)
             :maximum-sanity (or (config :maximum-sanity) 3)
             :maximum-stamina (or (config :maximum-stamina) 7)}))
