(ns arkham-horror.azathoth-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest ends-world-test
  (is (-> {:ancient-one ancient-one/azathoth}
          game/make
          ancient-one/awaken
          game/lost?)))
