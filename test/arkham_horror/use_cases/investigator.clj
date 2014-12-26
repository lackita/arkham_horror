(ns arkham-horror.use-cases.investigator
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [arkham-horror.generators :as gen']
            [arkham-horror.investigator :as investigator]))

(deftest sanity
  (checking "Primary Course" [investigator gen'/investigator]
    (dosync
     (investigator/decrement-maximum-sanity investigator)
     (is (= (investigator/maximum-sanity investigator)
            (dec (investigator/initial-maximum-sanity (investigator/name investigator)))))
     (is (= (investigator/maximum-sanity investigator) (investigator/sanity investigator)))
     (investigator/move-sanity investigator -2)
     (is (= (investigator/sanity investigator)
            (- (investigator/maximum-sanity investigator) 2)))))
  (checking "Exceptional Course: Too High" [investigator gen'/investigator]
    (dosync
     (investigator/move-sanity investigator 1)
     (is (= (investigator/sanity investigator) (investigator/maximum-sanity investigator)))))
  (checking "Exceptional Course: Too Low" [investigator gen'/investigator]
    (dosync
     (investigator/move-sanity investigator (- (inc (investigator/sanity investigator))))
     (is (= (investigator/sanity investigator) 0))))
  (checking "Exceptional Course: Maximum Too Low" [investigator gen'/investigator]
    (dosync
     (dotimes [n (inc (investigator/maximum-sanity investigator))]
       (investigator/decrement-maximum-sanity investigator))
     (is (= (investigator/maximum-sanity investigator) 0)))))

(deftest stamina
  (checking "Primary Course" [investigator gen'/investigator]
    (dosync
     (investigator/decrement-maximum-stamina investigator)
     (is (= (investigator/maximum-stamina investigator)
            (dec (investigator/initial-maximum-stamina (investigator/name investigator)))))
     (is (= (investigator/maximum-stamina investigator) (investigator/stamina investigator)))
     (investigator/move-stamina investigator -2)
     (is (= (investigator/stamina investigator)
            (- (investigator/maximum-stamina investigator) 2)))))
  (checking "Exceptional Course: Too High" [investigator gen'/investigator]
    (dosync
     (investigator/move-stamina investigator 1)
     (is (= (investigator/stamina investigator) (investigator/maximum-stamina investigator)))))
  (checking "Exceptional Course: Too Low" [investigator gen'/investigator]
    (dosync
     (investigator/move-stamina investigator (- (inc (investigator/stamina investigator))))
     (is (= (investigator/stamina investigator) 0))))
  (checking "Exceptional Course: Maximum Too Low" [investigator gen'/investigator]
    (dosync
     (dotimes [n (inc (investigator/maximum-stamina investigator))]
       (investigator/decrement-maximum-stamina investigator))
     (is (= (investigator/maximum-stamina investigator) 0)))))

(deftest focus
  (checking "Primary Course: Speed"
    [investigator (gen/such-that #(not (= (investigator/maximum-speed %)
                                          (investigator/speed %)))
                                 gen'/investigator)]
    (dosync
     (let [current-speed (investigator/speed investigator)]
       (investigator/focus investigator {:speed-sneak 1})
       (is (= (investigator/speed investigator) (inc current-speed)))
       (investigator/focus investigator {:speed-sneak -1})
       (is (= (investigator/speed investigator) current-speed))
       (is true))))
  (checking "Primary Course: Fight"
    [investigator (gen/such-that #(not (= (investigator/maximum-fight %)
                                          (investigator/fight %)))
                                 gen'/investigator)]
    (dosync
     (let [current-fight (investigator/fight investigator)]
       (investigator/focus investigator {:fight-will 1})
       (is (= (investigator/fight investigator) (inc current-fight)))
       (investigator/focus investigator {:fight-will -1})
       (is (= (investigator/fight investigator) current-fight))
       (is true)))))
