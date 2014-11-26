(ns user
  (:require [arkham-horror.setup :as setup]))

(def active-game (agent nil))

(defn begin []
  (print "Welcome to Arkham Horror")
  (send active-game setup/begin))

(defn onslaught []
  (send active-game setup/onslaught)
  (print (@active-game :ancient-one) "has ended the world"))
