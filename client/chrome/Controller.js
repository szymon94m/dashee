(function(){
    // Maintains state of world, updating objects step by step
    // according to user input and world factor such as friction and
    // collisions.
    var controller = function(opts, c){
 
        var that = {};
        opts = opts || {};

        /*var c=document.getElementById("stage");
        var ctx=c.getContext("2d");*/

        console.log(" --- Create UDP Server");
        var server = ServerUDP();
        console.log(" --- Starting Simulator");
        var simulator = Simulator();
        var vehicle = VehicleCar({simulator:simulator});
        var automation = AutomationParty({vehicle:vehicle, proximitySensor:simulator.getSensorProximity()});
        
        console.log(" --- Start Main Animation Loop");

        // Update feedback on server commands and packet rate.
        function updateHudFields(){
            // @TODO use some MVC solution to update these values
            var packetsPerSec = document.getElementById('packets-per-sec');
            var throttleLabel = document.getElementById('throttle-val');
            var steerLabel = document.getElementById('steer-val');
        }

        var startTime = new Date().getTime();
        var lastSec = 0;

        that.applyStep = function(){
            var curTime = new Date().getTime();
            var secondsPassed = Math.round((curTime - startTime) / 1000);
            
            //Get current server commands
            var serverBuffer;
            var serverBuffer = server.getBufferBytes();

            //1
            if (serverBuffer){
                vehicle.read(serverBuffer);
                vehicle.update();
            }else{
                automation.update();
            }

            simulator.update();
            //vehicle.update();
        }

        function animloop(){
            window.webkitRequestAnimationFrame(animloop);
            that.applyStep();
        };

        animloop();

        return that;
      };
      
      Controller = controller;

})();