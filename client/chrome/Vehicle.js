// Parent class for all vehicles.
(function(){
    var vehicle = function(opts, c){
 
        var that = {};
        opts = opts || {};
        var throttle;
        var yaw;

        var simVehicle = simulator.getVehicle();
        
        // Reads commands in dashee byte format.
        that.read = function(){
            console.error("Must implement");
        }

        that.setThrottle = function(throttleVal){
            throttle = throttleVal;
        }

        that.setYaw = function(yawVal){
            yaw = yawVal;
        }

        that.getThrottle = function(){
            return throttle;
        }

        that.getYaw = function(){
            return yaw;
        }

        that.getSimVehicle = function(){
            return simVehicle;
        }

        //given commands update your position
        that.update = function(){
            console.error("Must implement");
        }

        return that;
      };
      
      Vehicle = vehicle;

})();