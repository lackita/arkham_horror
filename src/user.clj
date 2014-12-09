(ns user
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]))

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
(def exhaust-item (make-facade (fn [_ & [game n]]
                                 (phase/update
                                  game
                                  (fn [phase]
                                    (investigator/update
                                     phase
                                     #(investigator/exhaust-item % n)))))))
(def accept-roll (make-facade combat/accept-roll))
(def defend (make-facade (comp combat/end-attack combat/ancient-one-attack)))
