(ns arkham-horror.board-test
  (:require [clojure.test :refer :all]
            [arkham-horror.board :as board]))

(deftest status-test
  (is (= (board/make-status "Foo" "Bar" '(foo-bar) '(baz))
         (clojure.string/join "\n" ["Foo"
                                    "Phase: Bar"
                                    "Commands:"
                                    "\t(foo-bar)"
                                    "\t(baz)"]))))
