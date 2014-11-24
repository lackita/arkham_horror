(ns arkham-horror.combat
  (:require [arkham-horror.investigators :as investigators]
            [arkham-horror.doom-track :as doom-track]))

(defn ancient-one-attack [game]
  (-> game
      investigators/resolve-ancient-one-attack
      doom-track/advance))
