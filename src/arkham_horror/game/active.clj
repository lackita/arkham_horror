(ns arkham-horror.game.active
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.help :as help]
            [arkham-horror.structure :as structure]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as old-dice]
            [arkham-horror.dice :as dice]))

(def active-game (agent nil))
(def dice (agent (dice/make :random)))
(def help-info (ref nil))

(defn set-help! [actions]
  (dosync (ref-set help-info actions)))

(defn help []
  @help-info)

(defn reset []
  (set-help! '[(begin <config>)])
  (if (agent-error active-game)
    (restart-agent active-game {} :clear-actions true)
    (send active-game (fn [_] {}))))

(reset)

(defn make-facade [function]
  (fn [& args]
    (apply send active-game function args)
    (await active-game)
    (print (game/message @active-game))))

(defn begin [config]
  ((make-facade (fn [_] (help/set-message (game/make config) ""))))
  (set-help! '[(start-init)])
  (print "Welcome to Arkham Horror!"))

(defn start-init []
  ((make-facade phase/start-init))
  (set-help! '[(init-investigator {:speed <speed> :fight <fight> :lore <lore>})])
  (print "Initialization started"))

(defn init-investigator [config]
  ((make-facade #(game/init-investigator % config)))
  (set-help! '[(advance-phase)])
  (await active-game)
  (print (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn advance-phase []
  (send active-game #(help/set-message % ""))
  ((make-facade game/advance-phase))
  (await active-game)
  (set-help! `[(~(@active-game :end-phase))])
  (await active-game)
  (print (if (phase/over? (phase/get @active-game)) "Phase over" "")))

(defn end-init []
  ((make-facade phase/end-init))
  (set-help! '[(awaken)])
  (print "Investigators initialized"))

(defn awaken []
  ((make-facade ancient-one/awaken))
  (await active-game)
  (if (game/over? @active-game)
    (set-help! '[(reset)])
    (set-help! '[(start-upkeep)]))
  (await active-game)
  (when (not (game/over? @active-game))
    (print (:name (ancient-one/get @active-game)) "awakened")))

(defn start-upkeep []
  ((make-facade phase/start-upkeep))
  (set-help! '[(focus-investigator {:speed-sneak <delta> :fight-will <delta> :lore-luck <delta>})
               (advance-phase)]))

(defn focus-investigator [deltas]
  ((make-facade #(game/focus-investigator % deltas)))
  (await active-game)
  (print (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn end-upkeep []
  ((make-facade phase/end-upkeep))
  (set-help! '[(start-attack)])
  (print "Investigators refreshed"))

(defn start-attack []
  ((make-facade combat/start))
  (set-help! '[(attack)])
  (await active-game)
  (if (phase/over? (phase/get @active-game))
    (print "Phase over")
    (print "Attack\nDoom track:" (doom-track/level
                                  (structure/get-path @active-game
                                                      [ancient-one doom-track])))))

(defn attack []
  (send dice #(dice/roll-and-save % 3))
  (structure/get-path @active-game [phase investigator])
  ((make-facade combat/investigator-attack))
  (set-help! '[(accept-roll) (exhaust-item <n>)])
  (await active-game)
  (print (->> (structure/get-path @active-game [phase investigator old-dice])
              old-dice/pending-roll
              (clojure.string/join " ")
              (str "Roll: "))))

(defn exhaust-item [n]
  ((make-facade #(setup/exhaust-item % n))))

(defn accept-roll []
  ((make-facade combat/accept-roll))
  (set-help! '[(end-attack)]))

(defn end-attack []
  ((make-facade combat/end))
  (set-help! '[(defend)]))

(defn defend []
  ((make-facade combat/ancient-one-attack))
  (await active-game)
  (when (game/over? @active-game)
    (set-help! '[(reset)])))
