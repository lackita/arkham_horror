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
            [arkham-horror.dice :as dice]
            [clojure.string :refer [join]]))

(def active-game (agent {}))
(def dice (agent (dice/make :random)))
(def help-info (ref nil))

(defn set-help! [actions]
  (dosync (ref-set help-info (if (game/over? @active-game)
                               '[(reset)]
                               actions))))

(defn help []
  @help-info)

(defn print-status [message]
  (print (cond (game/won? @active-game) (str (:name (ancient-one/get @active-game))
                                             " has been defeated!")
               (game/lost? @active-game) (str (:name (ancient-one/get @active-game))
                                              " has destroyed the world!")
               :else message)))

(defn reset []
  (set-help! '[(begin <config>)])
  (if (agent-error active-game)
    (restart-agent active-game {} :clear-actions true)
    (send active-game (fn [_] {}))))

(reset)

(defmacro make-move [move actions status]
  `(do (send active-game ~move)
       (await active-game)
       (set-help! ~actions)
       (print-status ~status)))

(defn begin [config]
  (make-move (fn [_] (game/make config)) '[(start-init)] "Welcome to Arkham Horror!"))

(defn start-init []
  (make-move phase/start-init
             '[(init-investigator {:speed <speed> :fight <fight> :lore <lore>})]
             "Initialization started"))

(defn init-investigator [config]
  (make-move #(game/init-investigator % config)
             '[(advance-phase)]
             (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn advance-phase []
  (make-move game/advance-phase `[(~(@active-game :end-phase))]
             (if (phase/over? (phase/get @active-game)) "Phase over" "")))

(defn end-init []
  (make-move phase/end-init '[(awaken)] "Investigators initialized"))

(defn awaken []
  (make-move ancient-one/awaken '[(start-upkeep)]
             (str (:name (ancient-one/get @active-game)) " awakened")))

(defn start-upkeep []
  (make-move phase/start-upkeep
             '[(focus-investigator {:speed-sneak <delta> :fight-will <delta> :lore-luck <delta>})
               (advance-phase)]
             (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn focus-investigator [deltas]
  (make-move #(game/focus-investigator % deltas) '[(advance-phase)]
             (investigator/describe (structure/get-path @active-game [phase investigator]))))

(defn end-upkeep []
  (make-move phase/end-upkeep '[(start-attack)]
             "Investigators refreshed"))

(defn start-attack []
  (make-move combat/start '[(attack)]
             (str "Attack\nDoom track: " (doom-track/level
                                          (structure/get-path @active-game
                                                              [ancient-one doom-track])))))


;; (send dice #(dice/roll-and-save % 3))
;; (structure/get-path @active-game [phase investigator])

(defn attack []
  (make-move combat/investigator-attack '[(accept-roll) (exhaust-item <n>)]
             (join " " (cons "Roll:"
                             (sort (old-dice/pending-roll
                                    (structure/get-path @active-game
                                                        [phase investigator old-dice])))))))

(defn exhaust-item [n]
  (make-move #(setup/exhaust-item % n) '[(accept-roll)]
             (join " " (cons "Roll:"
                             (sort (old-dice/pending-roll
                                    (structure/get-path @active-game
                                                        [phase investigator old-dice])))))))

(defn accept-roll []
  (make-move combat/accept-roll '[(end-attack)]
             (str "Attack\nDoom track: " (doom-track/level
                                          (structure/get-path @active-game
                                                              [ancient-one doom-track])))))

(defn end-attack []
  (make-move combat/end '[(defend)] "Defend"))

(defn defend []
  (make-move combat/ancient-one-attack '[(start-upkeep)]
             (clojure.string/join "\n" (map investigator/describe
                                            (@active-game :investigators)))))
