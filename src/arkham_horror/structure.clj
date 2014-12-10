(ns arkham-horror.structure)

(defmacro get-path [item path]
  (if (seq path)
    `(when ~item
       (get-path (~(symbol (str (first path) "/get")) ~item)
                 ~(rest path)))
    item))

(defmacro update-path [item path function]
  (if (seq path)
    (let [active (first path)]
      `(~(symbol (str (first path) "/update"))
        ~item
        (fn [~active] (update-path ~active ~(rest path) ~function))))
    `(~function ~item)))
