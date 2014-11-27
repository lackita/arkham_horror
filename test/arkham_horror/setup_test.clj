(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]))

(deftest begin-test
  (is (ancient-one/awakened? (setup/begin :cthulu [])))
  (is (ancient-one/valid? (setup/begin :azathoth []))))

(deftest setup-test
  (is (game/over? (setup/onslaught (setup/begin :cthulu [])))))
