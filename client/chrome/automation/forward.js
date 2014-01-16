(function(){
    // Automatically move the car forward
    var forward = function(opts){
 
        var that = Automation();
        opts = opts || {};

        // Given the current the state of afairs
        // return dashee commands.
        that.update = function(){
            return {
                'throttle':255,
                'steering':50,
            };
        }

        return that;
    };
      
    AutomationForward = forward;

})();