(ns arkham-horror.setup
  (:require [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.dice :as dice]))

(defn begin [ancient-one investigators]
  (game/make {:ancient-one ancient-one
              :investigators (map #(investigator/make %)
                                  investigators)}))

(defn init [{investigators :investigators :as game} config]
  (merge game
         {:investigators (map investigator/init investigators config)
          :initialized true}))

(defn awaken [active-game]
  (ancient-one/awaken active-game))

(defn focus [{investigators :investigators :as active-game} deltas]
  (assoc active-game :investigators (map investigator/focus investigators deltas)))

(defn game-status [active-game]
  (cond (game/won? active-game) "You win"
        (game/lost? active-game) "You lose"
        (dice/pending-roll (dice/get active-game)) (->> (dice/get active-game)
                                                        dice/pending-roll
                                                        (clojure.string/join " ")
                                                        (str "Roll: "))
        (and (combat/in-combat? active-game)
             (not (phase/investigator active-game))) "Defend"
             (combat/in-combat? active-game) (str "Attack\n" "Doom track: "
                                                  (active-game :doom-track))
             (ancient-one/awakened? (ancient-one/get active-game)) "Refresh investigators"
             (active-game :initialized) "Awaken ancient one"
             :else "Initialize investigators"))
