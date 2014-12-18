(ns arkham-horror.game.active
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
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

(defn begin [config]
  (send active-game (fn [_] (game/make config)))
  (await active-game)
  (set-help! '[(start-init)])
  (print "Welcome to Arkham Horror!"))

(defn start-init []
  (send active-game phase/start-init)
  (await active-game)
  (set-help! '[(init-investigator {:speed <speed> :fight <fight> :lore <lore>})])
  (print "Initialization started"))

(defn init-investigator [config]
  (send active-game #(game/init-investigator % config))
  (await active-game)
  (set-help! '[(advance-phase)])
  (await active-game)
  (print (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn advance-phase []
  (send active-game game/advance-phase)
  (await active-game)
  (set-help! `[(~(@active-game :end-phase))])
  (await active-game)
  (print (if (phase/over? (phase/get @active-game)) "Phase over" "")))

(defn end-init []
  (send active-game phase/end-init)
  (await active-game)
  (set-help! '[(awaken)])
  (print "Investigators initialized"))

(defn awaken []
  (send active-game ancient-one/awaken)
  (await active-game)
  (if (game/over? @active-game)
    (set-help! '[(reset)])
    (set-help! '[(start-upkeep)]))
  (await active-game)
  (if (game/lost? @active-game)
    (print (str (:name (ancient-one/get @active-game)) " has destroyed the world!"))
    (print (:name (ancient-one/get @active-game)) "awakened")))

(defn start-upkeep []
  (send active-game phase/start-upkeep)
  (await active-game)
  (set-help! '[(focus-investigator {:speed-sneak <delta> :fight-will <delta> :lore-luck <delta>})
               (advance-phase)])
  (await active-game)
  (print (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn focus-investigator [deltas]
  (send active-game #(game/focus-investigator % deltas))
  (await active-game)
  (print (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn end-upkeep []
  (send active-game phase/end-upkeep)
  (await active-game)
  (set-help! '[(start-attack)])
  (print "Investigators refreshed"))

(defn start-attack []
  (send active-game combat/start)
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
  (send active-game combat/investigator-attack)
  (set-help! '[(accept-roll) (exhaust-item <n>)])
  (await active-game)
  (apply print "Roll:"
         (old-dice/pending-roll
          (structure/get-path @active-game [phase investigator old-dice]))))

(defn exhaust-item [n]
  (send active-game #(setup/exhaust-item % n))
  (await active-game)
  (apply print "Roll:"
         (sort (old-dice/pending-roll
                (structure/get-path @active-game [phase investigator old-dice])))))

(defn accept-roll []
  (send active-game combat/accept-roll)
  (set-help! '[(end-attack)])
  (await active-game)
  (if (game/won? @active-game)
    (print (str (:name (ancient-one/get @active-game)) " has been defeated!"))
    (if (phase/over? (phase/get @active-game))
      (print "Phase over")
      (print "Attack\nDoom track:" (doom-track/level
                                    (structure/get-path @active-game
                                                        [ancient-one doom-track]))))))

(defn end-attack []
  (send active-game combat/end)
  (await active-game)
  (set-help! '[(defend)])
  (print "Defend"))

(defn defend []
  (send active-game combat/ancient-one-attack)
  (await active-game)
  (if (game/over? @active-game)
    (do (set-help! '[(reset)])
        (print (if (game/won? @active-game)
                 nil
                 (str (:name (ancient-one/get @active-game)) " has destroyed the world!"))))
    (print (clojure.string/join "\n" (map investigator/describe
                                          (@active-game :investigators))))))
