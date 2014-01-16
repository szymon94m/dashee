(function(){
  // UDP server to listen for dashee formatted commands.
  var udp = function(opts, c){
    var that = {};
    var socketId;
    var rotator;
    var packetCountReset = 1000; // Reset packet count after 1s
    var packetCount = []; // Store an array of packet counts per second
    var curPacketCount = 0; 
    var tempPacketCount = 0; 
    var samples=0;
    var prevTime = new Date().getTime();
    var curTime = new Date().getTime();
    
    // This server exists to update these commands and expose it to
    // the outside world.
    var bufferBytes;


    // UDP Server listen for packets on port 2047
    chrome.socket.create('udp', function(socketInfo) {
      socketId = socketInfo.socketId;
      chrome.socket.bind(socketId, '0.0.0.0', 2047, function(result) {
      	if (result != 0) {
          chrome.socket.destroy(socketId);
          that.handleError("Error on joinGroup(): ", result);
        } else {
          that.poll();

        }
      });
    });

    that.poll = function () {
      if (socketId) {
        chrome.socket.recvFrom(socketId, 10, function (result) {
          if (result.resultCode >= 0) {
            startedPolling = true;
            bufferBytes = that.arrayBufferToView(result.data);
            that.poll();
          } else {
            console.log(result.resultCode);
            that.handleError("", result.resultCode);
            that.disconnect();
          }
        });
      }
    };

    // Return current car commands
    that.getBufferBytes = function(){
      var tempBytes = bufferBytes;
      bufferBytes = false;
      return tempBytes;
    }

    that.disconnect = function (callback) {
      socketId = undefined;
      chrome.socket.destroy(socketId);
      this.onDisconnected();
      if (callback) {
        callback.call(this);
      }
    };

    that.handleError = function (additionalMessage, alternativeMessage) {
      var err = chrome.runtime.lastError;
      err = err && err.message || alternativeMessage;
      console.log(additionalMessage + err);
    };

    that.arrayBufferToView = function (arrayBuffer) {
      // UTF-16LE
      return new Uint8Array(arrayBuffer);
    };

    that.arrayBufferToString = function (arrayBuffer) {
      // UTF-16LE
      return String.fromCharCode.apply(String, new Uint8Array(arrayBuffer));
    };

    that.stringToArrayBuffer = function (string) {
      // UTF-16LE
      var buf = new ArrayBuffer(string.length * 2);
      var bufView = new Uint16Array(buf);
      for (var i = 0, strLen = string.length; i < strLen; i++) {
        bufView[i] = string.charCodeAt(i);
      }
      return buf;
    };

    return that;
  }

  ServerUDP = udp;

})();

