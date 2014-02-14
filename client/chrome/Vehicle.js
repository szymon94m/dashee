// Parent class for all vehicles.
(function(){
    var vehicle = function(opts, c){
 
        var that = {};
        opts = opts || {};
        var throttle = 128;
        var yaw = 128;

        var sim = opts.simulator || false;
        
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
            if (sim == false)
                console.error("No simVehicle set");
            return sim.getVehicle();
        }

        //given commands update your position
        that.update = function(){
            console.error("Must implement");
        }

        return that;
      };
      
      Vehicle = vehicle;

})();