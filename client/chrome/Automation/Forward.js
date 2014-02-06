(function(){
    // Automatically move the car forward
    var forward = function(opts){
 
        var that = Automation();
        opts = opts || {};
        var vehicle = opts.vehicle || false;
        var proximitySensor = opts.proximitySensor || false;

        // Given the current the state of afairs
        // return dashee commands.
        that.update = function(){

            if(!vehicle)
                console.error('Automation needs a vehicle');

            //console.log("forward proximitySensor" + proximitySensor.read());



            if (proximitySensor.read() == 0)
            {
                vehicle.setThrottle(255);
                vehicle.setYaw(0); 
            }
            else
            {
                vehicle.setThrottle(100);
                vehicle.setYaw(200); 
            }

            vehicle.update();
        }

        return that;
    };
      
    AutomationForward = forward;

})();