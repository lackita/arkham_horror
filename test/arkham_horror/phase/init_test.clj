(ns arkham-horror.phase.init-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.phase :as phase]
            [arkham-horror.phase.init :as init]))

(deftest start-test
  (is (phase/get (init/start (game/make {:ancient-one "Cthulu"
                                         :investigators ["Monterey Jack"]})))))

(deftest stats-test
  (is (= (investigator/get (init/stats (phase/make [(investigator/make "Monterey Jack")])
                                       {:speed 2 :fight 3 :lore 4}))
         (investigator/init (investigator/make "Monterey Jack")
                            {:speed 2 :fight 3 :lore 4}))))
