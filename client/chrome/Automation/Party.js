(function(){
    // Automatically move the car party
    var party = function(opts){
 
        var that = Automation();
        opts = opts || {};
        var vehicle = opts.vehicle || false;
        var proximitySensor = opts.proximitySensor || false;

        // Given the current the state of afairs
        // return dashee commands.
        that.update = function(){

            if(!vehicle)
                console.error('Automation needs a vehicle');

            if (proximitySensor.read() == 0)
            {
                vehicle.setThrottle(255);
                vehicle.setYaw(0); 
            }
            else
            {
                vehicle.setThrottle(0);
                vehicle.setYaw(255); 
            }

            vehicle.update();
        };

        return that;
    };
      
    AutomationParty = party;

})();