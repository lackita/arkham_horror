(ns arkham-horror.game
  (:require [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.phase :as phase]
            [arkham-horror.structure :as structure]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.combat :as combat]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.message :as message]))

(defn make [config]
  (-> config
      (ancient-one/set (ancient-one/make (or (config :ancient-one)
                                             (ancient-one/random))))
      (investigator/set-all (config :investigators) (config :dice))
      (message/set "Welcome to Arkham Horror!")))

(defn lost? [game]
  (every? investigator/devoured? (if (phase/get game)
                                   (phase/all-investigators (phase/get game))
                                   (game :investigators))))

(defn won? [game]
  (and (not (lost? game))
       (ancient-one/defeated? (ancient-one/get game))))

(defn over? [game]
  (or (lost? game)
      (won? game)))

(defn init-investigator [game stats]
  (phase/update game #(phase/init-investigator % stats)))

(defn advance-phase [game]
  (phase/update game phase/advance))

(defn message [game]
  (cond (won? game)
        "You win"
        (lost? game)
        "You lose"
        (dice/pending-roll (structure/get-path game [phase investigator dice]))
        (->> (structure/get-path game [phase investigator dice])
             dice/pending-roll
             (clojure.string/join " ")
             (str "Roll: "))
        (and (combat/get game)
             (not (structure/get-path game [phase investigator])))
        "Defend"
        (combat/get game)
        (str "Attack\n" "Doom track: "
             (doom-track/level (structure/get-path game [ancient-one doom-track])))
        (ancient-one/awakened? (ancient-one/get game))
        "Refresh investigators"
        (game :initialized)
        "Awaken ancient one"
        :else
        (message/get game)))
