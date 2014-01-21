// Tire Definition, based on turorial at http://www.iforce2d.net/b2dtut/top-down-car
(function(){
    // Box 2D tire class takes the Box 2D World as a parameter.
    // Requires box 2D.
    var tire = function(world, opts){
        var that = {};
        var opts = opts || {};
        that.body;

        // Forward and backward force is applied the maxDriveForce rate
        // until the speed is reached
        var maxForwardSpeed = opts.maxForwardSpeed || 7;
        var maxBackwardSpeed = opts.maxBackwardSpeed || -3;
        var maxDriveForce = opts.maxBackwardSpeed || 0.5;
        var maxLateralImpulse = opts.maxLateralImpulse || 0.025;
        var desiredSpeed = 0;
        var powerMappingReverse;
        var powerMappingForward;


        // Initialise Our Tire.
        (function init(){
            updateMappings();
            initBody();
        })();


        // Initialize box2D body.
        function initBody(){
            // Create the box 2D body.
            var bodyDef = new b2BodyDef;

            // Set type to dynamic unlike static default.
            bodyDef.type = b2Body.b2_dynamicBody;
            bodyDef.angle = 0;
            
            // Add to simulation.
            that.body = world.CreateBody(bodyDef);

            var fixDef = new b2FixtureDef;
            fixDef.density = 1.0; // Weight relative to size
            fixDef.friction = 0.5;
            fixDef.restitution = 0.2; // Bouncyness
            fixDef.shape = new b2PolygonShape;

            // The SetAsBox function takes half the width and height
            // So the figures below need to be doubled and it's in meters.
            // 0.0625 is 12.5 cm in width about as small as Box2D supports.
            fixDef.shape.SetAsBox(0.0625, 0.15);
            that.body.CreateFixture(fixDef);
        }

        // Mapping between box2d speed and dashee command values.
        function updateMappings(){
            powerMappingReverse = rangeMapping(0, 128, maxBackwardSpeed, 0);
            powerMappingForward = rangeMapping(128, 255, 0, maxForwardSpeed);
        }

        // Velocity to the side.
        function getLateralVelocity() {
            //console.log(that.body.GetWorldVector());
            var currentRightNormal = that.body.GetWorldVector( new b2Vec2(1,0) );
            //console.log(that.body.GetLinearVelocity());
            return b2Math.MulFV(
            b2Math.Dot( currentRightNormal, that.body.GetLinearVelocity() ),
            currentRightNormal);
        }

        // Velocity to the 'front.'
        function getForwardVelocity() {
            var currentRightNormal = that.body.GetWorldVector( new b2Vec2(0,1) );
            return b2Math.MulFV(
            b2Math.Dot( currentRightNormal, that.body.GetLinearVelocity() ),
            currentRightNormal);
        }

        // As this is a tire we need to kill lateral movement,
        // and also gradually kill forward movement to simulate friction,
        // on the top down surface.
        function updateFriction() {
            // Kill lateral velocity, optionally allow some to get skid effect.
            var negativeLateralImpulse = b2Math.MulFV(that.body.GetMass(), getLateralVelocity().GetNegative());

            if ( negativeLateralImpulse.Length() > maxLateralImpulse ){
                // Anything over our max lateral negativeLateralImpulse should be applied, so in effect we need to reduce the impluse
                // to be cancelled out.
                negativeLateralImpulse = b2Math.MulFV(maxLateralImpulse / negativeLateralImpulse.Length(), negativeLateralImpulse);
            }

            var currentTraction = 1;
            that.body.ApplyImpulse( b2Math.MulFV(currentTraction, negativeLateralImpulse), that.body.GetWorldCenter() );
            
            // Kill angular velocity
            that.body.ApplyAngularImpulse( 0.1 * that.body.GetInertia() * -that.body.GetAngularVelocity() );

            var currentForwardNormal = getForwardVelocity();
            var currentForwardSpeed = currentForwardNormal.Normalize();
            var dragForceMagnitude = -0.1 * currentForwardSpeed;
            that.body.ApplyForce( b2Math.MulFV(dragForceMagnitude , currentForwardNormal), that.body.GetWorldCenter() );
        }

        // Based on the desiredSpeed apply forward force on the tire.
        function updateDrive() {
            //find current speed in forward direction
            var currentForwardNormal = that.body.GetWorldVector(new b2Vec2(0,1) );
            var currentSpeed = b2Math.Dot( getForwardVelocity(), currentForwardNormal );

            //apply necessary force
            var force = 0;
            if(Math.abs(currentSpeed) < 0.15) currentSpeed = 0;
            if ( desiredSpeed > currentSpeed )
                force = maxDriveForce;
            else if ( desiredSpeed < currentSpeed )
                force = -maxDriveForce;
            else
                return;
            that.body.ApplyForce(b2Math.MulFV(force , currentForwardNormal), that.body.GetWorldCenter() );
        }

        // Take dashee power unit and convert to required speed.
        that.setPower = function(in_power_val){
            if(in_power_val>128){
                desiredSpeed = powerMappingForward(in_power_val);
            }else{
                desiredSpeed = powerMappingReverse(in_power_val);
            }
        }

        // Apply force and friction to the tire.
        that.update = function(){
            updateFriction();
            updateDrive();
        }

        that.setCharacteristics = function(in_maxForwardSpeed, in_maxBackwardSpeed, in_tireMaxDriveForce, in_tireMaxLateralImpulse){
            maxForwardSpeed = in_maxForwardSpeed;
            maxBackwardSpeed = in_maxBackwardSpeed;
            maxDriveForce = in_tireMaxDriveForce;
            maxLateralImpulse = in_tireMaxLateralImpulse;
            updateMappings()
        }

        return that;
    }
    ModelTire = tire;
})();
