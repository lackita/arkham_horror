(ns arkham-horror.setup
  (:require [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.structure :as structure]))

(defn begin [ancient-one investigators]
  (game/make {:ancient-one ancient-one
              :investigators investigators}))

(defn init [{investigators :investigators :as game} config]
  (merge game
         {:investigators (map investigator/init investigators config)
          :initialized true}))

(defn awaken [active-game]
  (ancient-one/awaken active-game))

(defn focus [{investigators :investigators :as active-game} deltas]
  (assoc active-game :investigators (map investigator/focus investigators deltas)))

(defn game-status [active-game]
  (cond (game/won? active-game)
          "You win"
        (game/lost? active-game)
          "You lose"
        (dice/pending-roll (structure/get-path active-game [phase investigator dice]))
          (->> (structure/get-path active-game [phase investigator dice])
               dice/pending-roll
               (clojure.string/join " ")
               (str "Roll: "))
        (and (combat/in-combat? active-game)
             (not (structure/get-path active-game [phase investigator])))
          "Defend"
        (combat/in-combat? active-game)
          (str "Attack\n" "Doom track: "
               (doom-track/level (structure/get-path active-game [ancient-one doom-track])))
        (ancient-one/awakened? (ancient-one/get active-game))
          "Refresh investigators"
        (active-game :initialized)
          "Awaken ancient one"
        :else
          "Initialize investigators"))
