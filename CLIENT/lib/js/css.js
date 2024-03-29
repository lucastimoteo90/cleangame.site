CodeMirror.defineMode("css", function(config, parserConfig) {
    "use strict";
  
    if (!parserConfig.propertyKeywords) parserConfig = CodeMirror.resolveMode("text/css");
  
    var indentUnit = config.indentUnit,
        tokenHooks = parserConfig.tokenHooks,
        mediaTypes = parserConfig.mediaTypes || {},
        mediaFeatures = parserConfig.mediaFeatures || {},
        propertyKeywords = parserConfig.propertyKeywords || {},
        colorKeywords = parserConfig.colorKeywords || {},
        valueKeywords = parserConfig.valueKeywords || {},
        fontProperties = parserConfig.fontProperties || {},
        allowNested = parserConfig.allowNested;
  
    var type, override;
    function ret(style, tp) { type = tp; return style; }
  
    // Tokenizers
  
    function tokenBase(stream, state) {
      var ch = stream.next();
      if (tokenHooks[ch]) {
        var result = tokenHooks[ch](stream, state);
        if (result !== false) return result;
      }
      if (ch == "@") {
        stream.eatWhile(/[\w\\\-]/);
        return ret("def", stream.current());
      } else if (ch == "=" || (ch == "~" || ch == "|") && stream.eat("=")) {
        return ret(null, "compare");
      } else if (ch == "\"" || ch == "'") {
        state.tokenize = tokenString(ch);
        return state.tokenize(stream, state);
      } else if (ch == "#") {
        stream.eatWhile(/[\w\\\-]/);
        return ret("atom", "hash");
      } else if (ch == "!") {
        stream.match(/^\s*\w*/);
        return ret("keyword", "important");
      } else if (/\d/.test(ch) || ch == "." && stream.eat(/\d/)) {
        stream.eatWhile(/[\w.%]/);
        return ret("number", "unit");
      } else if (ch === "-") {
        if (/[\d.]/.test(stream.peek())) {
          stream.eatWhile(/[\w.%]/);
          return ret("number", "unit");
        } else if (stream.match(/^[^-]+-/)) {
          return ret("meta", "meta");
        }
      } else if (/[,+>*\/]/.test(ch)) {
        return ret(null, "select-op");
      } else if (ch == "." && stream.match(/^-?[_a-z][_a-z0-9-]*/i)) {
        return ret("qualifier", "qualifier");
      } else if (/[:;{}\[\]\(\)]/.test(ch)) {
        return ret(null, ch);
      } else if (ch == "u" && stream.match("rl(")) {
        stream.backUp(1);
        state.tokenize = tokenParenthesized;
        return ret("property", "word");
      } else if (/[\w\\\-]/.test(ch)) {
        stream.eatWhile(/[\w\\\-]/);
        return ret("property", "word");
      } else {
        return ret(null, null);
      }
    }
  
    function tokenString(quote) {
      return function(stream, state) {
        var escaped = false, ch;
        while ((ch = stream.next()) != null) {
          if (ch == quote && !escaped) {
            if (quote == ")") stream.backUp(1);
            break;
          }
          escaped = !escaped && ch == "\\";
        }
        if (ch == quote || !escaped && quote != ")") state.tokenize = null;
        return ret("string", "string");
      };
    }
  
    function tokenParenthesized(stream, state) {
      stream.next(); // Must be '('
      if (!stream.match(/\s*[\"\']/, false))
        state.tokenize = tokenString(")");
      else
        state.tokenize = null;
      return ret(null, "(");
    }
  
    // Context management
  
    function Context(type, indent, prev) {
      this.type = type;
      this.indent = indent;
      this.prev = prev;
    }
  
    function pushContext(state, stream, type) {
      state.context = new Context(type, stream.indentation() + indentUnit, state.context);
      return type;
    }
  
    function popContext(state) {
      state.context = state.context.prev;
      return state.context.type;
    }
  
    function pass(type, stream, state) {
      return states[state.context.type](type, stream, state);
    }
    function popAndPass(type, stream, state, n) {
      for (var i = n || 1; i > 0; i--)
        state.context = state.context.prev;
      return pass(type, stream, state);
    }
  
    // Parser
  
    function wordAsValue(stream) {
      var word = stream.current().toLowerCase();
      if (valueKeywords.hasOwnProperty(word))
        override = "atom";
      else if (colorKeywords.hasOwnProperty(word))
        override = "keyword";
      else
        override = "variable";
    }
  
    var states = {};
  
    states.top = function(type, stream, state) {
      if (type == "{") {
        return pushContext(state, stream, "block");
      } else if (type == "}" && state.context.prev) {
        return popContext(state);
      } else if (type == "@media") {
        return pushContext(state, stream, "media");
      } else if (type == "@font-face") {
        return "font_face_before";
      } else if (/^@(-(moz|ms|o|webkit)-)?keyframes$/.test(type)) {
        return "keyframes";
      } else if (type && type.charAt(0) == "@") {
        return pushContext(state, stream, "at");
      } else if (type == "hash") {
        override = "builtin";
      } else if (type == "word") {
        override = "tag";
      } else if (type == "variable-definition") {
        return "maybeprop";
      } else if (type == "interpolation") {
        return pushContext(state, stream, "interpolation");
      } else if (type == ":") {
        return "pseudo";
      } else if (allowNested && type == "(") {
        return pushContext(state, stream, "params");
      }
      return state.context.type;
    };
  
    states.block = function(type, stream, state) {
      if (type == "word") {
        if (propertyKeywords.hasOwnProperty(stream.current().toLowerCase())) {
          override = "property";
          return "maybeprop";
        } else if (allowNested) {
          override = stream.match(/^\s*:/, false) ? "property" : "tag";
          return "block";
        } else {
          override += " error";
          return "maybeprop";
        }
      } else if (type == "meta") {
        return "block";
      } else if (!allowNested && (type == "hash" || type == "qualifier")) {
        override = "error";
        return "block";
      } else {
        return states.top(type, stream, state);
      }
    };
  
    states.maybeprop = function(type, stream, state) {
      if (type == ":") return pushContext(state, stream, "prop");
      return pass(type, stream, state);
    };
  
    states.prop = function(type, stream, state) {
      if (type == ";") return popContext(state);
      if (type == "{" && allowNested) return pushContext(state, stream, "propBlock");
      if (type == "}" || type == "{") return popAndPass(type, stream, state);
      if (type == "(") return pushContext(state, stream, "parens");
  
      if (type == "hash" && !/^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/.test(stream.current())) {
        override += " error";
      } else if (type == "word") {
        wordAsValue(stream);
      } else if (type == "interpolation") {
        return pushContext(state, stream, "interpolation");
      }
      return "prop";
    };
  
    states.propBlock = function(type, _stream, state) {
      if (type == "}") return popContext(state);
      if (type == "word") { override = "property"; return "maybeprop"; }
      return state.context.type;
    };
  
    states.parens = function(type, stream, state) {
      if (type == "{" || type == "}") return popAndPass(type, stream, state);
      if (type == ")") return popContext(state);
      return "parens";
    };
  
    states.pseudo = function(type, stream, state) {
      if (type == "word") {
        override = "variable-3";
        return state.context.type;
      }
      return pass(type, stream, state);
    };
  
    states.media = function(type, stream, state) {
      if (type == "(") return pushContext(state, stream, "media_parens");
      if (type == "}") return popAndPass(type, stream, state);
      if (type == "{") return popContext(state) && pushContext(state, stream, allowNested ? "block" : "top");
  
      if (type == "word") {
        var word = stream.current().toLowerCase();
        if (word == "only" || word == "not" || word == "and")
          override = "keyword";
        else if (mediaTypes.hasOwnProperty(word))
          override = "attribute";
        else if (mediaFeatures.hasOwnProperty(word))
          override = "property";
        else
          override = "error";
      }
      return state.context.type;
    };
  
    states.media_parens = function(type, stream, state) {
      if (type == ")") return popContext(state);
      if (type == "{" || type == "}") return popAndPass(type, stream, state, 2);
      return states.media(type, stream, state);
    };
  
    states.font_face_before = function(type, stream, state) {
      if (type == "{")
        return pushContext(state, stream, "font_face");
      return pass(type, stream, state);
    };
  
    states.font_face = function(type, stream, state) {
      if (type == "}") return popContext(state);
      if (type == "word") {
        if (!fontProperties.hasOwnProperty(stream.current().toLowerCase()))
          override = "error";
        else
          override = "property";
        return "maybeprop";
      }
      return "font_face";
    };
  
    states.keyframes = function(type, stream, state) {
      if (type == "word") { override = "variable"; return "keyframes"; }
      if (type == "{") return pushContext(state, stream, "top");
      return pass(type, stream, state);
    };
  
    states.at = function(type, stream, state) {
      if (type == ";") return popContext(state);
      if (type == "{" || type == "}") return popAndPass(type, stream, state);
      if (type == "word") override = "tag";
      else if (type == "hash") override = "builtin";
      return "at";
    };
  
    states.interpolation = function(type, stream, state) {
      if (type == "}") return popContext(state);
      if (type == "{" || type == ";") return popAndPass(type, stream, state);
      if (type != "variable") override = "error";
      return "interpolation";
    };
  
    states.params = function(type, stream, state) {
      if (type == ")") return popContext(state);
      if (type == "{" || type == "}") return popAndPass(type, stream, state);
      if (type == "word") wordAsValue(stream);
      return "params";
    };
  
    return {
      startState: function(base) {
        return {tokenize: null,
                state: "top",
                context: new Context("top", base || 0, null)};
      },
  
      token: function(stream, state) {
        if (!state.tokenize && stream.eatSpace()) return null;
        var style = (state.tokenize || tokenBase)(stream, state);
        if (style && typeof style == "object") {
          type = style[1];
          style = style[0];
        }
        override = style;
        state.state = states[state.state](type, stream, state);
        return override;
      },
  
      indent: function(state, textAfter) {
        var cx = state.context, ch = textAfter && textAfter.charAt(0);
        var indent = cx.indent;
        if (cx.type == "prop" && ch == "}") cx = cx.prev;
        if (cx.prev &&
            (ch == "}" && (cx.type == "block" || cx.type == "top" || cx.type == "interpolation" || cx.type == "font_face") ||
             ch == ")" && (cx.type == "parens" || cx.type == "params" || cx.type == "media_parens") ||
             ch == "{" && (cx.type == "at" || cx.type == "media"))) {
          indent = cx.indent - indentUnit;
          cx = cx.prev;
        }
        return indent;
      },
  
      electricChars: "}",
      blockCommentStart: "/*",
      blockCommentEnd: "*/",
      fold: "brace"
    };
  });
  
  (function() {
    function keySet(array) {
      var keys = {};
      for (var i = 0; i < array.length; ++i) {
        keys[array[i]] = true;
      }
      return keys;
    }
  
    var mediaTypes_ = [
      "all", "aural", "braille", "handheld", "print", "projection", "screen",
      "tty", "tv", "embossed"
    ], mediaTypes = keySet(mediaTypes_);
  
    var mediaFeatures_ = [
      "width", "min-width", "max-width", "height", "min-height", "max-height",
      "device-width", "min-device-width", "max-device-width", "device-height",
      "min-device-height", "max-device-height", "aspect-ratio",
      "min-aspect-ratio", "max-aspect-ratio", "device-aspect-ratio",
      "min-device-aspect-ratio", "max-device-aspect-ratio", "color", "min-color",
      "max-color", "color-index", "min-color-index", "max-color-index",
      "monochrome", "min-monochrome", "max-monochrome", "resolution",
      "min-resolution", "max-resolution", "scan", "grid"
    ], mediaFeatures = keySet(mediaFeatures_);
  
    var propertyKeywords_ = [
      "align-content", "align-items", "align-self", "alignment-adjust",
      "alignment-baseline", "anchor-point", "animation", "animation-delay",
      "animation-direction", "animation-duration", "animation-fill-mode",
      "animation-iteration-count", "animation-name", "animation-play-state",
      "animation-timing-function", "appearance", "azimuth", "backface-visibility",
      "background", "background-attachment", "background-clip", "background-color",
      "background-image", "background-origin", "background-position",
      "background-repeat", "background-size", "baseline-shift", "binding",
      "bleed", "bookmark-label", "bookmark-level", "bookmark-state",
      "bookmark-target", "border", "border-bottom", "border-bottom-color",
      "border-bottom-left-radius", "border-bottom-right-radius",
      "border-bottom-style", "border-bottom-width", "border-collapse",
      "border-color", "border-image", "border-image-outset",
      "border-image-repeat", "border-image-slice", "border-image-source",
      "border-image-width", "border-left", "border-left-color",
      "border-left-style", "border-left-width", "border-radius", "border-right",
      "border-right-color", "border-right-style", "border-right-width",
      "border-spacing", "border-style", "border-top", "border-top-color",
      "border-top-left-radius", "border-top-right-radius", "border-top-style",
      "border-top-width", "border-width", "bottom", "box-decoration-break",
      "box-shadow", "box-sizing", "break-after", "break-before", "break-inside",
      "caption-side", "clear", "clip", "color", "color-profile", "column-count",
      "column-fill", "column-gap", "column-rule", "column-rule-color",
      "column-rule-style", "column-rule-width", "column-span", "column-width",
      "columns", "content", "counter-increment", "counter-reset", "crop", "cue",
      "cue-after", "cue-before", "cursor", "direction", "display",
      "dominant-baseline", "drop-initial-after-adjust",
      "drop-initial-after-align", "drop-initial-before-adjust",
      "drop-initial-before-align", "drop-initial-size", "drop-initial-value",
      "elevation", "empty-cells", "fit", "fit-position", "flex", "flex-basis",
      "flex-direction", "flex-flow", "flex-grow", "flex-shrink", "flex-wrap",
      "float", "float-offset", "flow-from", "flow-into", "font", "font-feature-settings",
      "font-family", "font-kerning", "font-language-override", "font-size", "font-size-adjust",
      "font-stretch", "font-style", "font-synthesis", "font-variant",
      "font-variant-alternates", "font-variant-caps", "font-variant-east-asian",
      "font-variant-ligatures", "font-variant-numeric", "font-variant-position",
      "font-weight", "grid", "grid-area", "grid-auto-columns", "grid-auto-flow",
      "grid-auto-position", "grid-auto-rows", "grid-column", "grid-column-end",
      "grid-column-start", "grid-row", "grid-row-end", "grid-row-start",
      "grid-template", "grid-template-areas", "grid-template-columns",
      "grid-template-rows", "hanging-punctuation", "height", "hyphens",
      "icon", "image-orientation", "image-rendering", "image-resolution",
      "inline-box-align", "justify-content", "left", "letter-spacing",
      "line-break", "line-height", "line-stacking", "line-stacking-ruby",
      "line-stacking-shift", "line-stacking-strategy", "list-style",
      "list-style-image", "list-style-position", "list-style-type", "margin",
      "margin-bottom", "margin-left", "margin-right", "margin-top",
      "marker-offset", "marks", "marquee-direction", "marquee-loop",
      "marquee-play-count", "marquee-speed", "marquee-style", "max-height",
      "max-width", "min-height", "min-width", "move-to", "nav-down", "nav-index",
      "nav-left", "nav-right", "nav-up", "opacity", "order", "orphans", "outline",
      "outline-color", "outline-offset", "outline-style", "outline-width",
      "overflow", "overflow-style", "overflow-wrap", "overflow-x", "overflow-y",
      "padding", "padding-bottom", "padding-left", "padding-right", "padding-top",
      "page", "page-break-after", "page-break-before", "page-break-inside",
      "page-policy", "pause", "pause-after", "pause-before", "perspective",
      "perspective-origin", "pitch", "pitch-range", "play-during", "position",
      "presentation-level", "punctuation-trim", "quotes", "region-break-after",
      "region-break-before", "region-break-inside", "region-fragment",
      "rendering-intent", "resize", "rest", "rest-after", "rest-before", "richness",
      "right", "rotation", "rotation-point", "ruby-align", "ruby-overhang",
      "ruby-position", "ruby-span", "shape-inside", "shape-outside", "size",
      "speak", "speak-as", "speak-header",
      "speak-numeral", "speak-punctuation", "speech-rate", "stress", "string-set",
      "tab-size", "table-layout", "target", "target-name", "target-new",
      "target-position", "text-align", "text-align-last", "text-decoration",
      "text-decoration-color", "text-decoration-line", "text-decoration-skip",
      "text-decoration-style", "text-emphasis", "text-emphasis-color",
      "text-emphasis-position", "text-emphasis-style", "text-height",
      "text-indent", "text-justify", "text-outline", "text-overflow", "text-shadow",
      "text-size-adjust", "text-space-collapse", "text-transform", "text-underline-position",
      "text-wrap", "top", "transform", "transform-origin", "transform-style",
      "transition", "transition-delay", "transition-duration",
      "transition-property", "transition-timing-function", "unicode-bidi",
      "vertical-align", "visibility", "voice-balance", "voice-duration",
      "voice-family", "voice-pitch", "voice-range", "voice-rate", "voice-stress",
      "voice-volume", "volume", "white-space", "widows", "width", "word-break",
      "word-spacing", "word-wrap", "z-index", "zoom",
      // SVG-specific
      "clip-path", "clip-rule", "mask", "enable-background", "filter", "flood-color",
      "flood-opacity", "lighting-color", "stop-color", "stop-opacity", "pointer-events",
      "color-interpolation", "color-interpolation-filters", "color-profile",
      "color-rendering", "fill", "fill-opacity", "fill-rule", "image-rendering",
      "marker", "marker-end", "marker-mid", "marker-start", "shape-rendering", "stroke",
      "stroke-dasharray", "stroke-dashoffset", "stroke-linecap", "stroke-linejoin",
      "stroke-miterlimit", "stroke-opacity", "stroke-width", "text-rendering",
      "baseline-shift", "dominant-baseline", "glyph-orientation-horizontal",
      "glyph-orientation-vertical", "kerning", "text-anchor", "writing-mode"
    ], propertyKeywords = keySet(propertyKeywords_);
  
    var colorKeywords_ = [
      "aliceblue", "antiquewhite", "aqua", "aquamarine", "azure", "beige",
      "bisque", "black", "blanchedalmond", "blue", "blueviolet", "brown",
      "burlywood", "cadetblue", "chartreuse", "chocolate", "coral", "cornflowerblue",
      "cornsilk", "crimson", "cyan", "darkblue", "darkcyan", "darkgoldenrod",
      "darkgray", "darkgreen", "darkkhaki", "darkmagenta", "darkolivegreen",
      "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen",
      "darkslateblue", "darkslategray", "darkturquoise", "darkviolet",
      "deeppink", "deepskyblue", "dimgray", "dodgerblue", "firebrick",
      "floralwhite", "forestgreen", "fuchsia", "gainsboro", "ghostwhite",
      "gold", "goldenrod", "gray", "grey", "green", "greenyellow", "honeydew",
      "hotpink", "indianred", "indigo", "ivory", "khaki", "lavender",
      "lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral",
      "lightcyan", "lightgoldenrodyellow", "lightgray", "lightgreen", "lightpink",
      "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray",
      "lightsteelblue", "lightyellow", "lime", "limegreen", "linen", "magenta",
      "maroon", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple",
      "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise",
      "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin",
      "navajowhite", "navy", "oldlace", "olive", "olivedrab", "orange", "orangered",
      "orchid", "palegoldenrod", "palegreen", "paleturquoise", "palevioletred",
      "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue",
      "purple", "red", "rosybrown", "royalblue", "saddlebrown", "salmon",
      "sandybrown", "seagreen", "seashell", "sienna", "silver", "skyblue",
      "slateblue", "slategray", "snow", "springgreen", "steelblue", "tan",
      "teal", "thistle", "tomato", "turquoise", "violet", "wheat", "white",
      "whitesmoke", "yellow", "yellowgreen"
    ], colorKeywords = keySet(colorKeywords_);
  
    var valueKeywords_ = [
      "above", "absolute", "activeborder", "activecaption", "afar",
      "after-white-space", "ahead", "alias", "all", "all-scroll", "alternate",
      "always", "amharic", "amharic-abegede", "antialiased", "appworkspace",
      "arabic-indic", "armenian", "asterisks", "auto", "avoid", "avoid-column", "avoid-page",
      "avoid-region", "background", "backwards", "baseline", "below", "bidi-override", "binary",
      "bengali", "blink", "block", "block-axis", "bold", "bolder", "border", "border-box",
      "both", "bottom", "break", "break-all", "break-word", "button", "button-bevel",
      "buttonface", "buttonhighlight", "buttonshadow", "buttontext", "cambodian",
      "capitalize", "caps-lock-indicator", "caption", "captiontext", "caret",
      "cell", "center", "checkbox", "circle", "cjk-earthly-branch",
      "cjk-heavenly-stem", "cjk-ideographic", "clear", "clip", "close-quote",
      "col-resize", "collapse", "column", "compact", "condensed", "contain", "content",
      "content-box", "context-menu", "continuous", "copy", "cover", "crop",
      "cross", "crosshair", "currentcolor", "cursive", "dashed", "decimal",
      "decimal-leading-zero", "default", "default-button", "destination-atop",
      "destination-in", "destination-out", "destination-over", "devanagari",
      "disc", "discard", "document", "dot-dash", "dot-dot-dash", "dotted",
      "double", "down", "e-resize", "ease", "ease-in", "ease-in-out", "ease-out",
      "element", "ellipse", "ellipsis", "embed", "end", "ethiopic", "ethiopic-abegede",
      "ethiopic-abegede-am-et", "ethiopic-abegede-gez", "ethiopic-abegede-ti-er",
      "ethiopic-abegede-ti-et", "ethiopic-halehame-aa-er",
      "ethiopic-halehame-aa-et", "ethiopic-halehame-am-et",
      "ethiopic-halehame-gez", "ethiopic-halehame-om-et",
      "ethiopic-halehame-sid-et", "ethiopic-halehame-so-et",
      "ethiopic-halehame-ti-er", "ethiopic-halehame-ti-et",
      "ethiopic-halehame-tig", "ew-resize", "expanded", "extra-condensed",
      "extra-expanded", "fantasy", "fast", "fill", "fixed", "flat", "footnotes",
      "forwards", "from", "geometricPrecision", "georgian", "graytext", "groove",
      "gujarati", "gurmukhi", "hand", "hangul", "hangul-consonant", "hebrew",
      "help", "hidden", "hide", "higher", "highlight", "highlighttext",
      "hiragana", "hiragana-iroha", "horizontal", "hsl", "hsla", "icon", "ignore",
      "inactiveborder", "inactivecaption", "inactivecaptiontext", "infinite",
      "infobackground", "infotext", "inherit", "initial", "inline", "inline-axis",
      "inline-block", "inline-table", "inset", "inside", "intrinsic", "invert",
      "italic", "justify", "kannada", "katakana", "katakana-iroha", "keep-all", "khmer",
      "landscape", "lao", "large", "larger", "left", "level", "lighter",
      "line-through", "linear", "lines", "list-item", "listbox", "listitem",
      "local", "logical", "loud", "lower", "lower-alpha", "lower-armenian",
      "lower-greek", "lower-hexadecimal", "lower-latin", "lower-norwegian",
      "lower-roman", "lowercase", "ltr", "malayalam", "match",
      "media-controls-background", "media-current-time-display",
      "media-fullscreen-button", "media-mute-button", "media-play-button",
      "media-return-to-realtime-button", "media-rewind-button",
      "media-seek-back-button", "media-seek-forward-button", "media-slider",
      "media-sliderthumb", "media-time-remaining-display", "media-volume-slider",
      "media-volume-slider-container", "media-volume-sliderthumb", "medium",
      "menu", "menulist", "menulist-button", "menulist-text",
      "menulist-textfield", "menutext", "message-box", "middle", "min-intrinsic",
      "mix", "mongolian", "monospace", "move", "multiple", "myanmar", "n-resize",
      "narrower", "ne-resize", "nesw-resize", "no-close-quote", "no-drop",
      "no-open-quote", "no-repeat", "none", "normal", "not-allowed", "nowrap",
      "ns-resize", "nw-resize", "nwse-resize", "oblique", "octal", "open-quote",
      "optimizeLegibility", "optimizeSpeed", "oriya", "oromo", "outset",
      "outside", "outside-shape", "overlay", "overline", "padding", "padding-box",
      "painted", "page", "paused", "persian", "plus-darker", "plus-lighter", "pointer",
      "polygon", "portrait", "pre", "pre-line", "pre-wrap", "preserve-3d", "progress", "push-button",
      "radio", "read-only", "read-write", "read-write-plaintext-only", "rectangle", "region",
      "relative", "repeat", "repeat-x", "repeat-y", "reset", "reverse", "rgb", "rgba",
      "ridge", "right", "round", "row-resize", "rtl", "run-in", "running",
      "s-resize", "sans-serif", "scroll", "scrollbar", "se-resize", "searchfield",
      "searchfield-cancel-button", "searchfield-decoration",
      "searchfield-results-button", "searchfield-results-decoration",
      "semi-condensed", "semi-expanded", "separate", "serif", "show", "sidama",
      "single", "skip-white-space", "slide", "slider-horizontal",
      "slider-vertical", "sliderthumb-horizontal", "sliderthumb-vertical", "slow",
      "small", "small-caps", "small-caption", "smaller", "solid", "somali",
      "source-atop", "source-in", "source-out", "source-over", "space", "square",
      "square-button", "start", "static", "status-bar", "stretch", "stroke",
      "sub", "subpixel-antialiased", "super", "sw-resize", "table",
      "table-caption", "table-cell", "table-column", "table-column-group",
      "table-footer-group", "table-header-group", "table-row", "table-row-group",
      "telugu", "text", "text-bottom", "text-top", "textarea", "textfield", "thai",
      "thick", "thin", "threeddarkshadow", "threedface", "threedhighlight",
      "threedlightshadow", "threedshadow", "tibetan", "tigre", "tigrinya-er",
      "tigrinya-er-abegede", "tigrinya-et", "tigrinya-et-abegede", "to", "top",
      "transparent", "ultra-condensed", "ultra-expanded", "underline", "up",
      "upper-alpha", "upper-armenian", "upper-greek", "upper-hexadecimal",
      "upper-latin", "upper-norwegian", "upper-roman", "uppercase", "urdu", "url",
      "vertical", "vertical-text", "visible", "visibleFill", "visiblePainted",
      "visibleStroke", "visual", "w-resize", "wait", "wave", "wider",
      "window", "windowframe", "windowtext", "x-large", "x-small", "xor",
      "xx-large", "xx-small"
    ], valueKeywords = keySet(valueKeywords_);
  
    var fontProperties_ = [
      "font-family", "src", "unicode-range", "font-variant", "font-feature-settings",
      "font-stretch", "font-weight", "font-style"
    ], fontProperties = keySet(fontProperties_);
  
    var allWords = mediaTypes_.concat(mediaFeatures_).concat(propertyKeywords_).concat(colorKeywords_).concat(valueKeywords_);
    CodeMirror.registerHelper("hintWords", "css", allWords);
  
    function tokenCComment(stream, state) {
      var maybeEnd = false, ch;
      while ((ch = stream.next()) != null) {
        if (maybeEnd && ch == "/") {
          state.tokenize = null;
          break;
        }
        maybeEnd = (ch == "*");
      }
      return ["comment", "comment"];
    }
  
    function tokenSGMLComment(stream, state) {
      if (stream.skipTo("-->")) {
        stream.match("-->");
        state.tokenize = null;
      } else {
        stream.skipToEnd();
      }
      return ["comment", "comment"];
    }
  
    CodeMirror.defineMIME("text/css", {
      mediaTypes: mediaTypes,
      mediaFeatures: mediaFeatures,
      propertyKeywords: propertyKeywords,
      colorKeywords: colorKeywords,
      valueKeywords: valueKeywords,
      fontProperties: fontProperties,
      tokenHooks: {
        "<": function(stream, state) {
          if (!stream.match("!--")) return false;
          state.tokenize = tokenSGMLComment;
          return tokenSGMLComment(stream, state);
        },
        "/": function(stream, state) {
          if (!stream.eat("*")) return false;
          state.tokenize = tokenCComment;
          return tokenCComment(stream, state);
        }
      },
      name: "css"
    });
  
    CodeMirror.defineMIME("text/x-scss", {
      mediaTypes: mediaTypes,
      mediaFeatures: mediaFeatures,
      propertyKeywords: propertyKeywords,
      colorKeywords: colorKeywords,
      valueKeywords: valueKeywords,
      fontProperties: fontProperties,
      allowNested: true,
      tokenHooks: {
        "/": function(stream, state) {
          if (stream.eat("/")) {
            stream.skipToEnd();
            return ["comment", "comment"];
          } else if (stream.eat("*")) {
            state.tokenize = tokenCComment;
            return tokenCComment(stream, state);
          } else {
            return ["operator", "operator"];
          }
        },
        ":": function(stream) {
          if (stream.match(/\s*{/))
            return [null, "{"];
          return false;
        },
        "$": function(stream) {
          stream.match(/^[\w-]+/);
          if (stream.match(/^\s*:/, false))
            return ["variable-2", "variable-definition"];
          return ["variable-2", "variable"];
        },
        "#": function(stream) {
          if (!stream.eat("{")) return false;
          return [null, "interpolation"];
        }
      },
      name: "css",
      helperType: "scss"
    });
  
    CodeMirror.defineMIME("text/x-less", {
      mediaTypes: mediaTypes,
      mediaFeatures: mediaFeatures,
      propertyKeywords: propertyKeywords,
      colorKeywords: colorKeywords,
      valueKeywords: valueKeywords,
      fontProperties: fontProperties,
      allowNested: true,
      tokenHooks: {
        "/": function(stream, state) {
          if (stream.eat("/")) {
            stream.skipToEnd();
            return ["comment", "comment"];
          } else if (stream.eat("*")) {
            state.tokenize = tokenCComment;
            return tokenCComment(stream, state);
          } else {
            return ["operator", "operator"];
          }
        },
        "@": function(stream) {
          if (stream.match(/^(charset|document|font-face|import|(-(moz|ms|o|webkit)-)?keyframes|media|namespace|page|supports)\b/, false)) return false;
          stream.eatWhile(/[\w\\\-]/);
          if (stream.match(/^\s*:/, false))
            return ["variable-2", "variable-definition"];
          return ["variable-2", "variable"];
        },
        "&": function() {
          return ["atom", "atom"];
        }
      },
      name: "css",
      helperType: "less"
    });
  })();


  (function() {
    var ie_lt8 = /MSIE \d/.test(navigator.userAgent) &&
      (document.documentMode == null || document.documentMode < 8);
  
    var Pos = CodeMirror.Pos;
  
    var matching = {"(": ")>", ")": "(<", "[": "]>", "]": "[<", "{": "}>", "}": "{<"};
    function findMatchingBracket(cm, where, strict) {
      var state = cm.state.matchBrackets;
      var maxScanLen = (state && state.maxScanLineLength) || 10000;
      var maxScanLines = (state && state.maxScanLines) || 100;
  
      var cur = where || cm.getCursor(), line = cm.getLineHandle(cur.line), pos = cur.ch - 1;
      var match = (pos >= 0 && matching[line.text.charAt(pos)]) || matching[line.text.charAt(++pos)];
      if (!match) return null;
      var forward = match.charAt(1) == ">", d = forward ? 1 : -1;
      if (strict && forward != (pos == cur.ch)) return null;
      var style = cm.getTokenTypeAt(Pos(cur.line, pos + 1));
  
      var stack = [line.text.charAt(pos)], re = /[(){}[\]]/;
      function scan(line, lineNo, start) {
        if (!line.text) return;
        var pos = forward ? 0 : line.text.length - 1, end = forward ? line.text.length : -1;
        if (line.text.length > maxScanLen) return null;
        if (start != null) pos = start + d;
        for (; pos != end; pos += d) {
          var ch = line.text.charAt(pos);
          if (re.test(ch) && cm.getTokenTypeAt(Pos(lineNo, pos + 1)) == style) {
            var match = matching[ch];
            if (match.charAt(1) == ">" == forward) stack.push(ch);
            else if (stack.pop() != match.charAt(0)) return {pos: pos, match: false};
            else if (!stack.length) return {pos: pos, match: true};
          }
        }
      }
      for (var i = cur.line, found, e = forward ? Math.min(i + maxScanLines, cm.lineCount()) : Math.max(-1, i - maxScanLines); i != e; i+=d) {
        if (i == cur.line) found = scan(line, i, pos);
        else found = scan(cm.getLineHandle(i), i);
        if (found) break;
      }
      return {from: Pos(cur.line, pos), to: found && Pos(i, found.pos),
              match: found && found.match, forward: forward};
    }
  
    function matchBrackets(cm, autoclear) {
      // Disable brace matching in long lines, since it'll cause hugely slow updates
      var maxHighlightLen = cm.state.matchBrackets.maxHighlightLineLength || 1000;
      var found = findMatchingBracket(cm);
      if (!found || cm.getLine(found.from.line).length > maxHighlightLen ||
         found.to && cm.getLine(found.to.line).length > maxHighlightLen)
        return;
  
      var style = found.match ? "CodeMirror-matchingbracket" : "CodeMirror-nonmatchingbracket";
      var one = cm.markText(found.from, Pos(found.from.line, found.from.ch + 1), {className: style});
      var two = found.to && cm.markText(found.to, Pos(found.to.line, found.to.ch + 1), {className: style});
      // Kludge to work around the IE bug from issue #1193, where text
      // input stops going to the textarea whenever this fires.
      if (ie_lt8 && cm.state.focused) cm.display.input.focus();
      var clear = function() {
        cm.operation(function() { one.clear(); two && two.clear(); });
      };
      if (autoclear) setTimeout(clear, 800);
      else return clear;
    }
  
    var currentlyHighlighted = null;
    function doMatchBrackets(cm) {
      cm.operation(function() {
        if (currentlyHighlighted) {currentlyHighlighted(); currentlyHighlighted = null;}
        if (!cm.somethingSelected()) currentlyHighlighted = matchBrackets(cm, false);
      });
    }
  
    CodeMirror.defineOption("matchBrackets", false, function(cm, val, old) {
      if (old && old != CodeMirror.Init)
        cm.off("cursorActivity", doMatchBrackets);
      if (val) {
        cm.state.matchBrackets = typeof val == "object" ? val : {};
        cm.on("cursorActivity", doMatchBrackets);
      }
    });
  
    CodeMirror.defineExtension("matchBrackets", function() {matchBrackets(this, true);});
    CodeMirror.defineExtension("findMatchingBracket", function(pos, strict){
      return findMatchingBracket(this, pos, strict);
    });
  })();

  
  // TODO actually recognize syntax of TypeScript constructs

CodeMirror.defineMode("javascript", function(config, parserConfig) {
    var indentUnit = config.indentUnit;
    var statementIndent = parserConfig.statementIndent;
    var jsonldMode = parserConfig.jsonld;
    var jsonMode = parserConfig.json || jsonldMode;
    var isTS = parserConfig.typescript;
  
    // Tokenizer
  
    var keywords = function(){
      function kw(type) {return {type: type, style: "keyword"};}
      var A = kw("keyword a"), B = kw("keyword b"), C = kw("keyword c");
      var operator = kw("operator"), atom = {type: "atom", style: "atom"};
  
      var jsKeywords = {
        "if": kw("if"), "while": A, "with": A, "else": B, "do": B, "try": B, "finally": B,
        "return": C, "break": C, "continue": C, "new": C, "delete": C, "throw": C, "debugger": C,
        "var": kw("var"), "const": kw("var"), "let": kw("var"),
        "function": kw("function"), "catch": kw("catch"),
        "for": kw("for"), "switch": kw("switch"), "case": kw("case"), "default": kw("default"),
        "in": operator, "typeof": operator, "instanceof": operator,
        "true": atom, "false": atom, "null": atom, "undefined": atom, "NaN": atom, "Infinity": atom,
        "this": kw("this"), "module": kw("module"), "class": kw("class"), "super": kw("atom"),
        "yield": C, "export": kw("export"), "import": kw("import"), "extends": C
      };
  
      // Extend the 'normal' keywords with the TypeScript language extensions
      if (isTS) {
        var type = {type: "variable", style: "variable-3"};
        var tsKeywords = {
          // object-like things
          "interface": kw("interface"),
          "extends": kw("extends"),
          "constructor": kw("constructor"),
  
          // scope modifiers
          "public": kw("public"),
          "private": kw("private"),
          "protected": kw("protected"),
          "static": kw("static"),
  
          // types
          "string": type, "number": type, "bool": type, "any": type
        };
  
        for (var attr in tsKeywords) {
          jsKeywords[attr] = tsKeywords[attr];
        }
      }
  
      return jsKeywords;
    }();
  
    var isOperatorChar = /[+\-*&%=<>!?|~^]/;
    var isJsonldKeyword = /^@(context|id|value|language|type|container|list|set|reverse|index|base|vocab|graph)"/;
  
    function readRegexp(stream) {
      var escaped = false, next, inSet = false;
      while ((next = stream.next()) != null) {
        if (!escaped) {
          if (next == "/" && !inSet) return;
          if (next == "[") inSet = true;
          else if (inSet && next == "]") inSet = false;
        }
        escaped = !escaped && next == "\\";
      }
    }
  
    // Used as scratch variables to communicate multiple values without
    // consing up tons of objects.
    var type, content;
    function ret(tp, style, cont) {
      type = tp; content = cont;
      return style;
    }
    function tokenBase(stream, state) {
      var ch = stream.next();
      if (ch == '"' || ch == "'") {
        state.tokenize = tokenString(ch);
        return state.tokenize(stream, state);
      } else if (ch == "." && stream.match(/^\d+(?:[eE][+\-]?\d+)?/)) {
        return ret("number", "number");
      } else if (ch == "." && stream.match("..")) {
        return ret("spread", "meta");
      } else if (/[\[\]{}\(\),;\:\.]/.test(ch)) {
        return ret(ch);
      } else if (ch == "=" && stream.eat(">")) {
        return ret("=>", "operator");
      } else if (ch == "0" && stream.eat(/x/i)) {
        stream.eatWhile(/[\da-f]/i);
        return ret("number", "number");
      } else if (/\d/.test(ch)) {
        stream.match(/^\d*(?:\.\d*)?(?:[eE][+\-]?\d+)?/);
        return ret("number", "number");
      } else if (ch == "/") {
        if (stream.eat("*")) {
          state.tokenize = tokenComment;
          return tokenComment(stream, state);
        } else if (stream.eat("/")) {
          stream.skipToEnd();
          return ret("comment", "comment");
        } else if (state.lastType == "operator" || state.lastType == "keyword c" ||
                 state.lastType == "sof" || /^[\[{}\(,;:]$/.test(state.lastType)) {
          readRegexp(stream);
          stream.eatWhile(/[gimy]/); // 'y' is "sticky" option in Mozilla
          return ret("regexp", "string-2");
        } else {
          stream.eatWhile(isOperatorChar);
          return ret("operator", "operator", stream.current());
        }
      } else if (ch == "`") {
        state.tokenize = tokenQuasi;
        return tokenQuasi(stream, state);
      } else if (ch == "#") {
        stream.skipToEnd();
        return ret("error", "error");
      } else if (isOperatorChar.test(ch)) {
        stream.eatWhile(isOperatorChar);
        return ret("operator", "operator", stream.current());
      } else {
        stream.eatWhile(/[\w\$_]/);
        var word = stream.current(), known = keywords.propertyIsEnumerable(word) && keywords[word];
        return (known && state.lastType != ".") ? ret(known.type, known.style, word) :
                       ret("variable", "variable", word);
      }
    }
  
    function tokenString(quote) {
      return function(stream, state) {
        var escaped = false, next;
        if (jsonldMode && stream.peek() == "@" && stream.match(isJsonldKeyword)){
          state.tokenize = tokenBase;
          return ret("jsonld-keyword", "meta");
        }
        while ((next = stream.next()) != null) {
          if (next == quote && !escaped) break;
          escaped = !escaped && next == "\\";
        }
        if (!escaped) state.tokenize = tokenBase;
        return ret("string", "string");
      };
    }
  
    function tokenComment(stream, state) {
      var maybeEnd = false, ch;
      while (ch = stream.next()) {
        if (ch == "/" && maybeEnd) {
          state.tokenize = tokenBase;
          break;
        }
        maybeEnd = (ch == "*");
      }
      return ret("comment", "comment");
    }
  
    function tokenQuasi(stream, state) {
      var escaped = false, next;
      while ((next = stream.next()) != null) {
        if (!escaped && (next == "`" || next == "$" && stream.eat("{"))) {
          state.tokenize = tokenBase;
          break;
        }
        escaped = !escaped && next == "\\";
      }
      return ret("quasi", "string-2", stream.current());
    }
  
    var brackets = "([{}])";
    // This is a crude lookahead trick to try and notice that we're
    // parsing the argument patterns for a fat-arrow function before we
    // actually hit the arrow token. It only works if the arrow is on
    // the same line as the arguments and there's no strange noise
    // (comments) in between. Fallback is to only notice when we hit the
    // arrow, and not declare the arguments as locals for the arrow
    // body.
    function findFatArrow(stream, state) {
      if (state.fatArrowAt) state.fatArrowAt = null;
      var arrow = stream.string.indexOf("=>", stream.start);
      if (arrow < 0) return;
  
      var depth = 0, sawSomething = false;
      for (var pos = arrow - 1; pos >= 0; --pos) {
        var ch = stream.string.charAt(pos);
        var bracket = brackets.indexOf(ch);
        if (bracket >= 0 && bracket < 3) {
          if (!depth) { ++pos; break; }
          if (--depth == 0) break;
        } else if (bracket >= 3 && bracket < 6) {
          ++depth;
        } else if (/[$\w]/.test(ch)) {
          sawSomething = true;
        } else if (sawSomething && !depth) {
          ++pos;
          break;
        }
      }
      if (sawSomething && !depth) state.fatArrowAt = pos;
    }
  
    // Parser
  
    var atomicTypes = {"atom": true, "number": true, "variable": true, "string": true, "regexp": true, "this": true, "jsonld-keyword": true};
  
    function JSLexical(indented, column, type, align, prev, info) {
      this.indented = indented;
      this.column = column;
      this.type = type;
      this.prev = prev;
      this.info = info;
      if (align != null) this.align = align;
    }
  
    function inScope(state, varname) {
      for (var v = state.localVars; v; v = v.next)
        if (v.name == varname) return true;
      for (var cx = state.context; cx; cx = cx.prev) {
        for (var v = cx.vars; v; v = v.next)
          if (v.name == varname) return true;
      }
    }
  
    function parseJS(state, style, type, content, stream) {
      var cc = state.cc;
      // Communicate our context to the combinators.
      // (Less wasteful than consing up a hundred closures on every call.)
      cx.state = state; cx.stream = stream; cx.marked = null, cx.cc = cc;
  
      if (!state.lexical.hasOwnProperty("align"))
        state.lexical.align = true;
  
      while(true) {
        var combinator = cc.length ? cc.pop() : jsonMode ? expression : statement;
        if (combinator(type, content)) {
          while(cc.length && cc[cc.length - 1].lex)
            cc.pop()();
          if (cx.marked) return cx.marked;
          if (type == "variable" && inScope(state, content)) return "variable-2";
          return style;
        }
      }
    }
  
    // Combinator utils
  
    var cx = {state: null, column: null, marked: null, cc: null};
    function pass() {
      for (var i = arguments.length - 1; i >= 0; i--) cx.cc.push(arguments[i]);
    }
    function cont() {
      pass.apply(null, arguments);
      return true;
    }
    function register(varname) {
      function inList(list) {
        for (var v = list; v; v = v.next)
          if (v.name == varname) return true;
        return false;
      }
      var state = cx.state;
      if (state.context) {
        cx.marked = "def";
        if (inList(state.localVars)) return;
        state.localVars = {name: varname, next: state.localVars};
      } else {
        if (inList(state.globalVars)) return;
        if (parserConfig.globalVars)
          state.globalVars = {name: varname, next: state.globalVars};
      }
    }
  
    // Combinators
  
    var defaultVars = {name: "this", next: {name: "arguments"}};
    function pushcontext() {
      cx.state.context = {prev: cx.state.context, vars: cx.state.localVars};
      cx.state.localVars = defaultVars;
    }
    function popcontext() {
      cx.state.localVars = cx.state.context.vars;
      cx.state.context = cx.state.context.prev;
    }
    function pushlex(type, info) {
      var result = function() {
        var state = cx.state, indent = state.indented;
        if (state.lexical.type == "stat") indent = state.lexical.indented;
        state.lexical = new JSLexical(indent, cx.stream.column(), type, null, state.lexical, info);
      };
      result.lex = true;
      return result;
    }
    function poplex() {
      var state = cx.state;
      if (state.lexical.prev) {
        if (state.lexical.type == ")")
          state.indented = state.lexical.indented;
        state.lexical = state.lexical.prev;
      }
    }
    poplex.lex = true;
  
    function expect(wanted) {
      return function(type) {
        if (type == wanted) return cont();
        else if (wanted == ";") return pass();
        else return cont(arguments.callee);
      };
    }
  
    function statement(type, value) {
      if (type == "var") return cont(pushlex("vardef", value.length), vardef, expect(";"), poplex);
      if (type == "keyword a") return cont(pushlex("form"), expression, statement, poplex);
      if (type == "keyword b") return cont(pushlex("form"), statement, poplex);
      if (type == "{") return cont(pushlex("}"), block, poplex);
      if (type == ";") return cont();
      if (type == "if") return cont(pushlex("form"), expression, statement, poplex, maybeelse);
      if (type == "function") return cont(functiondef);
      if (type == "for") return cont(pushlex("form"), forspec, statement, poplex);
      if (type == "variable") return cont(pushlex("stat"), maybelabel);
      if (type == "switch") return cont(pushlex("form"), expression, pushlex("}", "switch"), expect("{"),
                                        block, poplex, poplex);
      if (type == "case") return cont(expression, expect(":"));
      if (type == "default") return cont(expect(":"));
      if (type == "catch") return cont(pushlex("form"), pushcontext, expect("("), funarg, expect(")"),
                                       statement, poplex, popcontext);
      if (type == "module") return cont(pushlex("form"), pushcontext, afterModule, popcontext, poplex);
      if (type == "class") return cont(pushlex("form"), className, objlit, poplex);
      if (type == "export") return cont(pushlex("form"), afterExport, poplex);
      if (type == "import") return cont(pushlex("form"), afterImport, poplex);
      return pass(pushlex("stat"), expression, expect(";"), poplex);
    }
    function expression(type) {
      return expressionInner(type, false);
    }
    function expressionNoComma(type) {
      return expressionInner(type, true);
    }
    function expressionInner(type, noComma) {
      if (cx.state.fatArrowAt == cx.stream.start) {
        var body = noComma ? arrowBodyNoComma : arrowBody;
        if (type == "(") return cont(pushcontext, pushlex(")"), commasep(pattern, ")"), poplex, expect("=>"), body, popcontext);
        else if (type == "variable") return pass(pushcontext, pattern, expect("=>"), body, popcontext);
      }
  
      var maybeop = noComma ? maybeoperatorNoComma : maybeoperatorComma;
      if (atomicTypes.hasOwnProperty(type)) return cont(maybeop);
      if (type == "function") return cont(functiondef);
      if (type == "keyword c") return cont(noComma ? maybeexpressionNoComma : maybeexpression);
      if (type == "(") return cont(pushlex(")"), maybeexpression, comprehension, expect(")"), poplex, maybeop);
      if (type == "operator" || type == "spread") return cont(noComma ? expressionNoComma : expression);
      if (type == "[") return cont(pushlex("]"), arrayLiteral, poplex, maybeop);
      if (type == "{") return contCommasep(objprop, "}", null, maybeop);
      return cont();
    }
    function maybeexpression(type) {
      if (type.match(/[;\}\)\],]/)) return pass();
      return pass(expression);
    }
    function maybeexpressionNoComma(type) {
      if (type.match(/[;\}\)\],]/)) return pass();
      return pass(expressionNoComma);
    }
  
    function maybeoperatorComma(type, value) {
      if (type == ",") return cont(expression);
      return maybeoperatorNoComma(type, value, false);
    }
    function maybeoperatorNoComma(type, value, noComma) {
      var me = noComma == false ? maybeoperatorComma : maybeoperatorNoComma;
      var expr = noComma == false ? expression : expressionNoComma;
      if (value == "=>") return cont(pushcontext, noComma ? arrowBodyNoComma : arrowBody, popcontext);
      if (type == "operator") {
        if (/\+\+|--/.test(value)) return cont(me);
        if (value == "?") return cont(expression, expect(":"), expr);
        return cont(expr);
      }
      if (type == "quasi") { cx.cc.push(me); return quasi(value); }
      if (type == ";") return;
      if (type == "(") return contCommasep(expressionNoComma, ")", "call", me);
      if (type == ".") return cont(property, me);
      if (type == "[") return cont(pushlex("]"), maybeexpression, expect("]"), poplex, me);
    }
    function quasi(value) {
      if (value.slice(value.length - 2) != "${") return cont();
      return cont(expression, continueQuasi);
    }
    function continueQuasi(type) {
      if (type == "}") {
        cx.marked = "string-2";
        cx.state.tokenize = tokenQuasi;
        return cont();
      }
    }
    function arrowBody(type) {
      findFatArrow(cx.stream, cx.state);
      if (type == "{") return pass(statement);
      return pass(expression);
    }
    function arrowBodyNoComma(type) {
      findFatArrow(cx.stream, cx.state);
      if (type == "{") return pass(statement);
      return pass(expressionNoComma);
    }
    function maybelabel(type) {
      if (type == ":") return cont(poplex, statement);
      return pass(maybeoperatorComma, expect(";"), poplex);
    }
    function property(type) {
      if (type == "variable") {cx.marked = "property"; return cont();}
    }
    function objprop(type, value) {
      if (type == "variable") {
        cx.marked = "property";
        if (value == "get" || value == "set") return cont(getterSetter);
      } else if (type == "number" || type == "string") {
        cx.marked = jsonldMode ? "property" : (type + " property");
      } else if (type == "[") {
        return cont(expression, expect("]"), afterprop);
      }
      if (atomicTypes.hasOwnProperty(type)) return cont(afterprop);
    }
    function getterSetter(type) {
      if (type != "variable") return pass(afterprop);
      cx.marked = "property";
      return cont(functiondef);
    }
    function afterprop(type) {
      if (type == ":") return cont(expressionNoComma);
      if (type == "(") return pass(functiondef);
    }
    function commasep(what, end) {
      function proceed(type) {
        if (type == ",") {
          var lex = cx.state.lexical;
          if (lex.info == "call") lex.pos = (lex.pos || 0) + 1;
          return cont(what, proceed);
        }
        if (type == end) return cont();
        return cont(expect(end));
      }
      return function(type) {
        if (type == end) return cont();
        return pass(what, proceed);
      };
    }
    function contCommasep(what, end, info) {
      for (var i = 3; i < arguments.length; i++)
        cx.cc.push(arguments[i]);
      return cont(pushlex(end, info), commasep(what, end), poplex);
    }
    function block(type) {
      if (type == "}") return cont();
      return pass(statement, block);
    }
    function maybetype(type) {
      if (isTS && type == ":") return cont(typedef);
    }
    function typedef(type) {
      if (type == "variable"){cx.marked = "variable-3"; return cont();}
    }
    function vardef() {
      return pass(pattern, maybetype, maybeAssign, vardefCont);
    }
    function pattern(type, value) {
      if (type == "variable") { register(value); return cont(); }
      if (type == "[") return contCommasep(pattern, "]");
      if (type == "{") return contCommasep(proppattern, "}");
    }
    function proppattern(type, value) {
      if (type == "variable" && !cx.stream.match(/^\s*:/, false)) {
        register(value);
        return cont(maybeAssign);
      }
      if (type == "variable") cx.marked = "property";
      return cont(expect(":"), pattern, maybeAssign);
    }
    function maybeAssign(_type, value) {
      if (value == "=") return cont(expressionNoComma);
    }
    function vardefCont(type) {
      if (type == ",") return cont(vardef);
    }
    function maybeelse(type, value) {
      if (type == "keyword b" && value == "else") return cont(pushlex("form"), statement, poplex);
    }
    function forspec(type) {
      if (type == "(") return cont(pushlex(")"), forspec1, expect(")"), poplex);
    }
    function forspec1(type) {
      if (type == "var") return cont(vardef, expect(";"), forspec2);
      if (type == ";") return cont(forspec2);
      if (type == "variable") return cont(formaybeinof);
      return pass(expression, expect(";"), forspec2);
    }
    function formaybeinof(_type, value) {
      if (value == "in" || value == "of") { cx.marked = "keyword"; return cont(expression); }
      return cont(maybeoperatorComma, forspec2);
    }
    function forspec2(type, value) {
      if (type == ";") return cont(forspec3);
      if (value == "in" || value == "of") { cx.marked = "keyword"; return cont(expression); }
      return pass(expression, expect(";"), forspec3);
    }
    function forspec3(type) {
      if (type != ")") cont(expression);
    }
    function functiondef(type, value) {
      if (value == "*") {cx.marked = "keyword"; return cont(functiondef);}
      if (type == "variable") {register(value); return cont(functiondef);}
      if (type == "(") return cont(pushcontext, pushlex(")"), commasep(funarg, ")"), poplex, statement, popcontext);
    }
    function funarg(type) {
      if (type == "spread") return cont(funarg);
      return pass(pattern, maybetype);
    }
    function className(type, value) {
      if (type == "variable") {register(value); return cont(classNameAfter);}
    }
    function classNameAfter(_type, value) {
      if (value == "extends") return cont(expression);
    }
    function objlit(type) {
      if (type == "{") return contCommasep(objprop, "}");
    }
    function afterModule(type, value) {
      if (type == "string") return cont(statement);
      if (type == "variable") { register(value); return cont(maybeFrom); }
    }
    function afterExport(_type, value) {
      if (value == "*") { cx.marked = "keyword"; return cont(maybeFrom, expect(";")); }
      if (value == "default") { cx.marked = "keyword"; return cont(expression, expect(";")); }
      return pass(statement);
    }
    function afterImport(type) {
      if (type == "string") return cont();
      return pass(importSpec, maybeFrom);
    }
    function importSpec(type, value) {
      if (type == "{") return contCommasep(importSpec, "}");
      if (type == "variable") register(value);
      return cont();
    }
    function maybeFrom(_type, value) {
      if (value == "from") { cx.marked = "keyword"; return cont(expression); }
    }
    function arrayLiteral(type) {
      if (type == "]") return cont();
      return pass(expressionNoComma, maybeArrayComprehension);
    }
    function maybeArrayComprehension(type) {
      if (type == "for") return pass(comprehension, expect("]"));
      if (type == ",") return cont(commasep(expressionNoComma, "]"));
      return pass(commasep(expressionNoComma, "]"));
    }
    function comprehension(type) {
      if (type == "for") return cont(forspec, comprehension);
      if (type == "if") return cont(expression, comprehension);
    }
  
    // Interface
  
    return {
      startState: function(basecolumn) {
        var state = {
          tokenize: tokenBase,
          lastType: "sof",
          cc: [],
          lexical: new JSLexical((basecolumn || 0) - indentUnit, 0, "block", false),
          localVars: parserConfig.localVars,
          context: parserConfig.localVars && {vars: parserConfig.localVars},
          indented: 0
        };
        if (parserConfig.globalVars) state.globalVars = parserConfig.globalVars;
        return state;
      },
  
      token: function(stream, state) {
        if (stream.sol()) {
          if (!state.lexical.hasOwnProperty("align"))
            state.lexical.align = false;
          state.indented = stream.indentation();
          findFatArrow(stream, state);
        }
        if (state.tokenize != tokenComment && stream.eatSpace()) return null;
        var style = state.tokenize(stream, state);
        if (type == "comment") return style;
        state.lastType = type == "operator" && (content == "++" || content == "--") ? "incdec" : type;
        return parseJS(state, style, type, content, stream);
      },
  
      indent: function(state, textAfter) {
        if (state.tokenize == tokenComment) return CodeMirror.Pass;
        if (state.tokenize != tokenBase) return 0;
        var firstChar = textAfter && textAfter.charAt(0), lexical = state.lexical;
        // Kludge to prevent 'maybelse' from blocking lexical scope pops
        for (var i = state.cc.length - 1; i >= 0; --i) {
          var c = state.cc[i];
          if (c == poplex) lexical = lexical.prev;
          else if (c != maybeelse) break;
        }
        if (lexical.type == "stat" && firstChar == "}") lexical = lexical.prev;
        if (statementIndent && lexical.type == ")" && lexical.prev.type == "stat")
          lexical = lexical.prev;
        var type = lexical.type, closing = firstChar == type;
  
        if (type == "vardef") return lexical.indented + (state.lastType == "operator" || state.lastType == "," ? lexical.info + 1 : 0);
        else if (type == "form" && firstChar == "{") return lexical.indented;
        else if (type == "form") return lexical.indented + indentUnit;
        else if (type == "stat")
          return lexical.indented + (state.lastType == "operator" || state.lastType == "," ? statementIndent || indentUnit : 0);
        else if (lexical.info == "switch" && !closing && parserConfig.doubleIndentSwitch != false)
          return lexical.indented + (/^(?:case|default)\b/.test(textAfter) ? indentUnit : 2 * indentUnit);
        else if (lexical.align) return lexical.column + (closing ? 0 : 1);
        else return lexical.indented + (closing ? 0 : indentUnit);
      },
  
      electricChars: ":{}",
      blockCommentStart: jsonMode ? null : "/*",
      blockCommentEnd: jsonMode ? null : "*/",
      lineComment: jsonMode ? null : "//",
      fold: "brace",
  
      helperType: jsonMode ? "json" : "javascript",
      jsonldMode: jsonldMode,
      jsonMode: jsonMode
    };
  });
  
  CodeMirror.defineMIME("text/javascript", "javascript");
  CodeMirror.defineMIME("text/ecmascript", "javascript");
  CodeMirror.defineMIME("application/javascript", "javascript");
  CodeMirror.defineMIME("application/ecmascript", "javascript");
  CodeMirror.defineMIME("application/json", {name: "javascript", json: true});
  CodeMirror.defineMIME("application/x-json", {name: "javascript", json: true});
  CodeMirror.defineMIME("application/ld+json", {name: "javascript", jsonld: true});
  CodeMirror.defineMIME("text/typescript", { name: "javascript", typescript: true });
  CodeMirror.defineMIME("application/typescript", { name: "javascript", typescript: true });
  
  