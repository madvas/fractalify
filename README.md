# Fractalify
##### Not just a clojure(script) example app
##

[fractalify.com] is a entertainment and educational webapp for creating & sharing fractal images made via so called [L-systems][1].

Originally, I started this app as a little app for practicing lovely language [Clojure][2] & [Clojurescript][3], but over time it somehow became fully usable and nice webapp :)

Main reason I share this code is that I spent fair amount of time finding out how various libraries work together, so every Clojure beginner can take a quick look into this code if he struggles using same libraries.

##### List of notable libraries used in this project:
#
##### *Clojure:*
*  [Ring](https://github.com/ring-clojure/ring)
*  [Liberator](http://clojure-liberator.github.io/liberator/) - Awesome stuff for creating REST API
*  [HTTP Kit](https://github.com/http-kit/http-kit) - Used as HTTP server
*  [Component](https://github.com/stuartsierra/component) - Dependency Injection for Clojure, lovely!
*  [Monger](http://clojuremongodb.info/) - Superb Clojure MongoDB client
*  [Friend](https://github.com/cemerick/friend) -  Authentication library for Clojure/Ring
*  [Midje](https://github.com/marick/Midje) - Perfect test framework, used for REST API tests
*  [cloudinary_java](https://github.com/cloudinary/cloudinary_java) Java library used to upload images to [Cloudinary](http://cloudinary.com/)

##### *Clojurescript*:
* [re-frame](https://github.com/Day8/re-frame) - Reagent Framework for SPAs (so simple, yet so powerful!)
* [Reagent](http://reagent-project.github.io/) - Minimalistic React interface
* [material-ui](http://material-ui.com/#/) - Material Design components for React
* [pushy](https://github.com/kibu-australia/pushy) - HTML5 pushState
* [monet](https://github.com/rm-hull/monet) - JS Canvas interop
* [Figwheel](https://github.com/bhauman/lein-figwheel) Hot loads for cljs
* [cljs-ajax](https://github.com/JulianBirch/cljs-ajax)
* WebWorkers - I haven't used any library for this, because none worked for me. It was quite challenging, you can see the code how I eventually managed to get it working.


##### *Both CLJ & CLJS:*
* [Schema](https://github.com/Prismatic/schema) - Data validation (this is gold!)
* [Plumbing](https://github.com/Prismatic/plumbing) - Utility functions (very useful)
* [Specter](https://github.com/nathanmarz/specter) - Advanced manipulating lists & maps
* [bidi](https://github.com/juxt/bidi) - Server & Client side routing
* [Transit Format](https://github.com/cognitect/transit-format) - Format used between client & server

Infinite thanks to creators or Clojure and creators of all these amazing libraries!   
Feel free to use code in any way or if you know how to improve it, please let me know.

Enjoy!  
[@matuslestan](https://twitter.com/matuslestan)

[1]: https://en.wikipedia.org/wiki/L-system
[2]: https://github.com/clojure/clojure
[3]: https://github.com/clojure/clojurescript
[fractalify.com]: http://fractalify.com/



