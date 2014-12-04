(ns arkham-horror.ancient-one-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest random-test
  (is (ancient-one/valid? {:ancient-one (ancient-one/random)}))
  (is (= (set (take 100 (repeatedly ancient-one/random)))
         ancient-one/available)))

(deftest awakened?-test
  (is (ancient-one/awakened? (ancient-one/awaken (game/make {:ancient-one :cthulu}))))
  (is (not (ancient-one/awakened? (game/make {:ancient-one :cthulu}))))
  (is (ancient-one/awakened? (ancient-one/awaken (game/make {:ancient-one :azathoth}))))
  (is (not (ancient-one/awakened? (game/make {:ancient-one :azathoth})))))
