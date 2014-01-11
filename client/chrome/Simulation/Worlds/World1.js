// World1 definition.
(function(){
    var world1 = function(world){
        var that = {};
        
        var gravity = new b2Vec2(0, 0);
        var world = new b2World(gravity,  true);

        var fixDef = new b2FixtureDef;
        fixDef.density = 1.0;
        fixDef.friction = 0.5;
        fixDef.restitution = 0.2;

        var bodyDef = new b2BodyDef;

        // Create walls, all measurements are in meters.
        // Trying to create a room 16ft by 12ft, 4.87 by 3.65 in meters

        var leftOffset = 2.1;

        fixDef.shape = new b2PolygonShape;
        
        // Set as box measurments are half the desired measurements.
        fixDef.shape.SetAsBox(b2D.canvasWidth/b2D.scale/2, 0.01);

        // positoned from mid point, so a 0 means half is off screen.
        
        // Top wall    
        bodyDef.position.Set(b2D.canvasWidth/b2D.scale/2, 0);
        world.CreateBody(bodyDef).CreateFixture(fixDef);
        
        // Bottom wall
        bodyDef.position.Set(b2D.canvasWidth/b2D.scale/2, b2D.canvasHeight/b2D.scale);
        world.CreateBody(bodyDef).CreateFixture(fixDef);

        fixDef.shape.SetAsBox(0.01, b2D.canvasHeight/b2D.scale/2);

        bodyDef.position.Set(0.0, b2D.canvasHeight/b2D.scale/2);
        world.CreateBody(bodyDef).CreateFixture(fixDef);

        bodyDef.position.Set(b2D.canvasWidth/b2D.scale, b2D.canvasHeight/b2D.scale/2);
        world.CreateBody(bodyDef).CreateFixture(fixDef);

        //setup debug draw
        var debugDraw = new b2DebugDraw();
        debugDraw.SetSprite(b2D.canvasContext);
        debugDraw.SetDrawScale(b2D.scale);
        debugDraw.SetFillAlpha(0.5);
        debugDraw.SetLineThickness(1.0);
        debugDraw.SetFlags(b2DebugDraw.e_shapeBit | b2DebugDraw.e_jointBit);
        world.SetDebugDraw(debugDraw);

        var car1 = car(world);

        var carRotation = document.getElementById('car-rotation');
        var carProximity = document.getElementById('range-finder');
        var prevTime = new Date().getTime();
        var curTime = new Date().getTime();

        var update = function() {

            // update object friction;
            car1.update();
            curTime = new Date().getTime();
            world.Step((curTime - prevTime)/1000, 10, 10);
            prevTime = curTime;
            world.DrawDebugData();
            var carX = car1.m_body.GetPosition().x;
            var carY = car1.m_body.GetPosition().y; 
            var angle = car1.m_body.GetAngle();
            //carRotation.innerHTML = "Orientation: " + angle.toFixed(2) + " Radians";
            var rayStartLength =0.6;
            var rayLength = 1.6; //long enough to hit the walls
            var p1 =  new b2Vec2(carX + rayStartLength*Math.sin(-angle), carY + rayStartLength*Math.cos(-angle));
            var p2 =  new b2Vec2(carX + rayLength*Math.sin(-angle), carY + rayLength*Math.cos(-angle)); 
            //carProximity.innerHTML = "Radar: Clear path";
            var closestItem = false;
            world.RayCast(function(fixture, point, normal, fraction){
                if(!closestItem || fraction < closestItem) closestItem = fraction;
                return 1;
            }, p1, p2);
            //if(closestItem !== false)
                //carProximity.innerHTML = "Radar: " + (closestItem-0.16).toFixed(2) + " meters away from an object";
            /*b2D.canvasContext.beginPath();
            b2D.canvasContext.moveTo(p1.x*scale, p1.y*scale);
            b2D.canvasContext.lineTo(p2.x*scale, p2.y*scale);
            b2D.canvasContext.stroke();*/

            b2D.canvasContext.save();
            
            // Calculate image position
            // it moves along from the middle of the car 0.2 meters at an angle perpendicular to the cars angle
            var offsetAngle = ( 128) * b2D.DEGTORAD;
            var carImgPos =  new b2Vec2(carX + 0.44*Math.sin(-angle - offsetAngle ), carY + 0.44*Math.cos(-angle -offsetAngle)); 

            b2D.canvasContext.translate((carImgPos.x)*b2D.scale, carImgPos.y*b2D.scale);
            b2D.canvasContext.rotate(angle);
            //b2D.canvasContext.drawImage(carImg, 0, 0);
            b2D.canvasContext.restore();
            world.ClearForces();
        }
        that.update =  update;
        //window.setInterval(update, 1000 / 60);

        // Return the current vehicle
        that.getVehicle = function(){
            return car1;
        }

        return that;
    }
    World1 = world1;
})();