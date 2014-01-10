(function(){
    // Parent class for vehicle will have magnitude and direction
    // Direction will be a vector, it should start facing "north."
    var vehicle = function(opts, c){
 
        var that = {};
        opts = opts || {};
        var position = Vector2D({x:250, y:250});
        var direction = Vector2D({x:.0, y:-1});
        
        var carOrientation = 270;
        var speed = 0.1;

        var frontLeftWheel = document.getElementById('wheel-1');
        var frontRightWheel = document.getElementById('wheel-2');
        var vehicleElement = document.getElementById('car');
        // Map between dashee steer values realistic wheel turns
        var steerMapping = rangeMapping(0,255,0,60);
        var steerRightMapping = rangeMapping(128,255,0,.8);
        var steerLeftMapping = rangeMapping(0,128,-.8,0);
        var powerMapping = rangeMapping(0,255,-3.5,3.5);
        var commands;

        var simCar = simulator.getVehicle();
        
        that.read = function(in_commands){
            commands = in_commands;
        }

        //given commands update your position
        that.update = function(){
            simCar.setSteer(commands.steering);
            simCar.setPower(commands.throttle);
        }

        return that;
      };
      
      Vehicle = vehicle;

})();