(ns fractalify.users.api-routes)

(defn get-routes []
  ["/api/" {"users" {["/" :username] [["/reset-pass" :reset-pass]
                                      ["/change-pass" :change-pass]
                                      ["/edit-profile" :edit-profile]
                                      ["" :user]]}
            "auth/" [["logged-user" :logged-user]
                     ["login" :login]
                     ["logout" :logout]
                     ["join" :join]
                     ["forgot-pass" :forgot-pass]]}])
