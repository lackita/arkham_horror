(ns arkham-horror.game.active
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.help :as help]))

(def active-game (agent (help/set-available-actions {} '(begin <config>))))

(defn make-facade [function]
  (fn [& args]
    (apply send active-game function args)
    (await active-game)
    (print (game/message @active-game))))

(defn help []
  (await active-game)
  (help/get-available-actions @active-game))

(def begin (make-facade #(game/make %2)))
(def start-init (make-facade phase/start))
(def init-investigator (make-facade game/init-investigator))
(def advance-phase (make-facade game/advance-phase))
(def end-init (make-facade phase/end-init))
(def awaken (make-facade ancient-one/awaken))
(def start-upkeep (make-facade phase/start-upkeep))
(def focus-investigator (make-facade game/focus-investigator))
(def start-attack (make-facade combat/start))
(def attack (make-facade combat/investigator-attack))
(def exhaust-item (make-facade setup/exhaust-item))
(def accept-roll (make-facade combat/accept-roll))
(def defend (make-facade (comp combat/end combat/ancient-one-attack)))
