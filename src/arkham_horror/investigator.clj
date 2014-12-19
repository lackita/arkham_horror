(ns arkham-horror.investigator
  (:require [arkham-horror.core :as core]))

(defn make [name {speed :speed fight :fight lore :lore}]
  {:pre [(not (@core/ancient-one :awakened)) (@core/game :begun)
         (> speed 0) (< speed 5)
         (> fight 1) (< fight 6)
         (> lore  0) (< lore  5)]}
  (ref {:name name
        :maximum-stamina 7 :maximum-sanity  3
        :speed speed       :sneak (- 4 speed)
        :fight fight       :will  (- 5 fight)
        :lore  lore        :luck  (- 6 lore)}))

(defn focus [investigator {speed-delta :speed-sneak
                           fight-delta :fight-will
                           lore-delta  :lore-luck}]
  (dosync (alter investigator #(merge-with + % {:speed (or speed-delta 0)
                                                :sneak (- (or speed-delta 0))
                                                :fight (or fight-delta 0)
                                                :will  (- (or fight-delta 0))
                                                :lore  (or lore-delta 0)
                                                :luck  (- (or lore-delta 0))}))))
