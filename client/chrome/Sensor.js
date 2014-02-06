// Abstact sensor class. 
(function(){
    var sensor = function(opts, c){
        var that = {};
        opts = opts || {};

        //given commands update your position
        that.read = function(){
            console.error('Please implement');
        }

        //given commands update your position
        that.update = function(){
            console.error('Please implement');
        }
    
        return that;
    
    };

    Sensor = sensor;
})();