(ns user
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.structure :as structure]))

(def active-game (agent nil))

(defn make-facade [function]
  (fn [& args]
    (apply send active-game function args)
    (await active-game)
    (print (game/message @active-game))))

(def begin (make-facade #(game/make %2)))
(def start-init (make-facade phase/start))
(def end-init (make-facade phase/end))
(def advance-phase (make-facade game/advance-phase))
(def init-investigator (make-facade game/init-investigator))
(def awaken (make-facade ancient-one/awaken))
(def focus (make-facade setup/focus))
(def start-attack (make-facade combat/start))
(def attack (make-facade combat/investigator-attack))
(def exhaust-item (make-facade setup/exhaust-item))
(def accept-roll (make-facade combat/accept-roll))
(def defend (make-facade (comp combat/end combat/ancient-one-attack)))
