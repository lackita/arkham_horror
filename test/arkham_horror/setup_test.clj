(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]))

(deftest begin-test
  (is (ancient-one/awakened? (setup/begin)))
  (is (ancient-one/valid? (setup/begin))))

(deftest setup-test
  (is (game/lost? (setup/onslaught (setup/begin)))))
