(ns user
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
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
(def init (make-facade setup/init))
(def awaken (make-facade setup/awaken))
(def focus (make-facade setup/focus))
(def start-attack (make-facade combat/start))
(def attack (make-facade combat/investigator-attack))
(def exhaust-item (make-facade setup/exhaust-item))
(def accept-roll (make-facade combat/accept-roll))
(def defend (make-facade (comp combat/end combat/ancient-one-attack)))
