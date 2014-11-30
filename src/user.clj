(ns user
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]))

(def active-game (agent nil))

(defn make-facade [function]
  (fn [& args]
    (send active-game function args)
    (await active-game)
    (setup/game-status @active-game)))

(def begin (make-facade setup/begin))
(def init (make-facade setup/init))
(def awaken (make-facade setup/awaken))
(def attack (make-facade setup/attack))
