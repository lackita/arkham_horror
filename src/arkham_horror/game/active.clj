(ns arkham-horror.game.active
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.help :as help]
            [arkham-horror.structure :as structure]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.dice :as dice]))

(def active-game (agent nil))
(def dice (agent (dice/make :random)))
(def help (ref (help/make)))

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

(defn begin [config]
  ((make-facade (fn [_] (game/make config)))))
(defn start-init []
  ((make-facade phase/start-init)))
(defn init-investigator [config]
  ((make-facade #(game/init-investigator % config))))
(defn advance-phase []
  ((make-facade game/advance-phase)))
(defn end-init []
  ((make-facade phase/end-init)))
(defn awaken []
  ((make-facade ancient-one/awaken)))
(defn start-upkeep []
  ((make-facade phase/start-upkeep)))
(defn focus-investigator [deltas]
  ((make-facade #(game/focus-investigator % deltas))))
(defn end-upkeep []
  ((make-facade phase/end-upkeep)))
(defn start-attack []
  ((make-facade combat/start)))
(defn attack []
  (send dice #(dice/roll-and-save % 3))
  (structure/get-path @active-game [phase investigator])
  ((make-facade combat/investigator-attack)))
(defn exhaust-item [n]
  ((make-facade #(setup/exhaust-item % n))))
(defn accept-roll []
  ((make-facade combat/accept-roll)))
(defn end-attack []
  ((make-facade combat/end)))
(defn defend []
  ((make-facade combat/ancient-one-attack)))
