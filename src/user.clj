(ns user (:require [arkham-horror.setup :as setup]))

(def active-game (agent nil))

(defn make-facade [function]
  (fn [& args]
    (apply send active-game function args)
    (await active-game)
    (print (setup/game-status @active-game))))

(def begin (make-facade (fn [_ & [ancient-one investigators]]
                          (setup/begin ancient-one investigators))))
(def init (make-facade setup/init))
(def awaken (make-facade setup/awaken))
(def attack (make-facade setup/attack))
