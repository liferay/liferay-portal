(function ($) {
  $(document).ready(function () {
    if (!Liferay.ThemeDisplay.isSignedIn()) {
      var dockbarSelector = 'div#sign-in, div.sign-in, span.sign-in';

      var elementDockbar = $(dockbarSelector);
      if (elementDockbar.length > 0) {
        elementDockbar.hide();

        $(document).keydown(function (event) {
          event = $.event.fix(event);
          // ctrl+shift/alt+l
          if ((event.shiftKey || event.altKey) && event.ctrlKey && event.keyCode === 76) {
            var elementDockbar = $(dockbarSelector);
            elementDockbar.toggle();
            return false;
          } else {
            return true;
          }
        });
      }
    }
  });
})(jQuery);
if (Liferay.Browser.isIe()) {
  Liferay.Loader.addModule({
    dependencies: [],
    name: 'arena/polyfills',
    path: '/o/frontend-js-web/arena/shim.js'
  });
} else {
  Liferay.Loader.define('arena/polyfills', [], function () {
    return {};
  });
}
