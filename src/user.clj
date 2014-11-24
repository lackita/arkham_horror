(ns user
  (:require [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]))

(def active-game (agent nil))

(defn begin []
  (print "Welcome to Arkham Horror")
  (send active-game
        (fn [_]
          (ancient-one/awaken (game/make {:ancient-one (ancient-one/random)})))))

(defn onslaught []
  (if (game/lost? @active-game)
    (print (@active-game :ancient-one) "has ended the world")
    (do (send active-game ancient-one/attack)
        (onslaught))))
