// Simulated proximity sensor. 
(function(){
    var sensorProximity = function(opts, c){
        var that = ModelSensor(opts);
        
        opts = opts || {};
        var x = opts.x || 0;
        var y = opts.y || 0;
        var angle = opts.angle || 0;
        var length = opts.length || 1.6;
        var world = opts.world || console.error('No World set biatches!');

        var proximity = 0;
        var p1 = 0;
        var p2 = 0; 

        // Perform the Actual raycast
        function raycast(p1, p2){
            var closestItem = 0;
            world.RayCast(function(fixture, point, normal, fraction){
                if(!closestItem || fraction < closestItem) closestItem = fraction;
                return 1;
            }, p1, p2);
            proximity = closestItem;
        }

        //given commands update your position
        that.update = function(){
            var sinAngle = Math.sin(angle);
            var cosAngle = Math.cos(angle);
            p1 = new b2Vec2(x, y);
            p2 = new b2Vec2(x + length*sinAngle, y + length*cosAngle); 
            raycast(p1, p2);
        }

        // Return the recent value of proximity
        that.read = function(){
            return proximity;
        }

        // Change the sensors location and view angle
        that.moveTo = function(in_x, in_y, in_angle){
            x = in_x;
            y = in_y;
            angle = in_angle * -1;

            that.update();
        }

        // Dont use this please
        that.setLength = function(in_length){
            if (in_length < 0) console.error('Length must be greater than 0');
            length = in_length;
        }

        that.debugDraw = function(context, scale){
            context.beginPath();
            context.moveTo(p1.x*scale, p1.y*scale);
            context.lineTo(p2.x*scale, p2.y*scale);
            context.stroke();
        }
    
        return that;
    
    };

    ModelSensorProximity = sensorProximity;
})();