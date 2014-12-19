(ns arkham-horror.game.active
  (:require [arkham-horror.setup :as setup]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]
            [arkham-horror.structure :as structure]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]
            [clojure.string :refer [join]]))

(def active-game (ref {}))
(def phase (ref nil))
(def ancient-one (ref nil))
(def help-info (ref nil))
(def lost? (ref false))
(def roll (ref []))

(defn set-help! [actions]
  (dosync (ref-set help-info (if (game/over? @active-game)
                               '[(reset)]
                               actions))))

(defn help []
  @help-info)

(defn print-status [message]
  (print (cond (game/won? @active-game) (str (:name (ancient-one/get @active-game))
                                             " has been defeated!")
               (or @lost? (game/lost? @active-game)) (str (:name (ancient-one/get @active-game))
                                                          " has destroyed the world!")
               :else message)))

(defn reset []
  (dosync (ref-set ancient-one nil)
          (ref-set phase nil)
          (set-help! '[(begin <config>)])
          (ref-set active-game {})))

(reset)

(defmacro make-move [move actions status]
  `(dosync (alter active-game ~move)
           (set-help! ~actions)
           (print-status ~status)))

(defn begin [config]
  (dosync (ref-set ancient-one (ancient-one/make (or (config :ancient-one) (ancient-one/random))))
          (make-move (fn [_] (game/make config)) '[(start-init)] "Welcome to Arkham Horror!")))

(defn start-init []
  (dosync (ref-set phase (assoc (phase/make (@active-game :investigators))
                           :end-phase 'end-init))
          (make-move phase/start
                     '[(init-investigator {:speed <speed> :fight <fight> :lore <lore>})]
                     "Initialization started")))

(defn advance-phase []
  (dosync (alter phase phase/advance)
          (make-move game/advance-phase `[(~(@phase :end-phase))]
                     (if (phase/over? (phase/get @active-game)) "Phase over" ""))))

(defn end-init []
  (dosync (ref-set phase nil)
          (make-move phase/end '[(awaken)] "Investigators initialized")))

(defn awaken []
  (dosync (let [results (ancient-one/awaken-actions @ancient-one)]
            (ref-set lost? (results :lost?))
            (ref-set ancient-one (results :ancient-one)))
          (make-move ancient-one/awaken '[(start-upkeep)]
                     (str (:name (ancient-one/get @active-game)) " awakened"))))

(defn investigator-status []
  (investigator/describe (structure/get-path @active-game [phase investigator])))

(defn init-investigator [config]
  (make-move #(game/init-investigator % config) '[(advance-phase)] (investigator-status)))

(defn start-upkeep []
  (dosync (ref-set phase (assoc (phase/make (@active-game :investigators))
                           :end-phase 'end-upkeep))
          (make-move phase/start
                     '[(focus-investigator {:speed-sneak <delta>
                                            :fight-will <delta>
                                            :lore-luck <delta>})
                       (advance-phase)]
                     (investigator-status))))

(defn focus-investigator [deltas]
  (make-move #(game/focus-investigator % deltas) '[(advance-phase)] (investigator-status)))

(defn end-upkeep []
  (dosync (ref-set phase nil)
          (make-move phase/end '[(start-attack)]
                     "Investigators refreshed")))

(defn roll-status []
  (join " " (cons "Roll:"
                  (sort (dice/pending-roll
                         (structure/get-path @active-game
                                             [phase investigator dice]))))))

(defn attack []
  (dosync (make-move combat/investigator-attack '[(accept-roll) (exhaust-item <n>)] (roll-status))
          (ref-set roll (dice/pending-roll (structure/get-path @active-game [phase investigator dice])))))

(defn exhaust-item [n]
  (make-move #(setup/exhaust-item % n) '[(accept-roll)] (roll-status)))

(defn ancient-one-status []
  (str "Attack\nDoom track: " (doom-track/level
                               (structure/get-path @active-game
                                                   [ancient-one doom-track]))))

(defn start-attack []
  (make-move combat/start '[(attack)] (ancient-one-status)))

(defn accept-roll []
  (dosync (alter phase phase/advance)
          (make-move combat/accept-roll '[(end-attack)] (ancient-one-status))))

(defn end-attack []
  (make-move combat/end '[(defend)] "Defend"))

(defn defend []
  (make-move combat/ancient-one-attack '[(start-upkeep)]
             (clojure.string/join "\n" (map investigator/describe
                                            (@active-game :investigators)))))
