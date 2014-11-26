(ns arkham-horror.setup
  (:require [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]))

(defn begin []
  (ancient-one/awaken (game/make {:ancient-one (ancient-one/random)})))

(defn onslaught [active-game]
  (if (game/lost? active-game)
    active-game
    (-> active-game
        combat/investigators-attack
        combat/ancient-one-attack
        onslaught)))
