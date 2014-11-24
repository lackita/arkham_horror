(ns arkham-horror.players
  (:require [arkham-horror.player :as player]))

(defn resolve-ancient-one-attack [{players :players
                                   :as game}]
  (assoc game :players (player/resolve-ancient-one-attack players)))
