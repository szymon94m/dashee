(function(){
    // Parent class for automation.
    var automation = function(opts){
 
        var that = {};
        opts = opts || {};
        that.paused = false;

        // Given the current the state of afairs
        // return dashee commands.
        that.update = function(){
        }

        return that;
    };
      
    Automation = automation;

})();