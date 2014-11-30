(ns arkham-horror.setup
  (:require [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator :as investigator]))

(defn begin [ancient-one investigators]
  (game/make {:ancient-one ancient-one
              :investigators (map #(investigator/make %)
                                  investigators)}))

(defn init [{investigators :investigators :as game} config]
  (merge game
         {:investigators (map investigator/init investigators config)
          :initialized true}))

(defn awaken [active-game]
  (ancient-one/awaken active-game))

(defn attack [active-game]
  (-> active-game
      combat/ancient-one-attack
      combat/investigators-attack))

(defn game-status [active-game]
  (cond (game/won? active-game) "You win"
        (game/lost? active-game) "You lose"
        (ancient-one/awakened? active-game) "Attack"
        (active-game :initialized) "Awaken ancient one"
        :else "Initialize investigators"))

(defn onslaught [active-game]
  (if (game/over? active-game)
    active-game
    (-> active-game
        attack
        onslaught)))
