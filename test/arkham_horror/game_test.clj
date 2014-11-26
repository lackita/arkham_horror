(ns arkham-horror.game-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]))

(deftest won-test
  (is (-> {:ancient-one :cthulu}
          game/make
          ancient-one/awaken
          ancient-one/defeat
          game/won?))
  (is (not (-> {:ancient-one :cthulu}
               game/make
               ancient-one/awaken
               game/won?)))
  (is (not (-> {:ancient-one :cthulu}
               game/make
               game/won?))))
