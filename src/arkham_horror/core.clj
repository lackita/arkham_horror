(ns arkham-horror.core
  (:require [arkham-horror.board :as board]))

(board/reset)

(defn begin
  ([config] (begin board/board config))
  ([board config]
     (dosync (board/update-status! (board :status)
                                   "Welcome to Arkham Horror!"
                                   "Setup"
                                   '(def investigator (investigator/make <name> {:speed <speed>
                                                                                 :fight <fight>
                                                                                 :lore <lore>}))
                                   '(awaken))
             (alter (board :game) assoc :begun true)
             (alter (board :ancient-one) assoc :name (config :ancient-one)))))

(defn awaken ([] (awaken board/board))
  ([board]
     (dosync (if (= (@(board :ancient-one) :name) "Azathoth")
               (board/update-status! (board :status)
                                     "Azathoth has destroyed the world!"
                                     "Lost"
                                     '(board/reset))
               (board/update-status! (board :status)
                                     (clojure.string/join "\n" ["Monterey Jack:"
                                                                "\tStamina: 6/6"
                                                                "\tSanity:  2/2"
                                                                "\tSpeed:  1 <2> 3  4 "
                                                                "\tSneak:  3 <2> 1  0 "
                                                                "\tFight: <2> 3  4  5 "
                                                                "\tWill:  <3> 2  1  0 "
                                                                "\tLore:   1 <2> 3  4 "
                                                                "\tLuck:   5 <4> 3  2 "])
                                     "Upkeep"
                                     '(investigator/focus <investigator> {:speed-sneak <speed-delta>
                                                                          :fight-will <fight-delta>
                                                                          :lore-luck <lore-delta>})))
             (alter (board :ancient-one) assoc :awakened true))))

(defn end-upkeep
  ([] (end-upkeep board/board))
  ([board]
     (dosync (board/update-status! (board :status)
                                   "Doom track: 13"
                                   "Attack"
                                   '(investigator/attack <investigator>)))))

(defn accept-roll
  ([] (accept-roll board/board))
  ([board]
     (dosync (board/update-status! (board :status)
                                   "Remaining meters:\n\tMonterey Jack\n\t\t:maximum-stamina: 6/6\n\t\t:maximum-sanity: 2/2"
                                   "Defend"
                                   '(investigator/defend <investigator> <meter>)))))
