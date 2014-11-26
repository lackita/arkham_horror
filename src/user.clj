(ns user
  (:require [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigators :as investigators]
            [arkham-horror.setup :as setup]))

(def active-game (agent nil))

(defn begin []
  (print "Welcome to Arkham Horror")
  (send active-game setup/begin))

(defn onslaught []
  (if (game/lost? @active-game)
    (print (@active-game :ancient-one) "has ended the world")
    (do
      (send active-game combat/investigators-attack)
      (send active-game combat/ancient-one-attack)
      (onslaught))))
