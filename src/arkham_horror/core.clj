(ns arkham-horror.core
  (:require [arkham-horror.status :as status]))

(defn get-status []
  (status/get-message))
(defn set-status! [& args]
  (apply status/set-message! args))

(def game (ref nil))
(def ancient-one (ref nil))

(defn reset []
  (dosync (set-status! "Game not started."
                       "None"
                       '(begin <config>))
          (ref-set ancient-one {})
          (ref-set game {})))
(reset)

(defn begin [config]
  (dosync (set-status! "Welcome to Arkham Horror!"
                       "Setup"
                       '(def investigator (investigator/make <name> {:speed <speed>
                                                                     :fight <fight>
                                                                     :lore <lore>}))
                       '(awaken))
          (alter game assoc :begun true)
          (alter ancient-one assoc :name (config :ancient-one))))

(defn awaken []
  (dosync (if (= (@ancient-one :name) "Azathoth")
            (set-status! "Azathoth has destroyed the world!"
                         "Lost"
                         '(reset))
            (set-status! (clojure.string/join "\n" ["Monterey Jack:"
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
          (alter ancient-one assoc :awakened true)))

(defn end-upkeep []
  (dosync (set-status! "Doom track: 13"
                       "Attack"
                       '(investigator/attack <investigator>))))

(defn accept-roll []
  (dosync (set-status! "Remaining meters:\n\tMonterey Jack\n\t\t:maximum-stamina: 6/6\n\t\t:maximum-sanity: 2/2"
                       "Defend"
                       '(investigator/defend <investigator> <meter>))))
