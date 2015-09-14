(ns material-ui.macros
  (:require [clojure.string :as str]))

(def material-tags
  '[AppBar
    AppCanvas
    Avatar
    FlatButton
    RaisedButton
    FloatingActionButton
    Card
    CardHeader
    CardMedia
    CardTitle
    CardActions
    CardText
    DatePicker
    Dialog
    DropDownMenu
    DropDownIcon
    FontIcon
    IconButton
    IconMenu
    MenuItem
    LeftNav
    List
    ListItem
    ListDivider
    Menu
    MenuItem
    Paper
    LinearProgress
    CircularProgress
    RefreshIndicator
    Slider
    Checkbox
    Snackbar
    Table
    Tabs
    Tab
    TextField
    SelectField
    TimePicker
    Toolbar
    ToolbarGroup
    ToolbarSeparator])

(defn kebab-case
  "Converts CamelCase / camelCase to kebab-case"
  [s]
  (str/join "-" (map str/lower-case (re-seq #"\w[a-z]+" s))))

(defn material-ui-react-import [tname]
  `(def ~(symbol (kebab-case (str tname))) (reagent.core/adapt-react-class (aget js/MaterialUI ~(name tname)))))

(defmacro export-material-ui-react-classes []
  `(do ~@(map material-ui-react-import material-tags)))

(macroexpand '(export-material-ui-react-classes))
