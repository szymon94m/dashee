/**
 * Listens for the app launching then creates the window
 *
 * @see http://developer.chrome.com/trunk/apps/app.runtime.html
 * @see http://developer.chrome.com/trunk/apps/app.window.html
 */
chrome.app.runtime.onLaunched.addListener(function() {
  // Center window on screen.
  var screenWidth = screen.availWidth;
  var screenHeight = screen.availHeight;
  var width = screen.availWidth;
  var height = 300;

  chrome.app.window.create('index.html', {
    bounds: {
      left: 0,
      top: 0,
      width:screen.availWidth,
      height:screen.availHeight
    }
  });
});
