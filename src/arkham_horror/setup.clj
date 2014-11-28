(ns arkham-horror.setup
  (:require [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator :as investigator]))

(defn begin [ancient-one investigators]
  (ancient-one/awaken (game/make {:ancient-one ancient-one
                                  :investigators (map #(investigator/make (:name %) (:stats %))
                                                      investigators)})))

(defn onslaught [active-game]
  (if (game/over? active-game)
    active-game
    (-> active-game
        combat/investigators-attack
        combat/ancient-one-attack
        onslaught)))
