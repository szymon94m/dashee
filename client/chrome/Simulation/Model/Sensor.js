// Simulated proximity sensor. 
(function(){
    var sensor = function(opts, c){
        var that = {};
        
        opts = opts || {};

        //given commands update your position
        that.update = function(){
            console.log('Must implement update');
        }

        // Return the recent value of proximity
        that.read = function(){
            console.log('Must implement read');
        }
    
        return that;
    
    };

    ModelSensor = sensor;
})();