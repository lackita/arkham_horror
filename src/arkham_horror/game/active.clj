(ns arkham-horror.game.active
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.help :as help]))

(def active-game (agent nil))

(defn pre-begin [& _]
  (help/set-available-actions {} '[(begin <config>)]))
(defn reset []
  (if (agent-error active-game)
    (restart-agent active-game (pre-begin) :clear-actions true)
    (send active-game pre-begin)))

(reset)

(defn help []
  (await active-game)
  (help/get-available-actions @active-game))

(defn make-facade [function]
  (fn [& args]
    (apply send active-game function args)
    (await active-game)
    (when (game/over? @active-game)
      (send active-game #(help/set-available-actions % '[(reset)])))
    (print (game/message @active-game))
    (help)))

(def begin (make-facade (fn [_ config] (game/make config))))
(def start-init (make-facade phase/start-init))
(def init-investigator (make-facade game/init-investigator))
(def advance-phase (make-facade game/advance-phase))
(def end-init (make-facade phase/end-init))
(def awaken (make-facade ancient-one/awaken))
(def start-upkeep (make-facade phase/start-upkeep))
(def focus-investigator (make-facade game/focus-investigator))
(def end-upkeep (make-facade phase/end-upkeep))
(def start-attack (make-facade combat/start))
(def attack (make-facade combat/investigator-attack))
(def exhaust-item (make-facade setup/exhaust-item))
(def accept-roll (make-facade combat/accept-roll))
(def end-attack (make-facade combat/end))
(def defend (make-facade combat/ancient-one-attack))
