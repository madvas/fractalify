(ns material-ui.core)

(def material-tags
  '[AppBar
    AppCanvas
    Circle
    Checkbox
    DatePicker
    DialogWindow
    Dialog
    DropDownIcon
    DropDownMenu
    EnhancedButton
    FlatButton
    FloatingActionButton
    FocusRipple
    FontIcon
    IconButton
    Icon
    InkBar
    Input
    LeftNav
    MenuItem
    Menu
    Overlay
    Paper
    RadioButton
    RadioButtonGroup
    RaisedButton
    Slider
    SlideIn
    Snackbar
    SvgIcon
    Tab
    TabTemplate
    Tabs
    TableHeader
    TableRowsItem
    TableRows
    TextField
    Toggle
    ToolbarGroup
    Toolbar
    Tooltip
    TouchRipple])


(defn material-ui-react-import [tname]
  `(def ~tname (reagent.core/adapt-react-class (aget js/MaterialUI ~(name tname)))))

(defmacro export-material-ui-react-classes []
  `(do ~@(map material-ui-react-import material-tags)))

