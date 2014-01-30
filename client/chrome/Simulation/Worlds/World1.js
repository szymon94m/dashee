// World1 definition.
(function(){
    var world1 = function(){
        var that = {};
        
        // CONSTANTS
        var GRAVITY = new b2Vec2(0, 0);
        var PROXIMITY_START = 0.6;
        var PROXIMITY_LENGTH = 1.6;
        var TIRE_LENGTH = 0.16;

        // Our world handler
        var world = new b2World(GRAVITY,  true);
        var model;

        // Switch Box2D debug view on and off.
        var debugDraw = true;

        // Track the time that has passed since last box2d step
        // so that box 2d can update accurately.
        var prevTime = new Date().getTime();
        var curTime = new Date().getTime();

        // Initialise Our Car.
        (function init(){
            initWalls();
            initDebug();
            model = ModelCar(world);
        })();

        // Create our world and add walls
        function initWalls(){
            var fixDef = new b2FixtureDef;
            fixDef.density = 1.0;
            fixDef.friction = 0.5;
            fixDef.restitution = 0.2;
            fixDef.shape = new b2PolygonShape;  

            // Note: All measurements are in meters.
            // SetAsBox() measurments are half the desired measurements.
            // Positoned from mid point, so a 0 means half is off screen.
            fixDef.shape.SetAsBox(b2D.canvasWidth/b2D.scale/2, 0.01);

            var bodyDef = new b2BodyDef;

            // Top wall                
            bodyDef.position.Set(b2D.canvasWidth/b2D.scale/2, 0);
            world.CreateBody(bodyDef).CreateFixture(fixDef);
            
            // Bottom wall
            bodyDef.position.Set(b2D.canvasWidth/b2D.scale/2, b2D.canvasHeight/b2D.scale);
            world.CreateBody(bodyDef).CreateFixture(fixDef);

            // Change the original fixDef dimensions for Left and Right
            fixDef.shape.SetAsBox(0.01, b2D.canvasHeight/b2D.scale/2);

            // Left wall
            bodyDef.position.Set(0.0, b2D.canvasHeight/b2D.scale/2);
            world.CreateBody(bodyDef).CreateFixture(fixDef);

            // Right wall
            bodyDef.position.Set(b2D.canvasWidth/b2D.scale, b2D.canvasHeight/b2D.scale/2);
            world.CreateBody(bodyDef).CreateFixture(fixDef);
        }

        // Initilize our world
        function initDebug(){
            //setup debug draw
            var debugDraw = new b2DebugDraw();
            debugDraw.SetSprite(b2D.canvasContext);
            debugDraw.SetDrawScale(b2D.scale);
            debugDraw.SetFillAlpha(0.5);
            debugDraw.SetLineThickness(1.0);
            debugDraw.SetFlags(b2DebugDraw.e_shapeBit | b2DebugDraw.e_jointBit);
            world.SetDebugDraw(debugDraw);    
        }

        // Return the current vehicle
        that.getVehicle = function(){
            return model;
        }

        // Enable debug draw
        that.enableDebugDraw = function(){
            debugDraw = true;
        }

        // Disable debug draw
        that.disableDebugDraw = function(){
            debugDraw = false;
        }

        // Update our models.
        that.update = function() {

            // update object friction;
            model.update();

            // Step through the world
            curTime = new Date().getTime();
            world.Step((curTime - prevTime)/1000, 10, 10);
            prevTime = curTime;

            // Helpfull debug information
            if (debugDraw){ 
                world.DrawDebugData();
                model.debugDraw(b2D.canvasContext, b2D.scale);
            }

            world.ClearForces();
        }

        // Place a proximity sensor on our model.
        function proximitySensor(){
            var carX = model.body.GetPosition().x;
            var carY = model.body.GetPosition().y; 
            var angle = model.body.GetAngle() * -1;
            var sinAngle = Math.sin(angle);
            var cosAngle = Math.cos(angle);
            var p1 = new b2Vec2(carX + PROXIMITY_START*sinAngle, carY + PROXIMITY_START*cosAngle);
            var p2 = new b2Vec2(carX + PROXIMITY_LENGTH*sinAngle, carY + PROXIMITY_LENGTH*cosAngle); 
            
            var closestItem = 0;
            world.RayCast(function(fixture, point, normal, fraction){
                if(!closestItem || fraction < closestItem) closestItem = fraction;
                return 1;
            }, p1, p2);

            // TODO: get the closestItem to be from 0-100, maybe depends on the thing
            return closestItem - TIRE_LENGTH;
        }

        return that;
    }
    World1 = world1;
})();