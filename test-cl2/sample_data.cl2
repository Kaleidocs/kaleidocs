(def profiles
  (atom {}))

(add-entity! profiles
             nil {:id "Company A"
                :fields [{:name "FOO"
                          :value "Some text"}]})
