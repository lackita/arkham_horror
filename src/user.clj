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
  (cond (game/lost? @active-game) (print (@active-game :ancient-one) "has ended the world")
        (game/won? @active-game)  (print (@active-game :ancient-one) "has been defeated")
        :else                     (print @active-game "ended unexpectedly")))
