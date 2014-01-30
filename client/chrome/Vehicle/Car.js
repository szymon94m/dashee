// 
(function(){
    var car = function(opts){
 
        opts = opts || {};
        var that = Vehicle(opts);

        var simCar = that.getSimVehicle();
        console.log(simCar);
        
        that.read = function(commands){
            that.setThrottle(commands[2]);
            that.setYaw(commands[1]);
        }

        //given commands update your position
        that.update = function(){
            simCar.setThrottle(that.getThrottle());
            simCar.setYaw(that.getYaw());
        }

        return that;

      };
      
      VehicleCar = car;

})();