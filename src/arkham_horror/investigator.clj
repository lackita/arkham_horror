(ns arkham-horror.investigator
  (:require [arkham-horror.core :as core]))

(defn make [name {speed :speed fight :fight lore :lore}]
  {:pre [(not (@core/ancient-one :awakened)) (@core/game :begun)
         (> speed 0) (< speed 5)
         (> fight 1) (< fight 6)
         (> lore  0) (< lore  5)]}
  (ref {:name name
        :speed speed :sneak (- 4 speed)
        :fight fight :will  (- 5 fight)
        :lore  lore  :luck  (- 6 lore)}))
