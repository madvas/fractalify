(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [fractalify.fractals.schemas :as fractals-schemas]
            [fractalify.users.schemas :as users-schemas]
            [fractalify.main.schemas :as main-schemas]
            [instar.core :as i]))

(def o s/optional-key)

(def db-schema
  {(o :active-panel)   s/Keyword

   (o :users)          users-schemas/UsersSchema
   (o :fractals)       fractals-schemas/FractalsSchema

   (o :dialog-props)   {s/Keyword s/Any}
   (o :snackbar-props) {:message              s/Str
                        (o :action)           s/Str
                        (o :autoHideDuration) s/Int
                        (o :onActionTouchTap) s/Any}
   (o :route-params)   (s/maybe {s/Keyword s/Any})})

(defn assoc-form-errors [db-schema]
  (i/transform db-schema [* :forms *] #(merge % main-schemas/FormErros)))

(defn create-db-schema [db-schema]
  (-> db-schema
      assoc-form-errors))

(defn valid? [db]
  (s/validate (create-db-schema db-schema) db))

(def plant1
  {:rules       [["F" "F[+F]F[-F]F"]]
   :angle       (u/round 25.7 2)
   :start       "F"
   :iterations  4
   :line-length 7
   :origin      {:x 300 :y 575}
   :start-angle 179})

(def koch-island
  {:rules       [["F" "F+FF-FF-F-F+F+FF-F-F+F+FF+FF-F"]]
   :angle       90
   :start       "F-F-F-F"
   :iterations  2
   :line-length 5
   :origin      {:x 300 :y 300}
   :start-angle 180})

(def koch-curve
  {:rules       [["F" "F+F-F-F+F"]]
   :angle       90
   :start       "-F"
   :iterations  4
   :line-length 36
   :origin      {:x 300 :y 300}
   :start-angle 167})

(def dragon-curve
  {:l-system {:rules       {1 ["X" "X+YF"]
                            2 ["Y" "FX-Y"]}
              :angle       90
              :start       "FX"
              :iterations  12
              :line-length 6
              :origin      {:x 300 :y 300}
              :start-angle 90
              :cmds        {1 ["F" :forward]
                            2 ["+" :left]
                            3 ["-" :right]
                            4 ["[" :push]
                            5 ["]" :pop]}}
   :canvas   {:bg-color   ["#FFF" 100]
              :size       600
              :color      ["#000" 100]
              :line-width 1}})

(def cantor-dust
  {:rules       {1 ["A" "ABA"]
                 2 ["B" "BBB"]}
   :size        600
   :angle       90
   :start       "A"
   :iterations  3
   :line-length 6
   :origin      {:x 300 :y 300}
   :start-angle 90
   :color       ["#000" 100]
   :cmds        {1 ["A" :forward]
                 2 ["B" :forward]}})

(def plant2 {:rules       [["F" "FF-[-F+F+F]+[+F-F-F]"]]
             :angle       22.5
             :start       "F"
             :iterations  4
             :line-length 10
             :origin      {:x 300 :y 550}
             :start-angle 180})

(def sierpinski-triangle
  {:rules       {1 ["A" "+B-A-B+"]
                 2 ["B" "-A+B+A-"]}
   :size        600
   :angle       60
   :start       "A"
   :iterations  6
   :line-length 6
   :origin      {:x 100 :y 500}
   :start-angle 90
   :color       ["#000" 100]
   :cmds        {1 ["A" :forward]
                 2 ["B" :forward]
                 3 ["+" :left]
                 4 ["-" :right]}})

(def plant3
  [{:rules       {1 ["X" "F−[[X]+X]+F[+FX]−X"]
                  2 ["F" "FF"]}
    :angle       25
    :start       "X"
    :iterations  6
    :line-length 6
    :origin      {:x 100 :y 500}
    :start-angle 180
    :cmds        {1 ["F" :forward]
                  2 ["+" :right]
                  3 ["-" :left]
                  4 ["[" :push]
                  5 ["]" :pop]}}
   {:bg-color   ["#E8FFFE" 100]
    :size       600
    :color      ["#060" 100]
    :line-width 1}])

(def default-db
  {:users    {:logged-user {:username "madvas"
                            :email "some@email.com"
                            :bio "I am good"
                            :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

              :forms       {:login {:user "HEHE" :password "abcdef"}}}
   :fractals {:forms          (merge {} dragon-curve)
              :fractal-detail (merge
                                dragon-curve
                                {:info          {;:title "Some such long title I cant even tell, how long th"
                                                 :title "Dragon curve"
                                                 :desc
                                                        "Normal description is this."
                                                 ;"This desc has about 140 chars long desc This desc has about 140 chars long ssome new lines N shit get it now This desc has about 140 chara."
                                                 }
                                 :src           "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAlgAAAJYCAYAAAC+ZpjcAAAgAElEQVR4Xu3dUZrbNrIGUHtn3llyd5ad+X5Mjx2nI8n6JRSBAs88ZtBA4aBAliVC/Pr9+/fvX/yPAAECBAgQIEBgmMBXBdYwSx0RIECAAAECBP4WUGBJBAIECBAgQIDAYAEF1mBQ3REgQIAAAQIEFFhygAABAgQIECAwWECBNRhUdwQIECBAgAABBZYcIECAAAECBAgMFlBgDQbVHQECBAgQIEBAgSUHCBAgQIAAAQKDBRRYg0F1R4AAAQIECBBQYMkBAgQIECBAgMBgAQXWYFDdESBAgAABAgQUWHKAAAECBAgQIDBYQIE1GFR3BAgQIECAAAEFlhwgQIAAAQIECAwWUGANBtUdAQIECBAgQECBJQcIECBAgAABAoMFFFiDQXVHgAABAgQIEFBgyQECBAgQIECAwGABBdZgUN0RIECAAAECBBRYcoAAAQIECBAgMFhAgTUYVHcECBAgQIAAAQWWHCBAgAABAgQIDBZQYA0G1R0BAgQIECBAQIElBwgQIECAAAECgwUUWINBdUeAAAECBAgQUGDJAQIECBAgQIDAYAEF1mBQ3REgQIAAAQIEFFhygAABAgQIECAwWECBNRhUdwQIECBAgAABBZYcIECAAAECBAgMFlBgDQbVHQECBAgQIEBAgSUHCBAgQIAAAQKDBRRYg0F1R4AAAQIECBBQYMkBAgQIECBAgMBgAQXWYFDdESBAgAABAgQUWHKAAAECBAgQIDBYQIE1GFR3BAgQIECAAAEFlhwgQIAAAQIECAwWUGANBtUdAQIECBAgQECBJQcIECBAgAABAoMFFFiDQXVHgAABAgQIEFBgyQECBAgQIECAwGABBdZgUN0RIECAAAECBBRYcoAAAQIECBAgMFhAgTUYVHcECBAgQIAAAQWWHCBAgAABAgQIDBZoU2D93//9382p//HHHzf/e9p+sOt/ukvjSdtXx6//uQJpPqTtq2eXxpO2r45f/wQIEEgFWhRYx8X2r7/++vLt27d/ze/Hf/tcZKXtU7S0fRpP2j6NR/teAmk+pO2rNdJ40vbV8eufAAECrwi0KbCOyd0qpEb891fgkr/58a/xrvEnc9V2vED3/Oke//gV1SMBAlcQUGCdsMpuMCcgbzxE9/zpHv/GqWVqBAgUCiiwCnF/dO0GcwLyxkN0z5/u8W+cWqZGgEChgAKrEFeBdQLuBYboXqB0j/8CKWaKBAgUCLQpsO495H6Y3Hr4PfnvR9t7pxFHmD96aDeJ8zC41b46/hEG+nhdoHv+dI//9ZXzlwQIXFmgRYF1LNCtY9v3Co5X/vtRpFUXWZ8T7ZU47xVk1fFfeZOsMHf5/+Xvk8Tyf4VsFAMBAs8ItCmwbk2m+1cP3eN/JsG0qRPonj/d469bWT0TILCDgALrl0/HKj/B2rFA3GEDdJ5D9wKle/ydc0fsBAjUCyiwFFj1WWaEEoHuBUr3+EsWVacECGwjoMBSYG2TzFebSPcCpXv8V8s38yVAIBOYVmDdemj3CD15t2D60OvIV3B0jz9LE61HC3TPn+7xj15P/REgQOCzwJQCKy100mPeaaH2qP2tlOkev20wV6B7/nSPf+7qG50AgasITCuwbhU1o74yqF68UXHe66c6fv3PFeieP93jn7v6RidA4CoCCqwXVtoN5gU0f/JToHv+dI9fKhIgQOAMAQXWC8puMC+g+RMF1h9//CsLfIJrUxAgsLOAAuuF1VVgvYDmTxRYCiy7gACBCwlMK7C8W/D+qz+O/Dv7R08vlPPTp5oe2khfqVSdP93jn54AAiBA4BICUwqsQ/bWMe/0RpL+TMPIFe0e/0gLfeUC3fOne/z5ivkLAgQIZALTCqxbYXb/6q17/FnqaD1aoHv+dI9/9HrqjwCBawsosAauvxvMQMwLdtU9f7rHf8GUM2UCBAoFFFgDcd1gBmJesKvu+dM9/gumnCkTIFAooMAaiOsGMxDzgl11z5/u8V8w5UyZAIFCgeUKrFmnC289tHu4j3o34tHXt2/f/rWU6UP9aTwj2xfmoK7/JzDzdJ78/1gEp3dtRwIERgksVWAdk5pxOunRje0oij5fdKtvhOnpyJHx35rvqGTTz+8F5P/9ny/58Y+vZD+m+1f+/z5HtSBA4DmB5QqsW2FXf/Wg/w91v6z93KY5u5X8fJyf1T5nr7fxCBDYQ0CB9aCwqL5wd+l/j1TvO4suebJrnH0zR+QECMwUUGApsH7mn0+wZm7F+2PvWrh0mdeaWSEqAgRWF1BgKbAUWIvv0i6FyK5xLp4ewiNAYFGBNgVW5enC9DTf1dofuet01bwdPPNQxTHrd0+/dt8v8n9e7huZQGeBFgXWAVx9usqNJD+91Tnxu8Uu/x/nZ/X+dbqw244RL4H5Am0KrFtUu34l0WVe89P32hF0yZNd47x29pk9AQK/E1BgeQbrt89gefj9d9tozv+/a+HSZV5zVt2oBAh0EVBgKbAUWF1266c4uxQiu8bZNG2ETYDASQIKLAWWAuukzTZ6mF0Lly7zGr2e+iNAYC+BaQXWrYd2D9qV3v3X/fRT9St39toK585G/q/30Hr6yqlzM8ZoBAh0E5hSYKUXMsfUP9JqVMGXFrKP2ndL+BXilf9j8zndF/J/hV0gBgL7C0wrsG5d5Lp8NdA9zv3Teu0Zds+f7vGvnR2iI0BgFwEF1gWfwdolebvOo3uB0j3+rnkjbgIEegkosBRYvTJ2g2i7Fyjd498ghUyBAIEGAgosBVaDNN0rxO4FSvf498omsyFAYFWBaQWWdwuOe2h95EO+qybqTnE5tPGxmmnejmp/jO3dmjvtKHMhsKbAlALroPButXk3mB/FrZvMvE0p/+X/vOwzMgECZwhMK7BuTa77Vw/d4z8j4YxxX6B7/nSPX24SIEBgpIAC64LPYHm34MgtNK6v7gVK9/jHraSeCBAg8OWLAkuBZR8sItC9QOke/yJpIAwCBDYRUGApsDZJ5f7T6F6gdI+/fwaZAQECKwncLbDSd6WNmNTOp6u+ffv2n5NLq70y5VjDXR98T/M5bS//Hz+0Lv9HZIg+CBDoJPD1zz///P75ppre+EdOeMfTVY8Kl/RGXu1z60Y4cn1n9JXmc9p+5Jyq1/eI9VjjX/836ucP7vUj/0dmiL4IEOgicLfAunVRnPVwtK8eHqdTtU+XZL4XZ3ef6vir+6/On+7xV/vonwCBOQIKrBOewapeWjeYvQvQ6vWt7l/+VwvonwCBFQUUWAqsn3k56xPK6o2hgPgQrnaYlT/V86rOT/0TILCngALrhBtPdeq4wfgE61eB6nxI+5f/1QL6J0BgRYGv3759+/7uQ6/HxCpPn3U/Xdjdpzr+6o1RnT/VPtXxz3zIfcTaV/tUr+8IA30QILCewJAC64x323U/XVV9Oq/apzr+6q3R3ac6/sP/3X9oPSrUqvOn2qc6/ur81z8BAucLDPmKcNdnL9KvQtL21cudxpO2r46/uv90vmn77vGn803bd/epjl//BAj0FlBgnfAM1q4FaO/Ur3/ou9onLWhWa9/dpzp+/RMg0FtAgaXA+pnB6Q24d+orsH6sX7ruo9pX58+oOGf9A6naR/8ECNQKKLAUWAqsP/741y4bdWOu3br1BeIoh1kFSvf4q/NH/wQI1Arcfch9xLvDHoV+66HUo/2904jVD7EeYycP+Xb3qY6/Nm3/KS4+jzMqf6p95P/jDKn2qV7f6vzXPwEC6wvcLLDSQudR+1sEj45Vpxe+tDAa0b67T3X81WlfnT/VPtXxz/zZhbQwmnF9qF7f6vzXPwECPQRufkVYHXr1R/fV/Xf3qY6/un/r+yFc7eCrvepM1j8BAjsLKLBeuFFVJ0T1jbM6/ur+u/tUx1/dv/WtFtA/AQI7CCiwFFjt8lgB4ROsX5M2zYd2CS9gAgRaCiiwFFjtEje9oabtq0HSeFZr392nOn79EyBA4BCIH3IfwfboId+j/+Q034z2x5id371YHf+IHHnUR3X+VPtUxz/zIfcRa1/tU72+Iwz0QYBAf4HoZxpGTne1n11IC7Xqd5NV+1THPzJXbvXV3ac6/jSf0/bV+VPtUx1/df7rnwCB9QWiHxqtns5qX4Wk8XT3qY6/uv90vdL23eNP55u27+5THb/+CRC4loAC64VnsBxfX3OTpAVB2r561mk8q7Xv7lMdv/4JELiWgAJLgbVNxlcXHNVQ1fFX99/dpzp+/RMgcC0BBZYCa5uMV0B8LGW1g09wt9kyJkKAQKHA3YfcjzE/n+Y7/lvyrre0ffXpp+r+H813xBo+Ol2VvmJox4d8Z54+u/VQtvwfkfX/9CH/x3rqjQCBWoGowDoKlBE38uobYXUh9aj/6sJlxI28uhCsTdnHvc84fTbyxn/rHzYz8zmNR/7PzH5jEyCwkkD0FeGsrx66j7vSgl8xlu750z3+K+acORMgQECBdcIzK9JsrkD3AqV7/HNX3+gECBCYI6DAUmDNybwTR+1eoHSP/8SlNhQBAgSWEVBgKbCWScaqQLoXKN3jr1pX/RIgQGBlgfgh92Myq78rcLWHgg+zyncXrpxgK8S286GKDvtR/q+wC8RAgMDZAgqsL1++nFGQVZ+uOjtxuo1XfbqwQ6FzRp7fc5D/3XaMeAkQeFfAV4QnfEU464cZ302O3f+++1dv3ePfPb/MjwCBawsosBRYl90B3QuU7vFfNvFMnACBSwgosBRYl0j0W5PsXqB0j/+yiWfiBAhcQkCBpcC6RKIrsOa9o9BX5JfdYiZO4NICHnJ/8SH3Ea8MunTmvTn5Ea8MmvnQ9zH9zqdx5f+bCezPCRDYXkCB9UKBdWTFqJdeb59hBRP07r8P1FkFovwvSGpdEiCwnYCvCF/4inC7LGg2oe7PHnWPv1m6CJcAAQJTBBRYCqwpiffOoN0LlO7xv7N2/pYAAQJXEVBgKbDa5Xr3AqV7/O0SRsAECBCYIKDAUmBNSLv3huxeoHSP/73V89cECBC4hoCH3F94WPhIDe8WnLdBvFvww37Fh9znZYWRCRAgsJaAAuvFG5V3q81NZO8WnFdgHYWd/J+b/0YnQGB9AV8RvvAVoR9OXDOxu3/11j3+NbNCVAQIEJgjoMBSYM3JvIJRuxco3eMvWFJdEiBAoK2AAkuB1TZ5PwfevUDpHv82iWQiBAgQGCCgwFJgDUijNbroXqB0j3+NLBAFAQIE1hC4+5B7+q6xYzqd3602Iv6jj1Gv0EnftVedTmk8afsR8Ttd+KG44unCNB/S9iPy51EfaTxpe/FXC+ifwPkCf3+CdWvYpFCYdUFfcdy0MB3RvjptRr77r/r0mdOF8wqse6cLO+XPrb0k/n8K9+r9W30t0z+BMwW+fv/+/WaBlQTR/auN7vEna/VK22qfV2JK/qY6fv1/rMYshyQXXmlbPa9XYkr+pnv8yVy1JbCSgAJr4o1h1IWvOqFGxTnr5y2q49e/AuvXPZjmg/1bLaB/AnMEFFgKrN9mXnrDSNv/NoA3G6TxaD+3YEr930yP3/55Gk/a/rcBvNkgjSdt/2Z4/pzAtgIKLAXWb5M7veCm7X8bwJsN0ni0V2C984lUmj9vpvdv/zyNJ23/2wA0IHBRgWEF1o8HXH91XPEh9CO+3U47HnOqfDfio4d8R3h2j1+ef+z6WQ7d80f8F737mvb2AkMKrEPpx796FFhzCrjq0z3V69s9/hGF5qwCZYdxu+eP+Le/15rgBQWGFVi37NKPmrUf+9VMdT5Xr1f3+Kt99P94v3TPH/FXC+ifQK2AAmuDZ7B2PZ1Xm/rzflZAYTT2HxLy/7WdUp2Hr0XlrwjsI6DAUmC9nM3dL9DV8ev/nEJKgfXaFq7Oz9ei8lcE9hFQYCmwXs7m7hfo6vj1r8D6dXOl+fDyxnzyD9N40vZPhqEZgW0F7hZYtx5qPhS8Quec01LVr9CpXt/u8R+5vttp004Ps3fPH/Fve880MQJPC9wssB4dy08vHG5UeUGWFrKP2t/KhOr17R5/p0Jkx/3VPX/E//T9R0MCWwvcLbBuXSTSj4i1f+0rkuqMq16X7vFX++j/8b7onj/irxbQP4EeAgqsBZ/Bqk6d6ht89/irffSvwHpnj1Tnzzux+VsCBP4RUGApsH5mw6gLd/UGGxWnfl77hLXarXv+iL9aQP8EeggosBRYCqw//vjXbq0uIPTvE6x3bg/V+fNObP6WAIEnPsHybsH84fRRD0cfy+Pdgve36aOH9I+/cvrvr7/xujrI/8e3qOr8r/Z3AyZwFYHoZxpGFRD6+UivRw7eTfb7m8znFvLq93nVpfCS/3Pzv9r/KjdY87y2QPRDo9UfTevfVyfvbEf58zh/uvu8kxvP/G21zzMxvNOme/zvzN3fElhRQIG14DNY9y6U1QnU/QJdHb/+5xZw8v+5T7U+P14wKm+r/fVPYDcBBZYC62dOj7oQ71ogVvvo3ye479xgqvPnndj8LYErCiiwFFgKLKcI/86Bqk8+Rt34qy/Qo+Lc9R8Y1f76J7CbQFxg3TtdmL5CJ21/wHc9FTXq4etbN8EfCfnjov45QVd6d2T3+Eeto34+sjR16J4/4v/n6pRer3a78ZrPNQSiAusgSTfGiPbphXjn9mlhmravLmTTeNL21fHrPy+MRu7HNB/S9tXrm8aTtu8ev9OL1yg8rjLLuMCaAVP90b3+P1aVA4df97d8kA8r5MOMe44xCYwQUGApLH7mkRuqG+oKN1R5KA9H3Nz0QWC2gAJLgaXA8pD73zmw+kPuCq9rFl6zb5LGJ/CqgAJLgaXAUmApsFwHlr0OvHpz83cEZgu0KbC8G3Huw8VHoq50irP64d/q/lfzTOOp9qnuP52v9vOuP7c+XZ194zQ+gWcEWhRYx0RunUYceTrJBXTeBTRdx0cX3BF5Ut1/Ot/V2lf7VPe/mqd4Pm5VM9/N+szNUhsCqUCbAuvWxDyT8aFyNYc0yVOf6v7TeFZrX+1T3f9qnuJ57TqW5on2BM4WUGBdsEDpfkFPN0k63+r+03hWa1/tU93/ap7iUWClOa99DwEFlgLrZ6Z2udCnWyudV3X/aTyrta/2qe5/NU/xKLDSnNe+h4ACS4GlwPp0ijDdule7QVb7VPd/tfXadb5pnmhP4GyBaQWWV+j0eah85kO46asz0ofcq/s/NvRKpy/TeKp9qvtP56v9Odel9JRomidn30iNR+CWwJQC67gJjnpptAviORfEGc7HmPdeVn0rmR/l1a34q/ufWZiOWK9qn+r+u/vvGv+jdU//4e22TmBlgWkF1q1NtutH2eb1sQVSh3TjrNZ/Gs9q7fm/lrerreNq8aR5pT2BrgIKrBdu/KtdsHaNJ91UqUN1/2k8q7Wv9qnufzVP8aQrrj2B3gIKLAXWzwxe7QaQbq00/ur+03hWa1/tU93/ap7iSVdcewK9BRRYCiwF1pN7+Go3yCdZXs6f6v6vtl5d5puuu/YEugpMK7C8W3Dfh9NHPZx7bKpRD7mPOIV0tcMZ/D8u66PyWT//3CaTfd315ipuAlMKrIM9PU5//E3n4+7if+1GlRZG1aeQ0v675zn/1/LWfvduQeUFgWkF1i36Lh9xi/Nj9WY5dN+2s9xGjct/bv6PWsfV+umeV+In8FlAgTWxUFjtAtclnu7buIvzvTj5K7B+zYFR+dw9r8RPQIF147meURcI/Zxz4+m+jbvnCf9z8rx7nqTxd88r8RNQYCmwpn21l15wfYKy5o28+2V0VB7qZ2x+ds8r8RN4usBKH+YdQfvolNbRv4fc//qb+UoO6UPWI/JwZB9Xe8h9pN2Ivrr7j9rvI979V319PubqdOGIrNfHKgJf//zzz++fkzo9jj5yMi6ITi39mk+dL7jVN6TqY//db3jd/Uet76N1TP8hXX197v4PqpH3Qn31F7hbYN3alLO+svFR/NiP4rt4dt9eXZxn7evq9e3uPyr+7s7V8eufQJWAAsspwp+5NeqCPqqfqqQ/q99RDrP6OcupapxZbquNW+X7o9/q+VbHr38CVQIKLAWWAqtod1XfeKr7L2I5rdtqny79V4NXO1THr38CVQIKLAWWAqtod1XfeKr7L2I5rdtqny79V4NXO1THr38CVQJfv3379v3dU2lHcJUPI3tY9WP5Rz30ekY/6amlY36f87A6rx5tqvTh31t9zcxb/h9vGvDO04/M7Hx9ro6/6uaqXwJDCqwfF7HqTfx5uc4oFG7d+I37uOB7dEFMTyHNOFX06MacxpPOd0S+8f/nSjHDf8XrQ5q36a2x2rk6/nS+2hN4RmDIV4SzTiFVfzSt/48USh2eSbxf21T3v1o86XzT9qvNd7V4Us9d26frkravdkvj0Z7A2QIKrBcKiOoLR/f+0yRO55v2n7avjiftP22/2nxXiyf13LV9ui5p+2q3NB7tCZwtoMBSYP3MuVEXxDSJ03HT/tP21fGk/aftV5vvavGknru2T9clbV/tlsajPYGzBRRYCiwF1qddl94Y0k2b9p+2r44n7T9tv9p803i6tE/XJW1f7ZDGoz2BswXuPuSenkJKH0L8sfk+T/jeg/K32q/4MOkxn3dPZXaa14h1fzTftP90A6V5lcaT9p/mT3U8af9X80/Xa1T77tfn6vjTPNSeQIXAzQLrGCgpdB61vxX0cdO5d3w63XijLlj6yX8GYuS63/JP+083yKM8HBFP2n9aWKc+aTxp/1fzT9drVPvu1+fq+NM81J5AlcDNrwirBvvRb/VHx/r/kK52SPMkjSftP21fHU/af9p+tfmuFk/q2aV96py2r3ZI49GeQFcBBdYJhUj1BWtW/2nSp3Gm/aftq+NJ+0/brzbf1eJJPbu0T53T9tUOaTzaE+gqoMBSYP3M3fTCmiZ9df+rxZPON22/2nxXiyf17NI+dU7bVzuk8WhPoKuAAkuBpcD6449/7d97N5h0k692o0rjSeebtq+OJ+2/S/vUOW1f7ZDGoz2BrgLxQ+4jJnpsYO8Iyx8qH/WQ7Kh+jlxIXo/0aN2rT6vdyts0Dx/N98dN6ddxRjnf64f/P9oz/KvXd9S6p9fsdF+kDmnepvFrT2AVgehnGkYGfaULYnoB6tQ+LYxurfvMC26ah6udcuV/zZc6p+ueXrvTfXH0n/w8TXX86Xy1J1AhEP3QaEUAv/ZZ/dG0/j+0qx2q86S6/2qf6v6rfar7r/bZtf/u61Idv/4JnC2gwDqh4Nj1gj7qWaWzk/5343Vfr9/Nb/X/v7v/rPir17V6XtXx65/A2QIKLAXWz5wbdQE9O4lHjzfKYVY/oz3O7m+WW/dxq9ep2qc6fv0TOFtAgaXAUmB92nXVN5Lq/s++iIwer9pn1/5Hr8Pn/qrdquPXP4GzBe4+5H4E8vmhxeO/jXqFTvVDlLfi7/TweJf4uz+s2j0P+fc/jTvquvTo+jzixvLodGF6+KN73o7w1Mf+AlGB9eOnFT4XWSM3XpfCQpwfmyP5mYbVttOjvO2wvvw/MmpUgbJDP9WFS3oKOG2/2jVCPATeEYi+Iqz+iFj/H0vZxeGdxFvhb7s4O0zQa1+sllcr7DUxELiigAKrUUHjwj12i67mmcYzVuP83tL5av9aoXn+yhqRAIFDQIGlwPq5E9IbWPctlM53tfb8Xys4VlvH6ni654n4CXQVUGApsBRYT76LsPpGmPbf9aLzI+50vtq/VlB2zxPxE+gqED/kfkw0eSWC9vs+hHus7ahTpTM20MzDGempq1v76JH/DM90zO7+jx6KH7G+ox66754naV5pT2AVAQWWU0g/c/GVC3p6I6k+5ZRurPSU04ifdUgL0/RGnhrMbN/dPy18R+TPqH06c92NTeAKAr4i9BXhy18RjvrKpvtGSx3S+Vb3n8azWvtqn9X6T+PZ9RTqankoHgKfBRRYCiwF1pvXhfSGlw5X3X8az2rtq31W6z+NR4G1WsaK5yoCCiwFlgLrzd2e3vDS4ar7T+NZrX21z2r9p/EosFbLWPFcRUCBpcBSYL2529MbXjpcdf9pPKu1r/ZZrf80HgXWahkrnqsIeMjdQ+4/c/2Vh2ePP05Ola72kHu60dOHlNP5Vvefzne19tU+q/V/b391P1yyWl6Jh0CFgAJLgXVagXUMdKV3F6bzPW7uP973+etmv1f4pv1XXEDO7LPaZ7X+X1n3WwXi1fLkzJw0FoFHAr4i9BXhaV8Rdt+K6Vcz6Xyr+0/jWa19tc9q/ftqb7UMFA+BTECBpcBSYD25Z1a7AT8Z9jbNuvun8SuwtkldE7mogAJLgaXAenLzpzfIJ7t92T/tv3v77v5p/Aqs7hkr/qsLKLAUWC/f4NMbRvfNVj3f6v75Pxao9k/7V2B1z1jxX13AQ+4ecv+5B6pPER4DXendhenF5dFD1ulpxHTsDu2rfVbr/148j/ZRh3UUI4GrCCiwFFinFVg/Tsh9LrKqb2wjN3P1Ka3q/kdazOir2me1/m/Fc28fzVgPYxIgcF/AV4S+IjztK0JfkbgUEXhfwFeH7xvqgcAZAgosBZYC64ydZgwCgwQUWIMgdUOgWECBpcBSYBVvMt0TGCmgwBqpqS8CdQIKLAWWAqtuf+mZwHABBdZwUh0SKBG4+5B7+q6rI7rknXTaf/n7tSgcHjs4PVey73XaRODeQ+63rhvHfxt1Srf6Yf8m/MIk8JbA359g3eoh2agKhQ9BDmMdHt0w3sp6f0yggcCj07X3/mGW/sN4RPsGlEIkMEXg6/fv328WWEk0o06H6edDnUOSfdoS2FOgy3VgT32zIvC+gAJLQfMzi1a7oL+f3nog0Fdgtf3o2a++uSTyOQIKLAWWAmvO3jMqgYcCCiwJQqC3gAJLgaXA6r2HRb+pgAJr04U1rcsIDCuwfry+4Vc5D31/aFzRIX149nD6fAr1+G/3DltcZoeaaEuBEafwXnnI/dY+qr7+2KctU1TQJwgMKbCOOJPjxNUbXv9zC7tHF9w0T/xMwwlXARvpKrYAACAASURBVEMMFRj5bs10v8wosLwbcWj66GwjgWEF1i2TLh9xi/Nj9UY5pPsjHTftX3sCZwpU53Pa/6z2Z5obi8CKAgqsgYXFrAvZauOmiZ7Gn/avPYEzBarzOe1/VvszzY1FYEUBBZYC62dejroQp4mejpv2rz2BMwWq8zntf1b7M82NRWBFAQWWAkuBteLOFFNbgbSgSSea9j+rfTov7QnsJnC3wEpPwXR5GPNYQO9MHPcOxPQh9DRP0v5326Dms7ZAdT6n/Y+6vqWngO3TtfNUdHMEbhZY6SmYLseJnS78SLJRDkdfyc8opHmS9j9nCxn1qgLV+Zz2f8a+Tv/hfdXcMG8Ch8DdAuvWzW3WR83G/UjW1RzSLZTGn/avPYEzBarzOe1/VPszDY1FYGcBBdaChcuoC2V1P+nGSONJ+9eewJkC1fmc9j+q/ZmGxiKws4ACS4H1M7/TC3S6Mar7T+PRnsA7AtX5nPY/qv07Jv6WAIF/BBRYCiwFlisCgRcE0oImHSLtf1T7NE7tCRC4LRA/5H504xTeuFN4nT2P2Ec95O4UkktUN4H0IfRH+2XWacFb1590X3dbN/ESOEsg+pmGUadU9POxvDs4pIWRU0hnbW3jnCGQFkbpzx/M+gdYuq/PsDYGgW4C0Q+NjvoIWj8fabKrQ7dNIF4CIwXs65Ga+iLQV0CBtXGhM+tC33c7iJzA+wKz9l31uO/L6IHAtQQUWAqsnxk/6gJ9rS1ktgT+LTBqH63Wj3UmQCATUGApsBRY2Z7RmsBDgdUKo1HxWHYCBDKBuMA6Hsy+dYowfXgzbX9My+nF9U4vehg223Ba7yWQPuTe+Tp2xH7v1HB6eCVtv1fWmM1VBKIC60BJN8aI9juctut8Yb3n/+iCe5UNZJ7XFUh/pmGH61j6D+MR7a+bYWbeXSAusGZMeNRH3Pr5WL1RDjNywZgEVhEYtY/08/i6tMp6i4NAKqDAGlhwXO1CmSab9gR2Erjafp81351yxlyuJaDAUmD9zPj0AnqtrWK2BP4tkO4X7V/7BF3eEegqoMBSYCmwuu5ecU8VUDC9VjClblMX2eAE3hBoU2DdO714zH3G6cL04c00zur+03hutT/+26hTRW/ksD8lMEXg0UPuHfZvl4fuH11npiy8QQk8KdCiwDrmstJx6LSwSC9k1f2n8Txqn95I/KzDkztTsxYCTkmf805V140W20GQnwTaFFi3Vi79qHlU+zSL0nGr+0/jqW6fzld7AjsJVO+vXfvfKQfMZU8BBdYLz2ClqZBe4Kr7T+Opbp/OV3sCOwlU769d+98pB8xlTwEFlgLrZ2bPuhDvubXMisBzArP2Xfdxn9PVisA8AQWWAkuBNW//GZnAsB/+7V4wpfFLHQKrC0wrsLo8HJo+XJk+jF/d/5GAM05Z3hs3ne/qG0h8BBKB9Pqw2v4dFY/DMUnWaNtVYEqB1eV487Go936G4NaCP5rXrQtTdf8jTwuOuLCm8+26qcRNYMT1YbX9OyqeR9eB9B/eMo3AygLTCqxbmyz9iLi6fbpwaTzV/afxVLdP56s9gZ0EqvdXl/53WlNzIfBIQIH14BmsNHXSC1x1/2k81e3T+WpPYCeB6v3Vpf+d1tRcCCiw/ieQXoDS1Fmt/zSe6vapp/YEdhKo3l9d+t9pTc2FgAJLgfWvHJh1IbYVCVxZYNa+W23cK+eAuV9LYNpXhKu9W/BY9s+n7Y7/Nuoh9xGn57ocDrj3MOwjTw+3XuvCc8XZdt+/ZzzkfsW8MOd9BaYUWAdnl+PKaWFUXSik/a/m7Hj2vhcTM/u9QPf9e+sfoq8UXul19feyWhBYT2BagXWLYrWPsu/Fs94yZhF1cd7VP1strQn8W6D7/rWvZfRVBBRYJ/yS+2rJ1P0CvZqneAicKdB9/yqwzswWY80UUGApsH7mX5cL98wNY2wCswW67NM0ztmuxicwWkCBpcBSYI3eVfojUCiQFi5d2heS6ZrAFIG7BVb6MOaI6B+dsjn6n/FOve4PY672kHu6jt39R+yLHfpIrydp+x2Mbs1h1P4dcbik+vp8zD85tb3rmpvXPgJf//zzz++fkzo9TjySY9QFJb2R32rffcNXXxBfOT2UrEt3/5H7onNf6fUkbd/Z5lHso/bvo32UFrLV12f/oNo1m685r7sF1q1NOevhxFkfcXdPiVluo8bt7i/+D4FR+TDr+jNrHUe5Vcc/Ks6rrW/1uuh/voACa+C7COcv578jqL7wVfe/mqd4XhOQJ3PdXhv9+b+yvs9baXktAQWWAutnxldfKNP+r7UV951tuu5p+13lUodZnwCNinNW/Lvmj3nNF1BgKbAUWPP34dYRuAG/tryj3F4b/fm/GhWnAut5cy17CHz99u3b93dP5x1TrTz98crDnumpmWMO776LcOSSpw+f3hr7FbdbDq88zN7df+RaXr2vV/IwyZ/q688r67fS/q32eWV9k+tMdfyvrK+/IfCMwJAC68eLm6uLrM8TGvVS4UcFxIxTLY8uWGk8ty70rxRMoy6IaTzpfJ9Jem3OF0jW/dEN9V4/K+XJivu32idZ31euP9Xxn78jjHgFgSFfEc76aHfUuOlH3NWJUR1P2n/aPvWp7j+NR/tzBKr37zmz+O8o1fmc9j/KOfUcFees+NP5ak/gs4AC64Vj5NVplF6Y0njS/tP21fGk/Wu/psCoG+eofkYprbZfZvmkDmn7UeulHwJVAgosBdbP3EovcKMu3Om4VZtBv+cKVOfPubP5Z7TqfE77H+Wceo6Kc1b86Xy1J+ATrBs5kF4IqtOoOp60/7R96lPdfxqP9ucIjLpxjupn1Kyr8zntf5bPqDhnxT8qH/RzXYG7D7knp3heecj9x6b5TH/vQflb7UeNO/Mh93vzOlxune5MH/ZM+7837qx40vledyufO/PV9u+sPEn3Vxpnct072v64Jv6aDaOuk0ef6fU5uW6MjP/c3WA0ArcFbhZY6UZ61P7WsOlGutd+5Li3LgRp/2mSPXIYEU/af3q6J/VJ40n7T/21f01g1v49ok0Lu9dm+NxfVefzK9e9ET6j1nfUKW/XgefyUav1BG4+g1Ud5qiPjtM403HT/tP21fGk/aftV5tvGo/2rwmkebLrVzypQ6o9yy2dV9o+ddCeQFcBBdbEX3KvvjCl/aft06Sv7j+NR/vXBNJ1nFUovDa75/8qdXi+54+Ws9zSeaXtUwftCXQVUGA1upClSZZe+NL21fGk/Wt/jkCaJ7MKhWqN1CGNZ5ZbOq+0feqgPYGuAgosBdbP3K2+UFb333UTdos7XcdZhUK1a+qQxjPLLZ1X2j510J5AV4H4IfcREz025L3TLkf/z74b8WibvJ7n0bjp6Z4ZDo/m++Mi92tc6UPrafvu/iPW8Ip9zNq/q1mnDqP2b7rvUrd0XtXXjTR+7QmsIhD9TMPIoEcVBGlhdGvc6gvWI7fUIf35jKRgTS+UZxz/Hplz+honkObtvTxM9++4GYzpKXUYtX+r3dJ5pdeZ6vjHrK5eCLwnEP2S+3tD/f6v04+aZ32E/vuZvNdilMOsft6bvb/uKjAq37rO/0fcoxzSfqrd0njS9tXx65/A2QIKrLPFnxgvvTCt1v6JKWqyocCoPOxOM8oh7afaLY0nbV8dv/4JnC2gwDpb/Inx0gvTau2fmKImGwqMysPuNKMc0n6q3dJ40vbV8eufwNkCCqyzxZ8YL70wrdb+iSlqsqHAqDzsTjPKIe2n2i2NJ21fHb/+CZwtcPch9yOQz6f5jv+WvIsqbX/GQ9ZnA/9uvOqHSW+t4yvO9/rxsOrvVvg6//+Ry+np4O75s9L+fXS9HZGFj9Y3fXi/+7qP8NTH/gJRgXXv1NjIjZcUBNUXlOrlf+WGlPiMLKTSgrvaTv9rCiQFh/37sYYj92l14XJrfdN/SHdf9zV3nqhWFIi+Ikw/8q1uvyJoElO1T3X/yVy1va7AvTzsLlK9v0b1391Z/AS6CiiwJq7cqAvorH4m0hm6kYAC62Ox7NNGSStUAgMEFFgDEF/tYtYFd9S4r87b311LQIGlwLpWxpstgQ8BBdbETBhV6MzqZyKdoRsJKLAUWI3SVagEhgnED7kfIz/7rsCRD2/eGvf4b6NONQ4TDTqadTjglcMKqX/AoOnmAvfyfOb+TR/WvrVEXQ6pPHLePPVMj8BUgdYF1iuFQvUpm3Q10wt9ckrrXoGb3tgeFcqreab+2p8jcC9vZxzvT/9h80hoxH6s/ofoK+8MPScrjEJgb4HWXxGO+mqs+xKnDul8q/tP49F+D4E0r0Z91ZiOm2qn/c9qn85LewIEMgEFVua1ZOv0Ap1Oorr/NB7t9xBI80qB9bHus9z2yDqzIHCegALrPOuykdILbhpIdf9pPNrvIZDmlQJLgbVH5pvFVQQUWBusdHqjSqdc3X8aj/Z7CKR5pcBSYO2R+WZxFYH2D7kfC5Wcauz+UHb6UG063+r+r7Kxdp/njMMZrzysXZ3Paf/p9epe+xmHA3bPafMjMFrgUgXWgXfvZx1Gw1b0lx4LT+db3X+FiT7PF0hP4aV59crp11sK6bjV+2XUacFHcaaF7/nZY0QC1xG41FeE3Zc1/UolnW91/2k82q8pkObJqPapRjpudf9pPKO+Ek3npT0BAmMEFFhjHE/pJb1Ap0FV95/Go/2aAmmejGqfaqTjVvefxqPASldEewJrCSiw1lqPh9GkF+h0atX9p/Fov6ZAmiej2qca6bjV/afxKLDSFdGewFoCCqy11kOB1Wg9rhrqqEIh7Sf1Xq3/NB4FVrri2hNYS8BD7v9bjw4Ph6YPF6epVt1/Go/2awqkD4/Perg7HffQTt5tmvY/qv2jONfMGFERuKbApQqsK767ME3rDoVmOiftxwvcypNRBUT67suRBV/68weHbPIzMaPapz+/Mj4D9EiAwO8ELvUVoY/of5cO/n8CrwuM2l/6+ViD1OH1lfOXBAhUCCiwXMgq8kqfFxRICwLtXyukPJt1wc1lyi0FFFgKrJaJK+j1BBRMYwum1HO9jBARgWsLKLAUWNfeAWY/TCAtCLQfW5ANW0gdESAwROByD7kfaslDqR4mHZJnOrmAQHoKdWT7dF+n7dOH39P2aTy32h//rfOrwC6wRUzxYgIKrC9fvox699nFcsd0CfxHID2FOqJ99enFR4XLavH7B6FNSWAdAV8RPviKcJ1lEgkBAvcEqr9qrJbvHn+1j/4JdBVQYCmwuuauuAn8LdC9QOkevzQkQOC2gAJLgWVvEGgt0L1A6R5/6+QRPIFCAQWWAqswvXRNoF6ge4HSPf76FTYCgZ4C2z7kPuIUz7GkTuX0TGxRX0dg5KtyDrXPp4yrrwPd479OppkpgUxgywLr0QUxfYeaUzlZQmlNYIZAuq9vFVLpOxBHzrN7/CMt9EVgF4EtvyJMFyf9iD7tX3sCBM4XSPd12r56Rmk8afvq+PVP4OoCCqwXTiFdPWnMn0AHgbTgSNtXG6TxpO2r49c/gasLKLAUWFffA+a/qUBacKTtq9nSeNL21fHrn8DVBRRYCqyr7wHz31QgLTjS9tVsaTxp++r49U/g6gLtH3JPH0JPHyZN+796Qpk/gREC1a+gSU8Zp9eB7vGPWEN9ELi6QOsC61i85GcU0uPQaf9XTybzJzBC4NE+TQujI57kZxfSwujWfLvHP2IN9UGAwJcvrb8iTBfQR+ipmPYEzhdI92navnpGaTxp++r49U+AwBgBBZZfch+TSXohMEggLTjS9oPCvNtNGk/avjp+/RMgMEZAgaXAGpNJeiEwSCAtONL2g8JUYFVD6p9AcwEFlgKreQoLfzeBtGBK21d7pfGk7avj1z8BAmMEPOT+5cuX4xUZ6SmhMfx6IUDgs0B6GOXRK26OvpOH3EesRvf4RxjogwCBL19aF1ivFEYjTglJHAIEagXSn1O5VUh5t2DtGumdAIHHAq2/Irz30bpFJ0BgP4H0q7S0fbVYGk/avjp+/RMgkAkosDIvrQkQmCSQFhxp++pppfGk7avj1z8BApmAAivz0poAgUkCacGRtq+eVhpP2r46fv0TIJAJKLAyL60JEJgkkBYcafvqaaXxpO2r49c/AQKZwOUecs94tCZAYBWBR6fz0lfozDg13D3+VfJAHAS6CLQusA7k5F2EXRZFnAQI3BZITwGn7avd03jS9tXx658AgecFWn9F+Pw0tSRAgAABAgQInCegwDrP2kgECBAgQIDARQQUWBdZaNMkQIAAAQIEzhNQYJ1nbSQCBAgQIEDgIgJ3H3JPT+UcXp/f+ZW+Iyxtf4x57yF3D4deJINNkwABAgQILChws8BKC5e0MBrZPi0EZxzPXnDdhUSAAAECBAgUCtz8ijAdL/1BvNXap/PVngABAgQIECDwSECBJT8IECBAgAABAoMFFFiDQXVHgAABAgQIEFBgyQECBAgQIECAwGCBuMC6dTpv5EPrx/wqTyN6yH1wBumOAAECBAgQ+I/A1+/fv39/1uXRy0qrC6MR/R99eHfhs6utHQECBAgQIPCqQFxg3SpSVjsVeC+eV5H8HQECBAgQIEAgEVBgJVraEiBAgAABAgSeEFBgPYGkCQECBAgQIEAgEVBgJVraEiBAgAABAgSeEIgLrOPE4K1Tfukra47YKk8L3ur/+G/eXfhEVmhCgAABAgQIvCUQFVjHSOlLlFf7WYe0EPSzDm/llz8mQIAAAQKXFIgLrBFKXU4dOo04YrX1QYAAAQIEriegwPrlU7nPXx8qsK63IcyYAAECBAiMEFBgKbBG5JE+CBAgQIAAgV8EFFgKLBuCAAECBAgQGCxQXmCt9pD74ZecXvSQ++CM0x0BAgQIELiAQGmB5d2FF8ggUyRAgAABAgT+I1BeYB0jPvvw+GqnC+ULAQIECBAgQOAVAQXWg2ewXgH1NwQIECBAgAABBZYCyy4gQIAAAQIEBgsosBRYg1NKdwQIECBAgMDdAit9Jc4typkPuaevxDni/3y68Phv995dKHUIECBAgAABAvcEbhZYjwqj9GcLZvxMw6PCKI0nna9UI0CAAAECBAjcLbBuFSmjXh1TfVowXdY0nrR/7QkQIECAAIFrCSiw/JL7tTLebAkQIECAwAkCCiwF1glpZggCBAgQIHAtAQWWAutaGW+2BAgQIEDgBIGvf/755/fP4/z1119//6db7+xLH/pOHyq/N+6seNL5nrBmhiBAgAABAgQWF/j67du378++/PiYS/KzBdU/01AdT9r/4mstPAIECBAgQOAkgb8/wXr2XYFpTOnpvLR9dTxp/9oTIECAAAECBA4BBZZfcrcTCBAgQIAAgcECCiwF1uCU0h0BAgQIECCgwFJg2QUECBAgQIDAYIFhD7lXnxY85v3uuwJHvgJo8DrojgABAgQIENhIYMjPNFSfFqz+2YhjPZPTkRutv6kQIECAAAECBQJDfmg0Pf1X3b7ASZcECBAgQIAAgacFFFhPU2lIgAABAgQIEHhOQIH1nJNWBAgQIECAAIGnBRRYT1NpSIAAAQIECBB4TiB+yP3o9tlX6zx6OH1UP94V+NxCa0WAAAECBAicJxD9TMMZBVNSeB1tnf47L1mMRIAAAQIECDwnEP3QaPXpv7T/56aoFQECBAgQIEDgXAEF1rneRiNAgAABAgQuIKDAusAimyIBAgQIECBwroAC61xvoxEgQIAAAQIXELj7kPut03kjX4kzov9jfTzkfoEsNUUCBAgQINBM4ObPNDwqXEa81Hlk/36moVnGCZcAAQIECFxA4OYPjabzrj79V91/Ol/tCRAgQIAAAQKPBBRY8oMAAQIECBAgMFhAgTUYVHcECBAgQIAAAQWWHCBAgAABAgQIDBaIC6z0Iff0IfTq/gf76Y4AAQIECBAg8B+BqMBKf6bhGC35GYXq/q0/AQIECBAgQOAMgbjAulU03Tvll07AacFUTHsCBAgQIEBgRQEF1oqrIiYCBAgQIECgtYACq/XyCZ4AAQIECBBYUUCBteKqiIkAAQIECBBoLRAXWH/99deX42Tgr//78d+SB9pvqT16yD09jdh6VQRPgAABAgQItBaICqxjprd+RuH47+8WVz8Uq/tvvVqCJ0CAAAECBFoIxAVWi1kJkgABAgQIECAwUUCBNRHf0AQIECBAgMCeAgqsPdfVrAgQIECAAIGJAgqsifiGJkCAAAECBPYUUGDtua5mRYAAAQIECEwUUGBNxDc0AQIECBAgsKeAAmvPdTUrAgQIECBAYKKAAmsivqEJECBAgACBPQUUWHuuq1kRIECAAAECEwUUWBPxDU2AAAECBAjsKaDA2nNdzYoAAQIECBCYKKDAmohvaAIECBAgQGBPAQXWnutqVgQIECBAgMBEAQXWRHxDEyBAgAABAnsKKLD2XFezIkCAAAECBCYKKLAm4huaAAECBAgQ2FNAgbXnupoVAQIECBAgMFFAgTUR39AECBAgQIDAngIKrD3X1awIECBAgACBiQIKrIn4hiZAgAABAgT2FFBg7bmuZkWAAAECBAhMFFBgTcQ3NAECBAgQILCngAJrz3U1KwIECBAgQGCigAJrIr6hCRAgQIAAgT0FFFh7rqtZESBAgAABAhMFFFgT8Q1NgAABAgQI7CmgwNpzXc2KAAECBAgQmCigwJqIb2gCBAgQIEBgTwEF1p7ralYECBAgQIDARAEF1kR8QxMgQIAAAQJ7Ciiw9lxXsyJAgAABAgQmCiiwJuIbmgABAgQIENhTQIG157qaFQECBAgQIDBRQIE1Ed/QBAgQIECAwJ4CCqw919WsCBAgQIAAgYkCCqyJ+IYmQIAAAQIE9hRQYO25rmZFgAABAgQITBRQYE3ENzQBAgQIECCwp4ACa891NSsCBAgQIEBgooACayK+oQkQIECAAIE9BRRYe66rWREgQIAAAQITBRRYE/ENTYAAAQIECOwpoMDac13NigABAgQIEJgooMCaiG9oAgQIECBAYE8BBdae62pWBAgQIECAwEQBBdZEfEMTIECAAAECewoosPZcV7MiQIAAAQIEJgoosCbiG5oAAQIECBDYU0CBtee6mhUBAgQIECAwUUCBNRHf0AQIECBAgMCeAgqsPdfVrAgQIECAAIGJAgqsifiGJkCAAAECBPYUUGDtua5mRYAAAQIECEwUUGBNxDc0AQIECBAgsKeAAmvPdTUrAgQIECBAYKKAAmsivqEJECBAgACBPQUUWHuuq1kRIECAAAECEwUUWBPxDU2AAAECBAjsKaDA2nNdzYoAAQIECBCYKKDAmohvaAIECBAgQGBPAQXWnutqVgQIECBAgMBEAQXWRHxDEyBAgAABAnsKKLD2XFezIkCAAAECBCYKKLAm4huaAAECBAgQ2FNAgbXnupoVAQIECBAgMFFAgTUR39AECBAgQIDAngIKrD3X1awIECBAgACBiQIKrIn4hiZAgAABAgT2FFBg7bmuZkWAAAECBAhMFFBgTcQ3NAECBAgQILCngAJrz3U1KwIECBAgQGCigAJrIr6hCRAgQIAAgT0FFFh7rqtZESBAgAABAhMFFFgT8Q1NgAABAgQI7CmgwNpzXc2KAAECBAgQmCigwJqIb2gCBAgQIEBgTwEF1p7ralYECBAgQIDARAEF1kR8QxMgQIAAAQJ7Ciiw9lxXsyJAgAABAgQmCiiwJuIbmgABAgQIENhTQIG157qaFQECBAgQIDBRQIE1Ed/QBAgQIECAwJ4CCqw919WsCBAgQIAAgYkCCqyJ+IYmQIAAAQIE9hRQYO25rmZFgAABAgQITBRQYE3ENzQBAgQIECCwp4ACa891NSsCBAgQIEBgooACayK+oQkQIECAAIE9BRRYe66rWREgQIAAAQITBRRYE/ENTYAAAQIECOwpoMDac13NigABAgQIEJgooMCaiG9oAgQIECBAYE8BBdae62pWBAgQIECAwEQBBdZEfEMTIECAAAECewoosPZcV7MiQIAAAQIEJgoosCbiG5oAAQIECBDYU0CBtee6mhUBAgQIECAwUUCBNRHf0AQIECBAgMCeAgqsPdfVrAgQIECAAIGJAgqsifiGJkCAAAECBPYUUGDtua5mRYAAAQIECEwUUGBNxDc0AQIECBAgsKeAAmvPdTUrAgQIECBAYKKAAmsivqEJECBAgACBPQUUWHuuq1kRIECAAAECEwUUWBPxDU2AAAECBAjsKaDA2nNdzYoAAQIECBCYKKDAmohvaAIECBAgQGBPAQXWnutqVgQIECBAgMBEAQXWRHxDEyBAgAABAnsKKLD2XFezIkCAAAECBCYKKLAm4huaAAECBAgQ2FNAgbXnupoVAQIECBAgMFFAgTUR39AECBAgQIDAngIKrD3X1awIECBAgACBiQIKrIn4hiZAgAABAgT2FFBg7bmuZkWAAAECBAhMFFBgTcQ3NAECBAgQILCngAJrz3U1KwIECBAgQGCigAJrIr6hCRAgQIAAgT0FFFh7rqtZESBAgAABAhMFFFgT8Q1NgAABAgQI7CmgwNpzXc2KAAECBAgQmCigwJqIb2gCBAgQIEBgTwEF1p7ralYECBAgQIDARAEF1kR8QxMgQIAAAQJ7Ciiw9lxXsyJAgAABAgQmCiiwJuIbmgABAgQIENhTQIG157qaFQECBAgQIDBRQIE1Ed/QBAgQIECAwJ4CCqw919WsCBAgQIAAgYkCCqyJ+IYmQIAAAQIE9hRQYO25rmZFgAABAgQITBRQYE3ENzQBAgQIECCwp4ACa891NSsCBAgQIEBgooACayK+oQkQIECAAIE9BRRYe66rWREgQIAAAQITBRRYE/ENTYAAAQIECOwpoMDac13NigABAgQIEJgooMCaiG9oAgQIECBAYE8BBdae62pWBAgQIECAwEQBBdZEfEMTIECAAAECewoosPZcV7MiQIAAAQIEJgoosCbiG5oAAQIECBDYU0CB1g/tcwAAAa9JREFUtee6mhUBAgQIECAwUUCBNRHf0AQIECBAgMCeAgqsPdfVrAgQIECAAIGJAgqsifiGJkCAAAECBPYUUGDtua5mRYAAAQIECEwUUGBNxDc0AQIECBAgsKeAAmvPdTUrAgQIECBAYKKAAmsivqEJECBAgACBPQUUWHuuq1kRIECAAAECEwUUWBPxDU2AAAECBAjsKaDA2nNdzYoAAQIECBCYKKDAmohvaAIECBAgQGBPAQXWnutqVgQIECBAgMBEAQXWRHxDEyBAgAABAnsKKLD2XFezIkCAAAECBCYKKLAm4huaAAECBAgQ2FNAgbXnupoVAQIECBAgMFFAgTUR39AECBAgQIDAngIKrD3X1awIECBAgACBiQIKrIn4hiZAgAABAgT2FFBg7bmuZkWAAAECBAhMFFBgTcQ3NAECBAgQILCngAJrz3U1KwIECBAgQGCigAJrIr6hCRAgQIAAgT0FFFh7rqtZESBAgAABAhMFFFgT8Q1NgAABAgQI7CmgwNpzXc2KAAECBAgQmCigwJqIb2gCBAgQIEBgTwEF1p7ralYECBAgQIDARIH/B5knXUshrtqvAAAAAElFTkSuQmCC"
                                 :id            123
                                 :author        {:username "madvas"
                                                 :email "some@email.com"
                                                 :bio "I am good"
                                                 :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}
                                 :star-count    7
                                 :starred-by-me true})
              :all-cmds       {:forward "Forward"
                               :left    "Rotate Left"
                               :right   "Rotate Right"
                               :push    "Push Position"
                               :pop     "Pop Position"
                               :default "No Action"}}})
