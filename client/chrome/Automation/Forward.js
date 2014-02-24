(function(){
    // Automatically move the car forward
    var forward = function(opts){
 
        var that = Automation();
        opts = opts || {};
        var vehicle = opts.vehicle || false;
        var proximitySensor = opts.proximitySensor || false;
        var goBack = false;
        // Given the current the state of afairs
        // return dashee commands.
        that.update = function(){

            if(!vehicle)
                console.error('Automation needs a vehicle');

            if (proximitySensor.read() == 0 && !goBack)
            {
                vehicle.setThrottle(255); 
                vehicle.setYaw(128); 
            }
            else
            {
                if(!goBack){
                    goBack = true;
                    setTimeout(function(){
                        goBack = false;
                    },1000);
                }
                vehicle.setThrottle(0);
                vehicle.setYaw(128);
            }

            vehicle.update();
        }

        return that;
    };
      
    AutomationForward = forward;

})();