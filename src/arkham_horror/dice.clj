(ns arkham-horror.dice
  [:require [arkham-horror.ancient-one :as ancient-one]])

(defn loaded [pips]
  (fn [] pips))

(defn roll [dice times]
  (take times (repeatedly dice)))

(defn combat-check [game fight]
  (count (filter #(#{5 6} %) (roll (game :dice)
                                   (+ fight
                                      (ancient-one/combat-modifier game))))))
