(ns user
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]))

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
(def focus (make-facade setup/focus))
(def start-attack (make-facade combat/start-attack))
(def attack (make-facade combat/investigator-attack))
(def defend (make-facade combat/ancient-one-attack))
