(ns user
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]))

(def active-game (agent nil))

(defn begin []
  (print "Welcome to Arkham Horror")
  (send active-game setup/begin))

(defn onslaught []
  (send active-game setup/onslaught)
  (await active-game)
  (print (game/ending-message @active-game)))
