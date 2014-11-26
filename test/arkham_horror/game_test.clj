(ns arkham-horror.game-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]))

(def cthulu-game (game/make {:ancient-one :cthulu
                             :investigators [nil]}))
(def azathoth-game (game/make {:ancient-one :azathoth
                               :investigators [nil]}))
(def awakened-game (ancient-one/awaken cthulu-game))
(def won-game (ancient-one/defeat awakened-game))
(def lost-game (ancient-one/awaken azathoth-game))

(deftest won-test
  (is (game/won? won-game))
  (is (not (game/won? awakened-game)))
  (is (not (game/won? cthulu-game))))

(deftest over-test
  (is (game/over? lost-game))
  (is (not (game/over? azathoth-game)))
  (is (game/over? won-game)))

(deftest ending-message-test
  (is (= (game/ending-message won-game)
         ":cthulu has been defeated"))
  (is (= (game/ending-message lost-game)
         ":azathoth has ended the world"))
  (is (= (game/ending-message cthulu-game)
         (str cthulu-game " ended unexpectedly"))))
