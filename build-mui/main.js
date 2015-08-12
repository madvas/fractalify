(function () {
  var React = require('react'),
    injectTapEventPlugin = require("react-tap-event-plugin"),
    materialUI = require('material-ui');

  var ThemeManager = require('material-ui/lib/styles/theme-manager')();

  window.ThemeManager = ThemeManager;

  console.log(ThemeManager.getCurrentTheme());

  ThemeManager.setTheme(ThemeManager.types.LIGHT);

  //Needed for React Developer Tools
  window.React = React;
 
  window.MaterialUI = materialUI;

  //Needed for onTouchTap
  //Can go away when react 1.0 release
  //Check this repo:
  //https://github.com/zilverline/react-tap-event-plugin
  injectTapEventPlugin();
 
})();
