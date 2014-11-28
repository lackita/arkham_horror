(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]))

(deftest begin-test
  (is (ancient-one/awakened? (setup/begin :cthulu [])))
  (is (ancient-one/valid? (setup/begin :azathoth [])))
  (is (= (:investigators (setup/begin :cthulu [{:name "Monterey Jack"
                                                :stats {:speed 2 :fight 2 :lore 2}}]))
         [(investigator/make :monterey-jack {:speed 2 :fight 2 :lore  2})])))

(deftest setup-test
  (is (game/over? (setup/onslaught (setup/begin :cthulu [])))))
