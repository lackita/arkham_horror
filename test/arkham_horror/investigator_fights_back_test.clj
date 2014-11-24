(ns arkham-horror.investigator-fights-back-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest investigator-attacks-test
  (investigator/attack
   (ancient-one/awaken
    (game/make {:ancient-one :cthulu
                :investigators [(investigator/make {:will 3})]}))))
