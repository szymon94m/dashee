// Box 2D car definition
// Build a car with our tires.
// Based on turorial at http://www.iforce2d.net/b2dtut/top-down-car
(function(){
    var car = function(world){
        
        var that = {};

        // Initilized box2d models
        that.tires = [];
        that.body;

        // CONSTANTS
        var LOCK_ANGLE = 35 * b2D.DEGTORAD;
        var STEP_PER_TIME = (160 * b2D.DEGTORAD) / 60.0; //from lock to lock in 0.5 sec

        // The mapped desired angle value set by setYaw
        var desiredYawAngle = 0;

        // Mapping range used for steering
        var steerMapping = rangeMapping(0,255,-LOCK_ANGLE,LOCK_ANGLE);
        
        // Max speed values
        var maxForwardSpeed = 7;
        var maxBackwardSpeed = -3;

        // Different forces on our tyres
        var backTireMaxDriveForce = 0.3;
        var frontTireMaxDriveForce = 0.5;
        var backTireMaxLateralImpulse = 0.01;
        var frontTireMaxLateralImpulse = 0.025;

        // Handlers to the Joint
        var frontLeftJoint;
        var frontRightJoint;

        // Initialise Our Car.
        (function init(){
            initBody();
            initTires();
        })();

        // Create car body.
        function initBody(){
            var bodyDef = new b2BodyDef;
            bodyDef.type = b2Body.b2_dynamicBody;
            bodyDef.position.Set(2, 2);
            bodyDef.angle = 0;
            that.body = world.CreateBody(bodyDef);
            
            var vertices = [
                new b2Vec2(0.2, 0),
                new b2Vec2(  0.2, 0.6),
                new b2Vec2( -0.2, 0.6),
                new b2Vec2(  -0.2,  0)
            ];

            var polygonShape = new b2PolygonShape;
            polygonShape.SetAsArray( vertices, 4 );

            var fixDef = new b2FixtureDef;
            fixDef.density = 1.0;
            fixDef.friction = 0.0;
            fixDef.restitution = 0.2;
            fixDef.shape = polygonShape;
            that.body.CreateFixture(fixDef);
        }

        // initilise our Joints.
        //
        // TODO: setCharacteristics using:
        //     tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        // 
        function initTires(){
            //prepare common joint parameters
            var jointDef = new b2RevoluteJointDef;
            jointDef.bodyA = that.body;
            jointDef.enableLimit = true;
            jointDef.collideConnected = false;
            jointDef.lowerAngle = 0;
            jointDef.upperAngle = 0;

            //back left tire
            var tireLeft = ModelTire(world);
            tireLeft.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
            jointDef.bodyB = tireLeft.body;
            jointDef.localAnchorA.Set( -0.2, 0 );
            jointDef.localAnchorB.Set( 0, 0 );
            world.CreateJoint( jointDef );
            that.tires.push(tireLeft);

            //back right tire
            var tireRgt = ModelTire(world);
            tireRgt.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
            jointDef.bodyB = tireRgt.body;
            jointDef.localAnchorA.Set( 0.2, 0 );
            jointDef.localAnchorB.Set( 0, 0 );
            world.CreateJoint( jointDef );
            that.tires.push(tireRgt);

            //front left tire
            var tireFLeft = ModelTire(world);
            tireFLeft.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
            jointDef.bodyB = tireFLeft.body;
            jointDef.localAnchorA.Set( -0.2, 0.6 );
            jointDef.localAnchorB.Set( 0, 0 );
            frontLeftJoint = world.CreateJoint( jointDef );
            that.tires.push(tireFLeft);

            //front right tire
            var tireFRgt = ModelTire(world);
            tireFRgt.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
            jointDef.bodyB = tireFRgt.body;
            jointDef.localAnchorA.Set( 0.2, 0.6 );
            jointDef.localAnchorB.Set( 0, 0 );
            frontRightJoint = world.CreateJoint( jointDef );
            that.tires.push(tireFRgt);
        }

        // Set the yaw value after mapping it using steerMapping
        that.setYaw = function(in_val){
            desiredYawAngle = steerMapping(in_val);
        }

        // Set the throttle for each tyres
        that.setThrottle = function(in_val){
            for (var i = 0; i < that.tires.length; i++)
                that.tires[i].setPower(in_val);
        }

        // Update the physical state of the car
        that.update = function() {
            for (var i = 0; i < that.tires.length; i++)
                that.tires[i].update();

            var angleNow = frontLeftJoint.GetJointAngle();
            var angleToTurn = desiredYawAngle - angleNow;
            angleToTurn = b2Math.Clamp(angleToTurn, -STEP_PER_TIME, STEP_PER_TIME);
            var newAngle = angleNow + angleToTurn;

            // Set limits would constain a joint movement, so by setting upper and lower limits,
            // you essentially for newAngle to be the angle of the joint, in this case
            // causing a turned wheel effect.
            frontLeftJoint.SetLimits(newAngle, newAngle);
            frontRightJoint.SetLimits(newAngle, newAngle);
        }

        return that;
    }
    ModelCar = car;
})();
