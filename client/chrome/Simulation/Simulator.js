/*
 * Begin box2D simulated environment, intiazing the choosen world.
 */

/* Globals @TODO: Make these not global, due to copying from playground */
var b2D = {
    DEGTORAD : 0.0174532925199432957,
    RADTODEG : 57.295779513082320876,
    scale : 100.0,
    canvasContext: null,
    canvasWidth : 900,
    canvasHeight : 600,
}


// Define box2D shorthands
var b2Vec2 = Box2D.Common.Math.b2Vec2
, b2AABB = Box2D.Collision.b2AABB
, b2BodyDef = Box2D.Dynamics.b2BodyDef
, b2Body = Box2D.Dynamics.b2Body
, b2FixtureDef = Box2D.Dynamics.b2FixtureDef
, b2Fixture = Box2D.Dynamics.b2Fixture
, b2World = Box2D.Dynamics.b2World
, b2MassData = Box2D.Collision.Shapes.b2MassData
, b2PolygonShape = Box2D.Collision.Shapes.b2PolygonShape
, b2CircleShape = Box2D.Collision.Shapes.b2CircleShape
, b2DebugDraw = Box2D.Dynamics.b2DebugDraw
, b2MouseJointDef =  Box2D.Dynamics.Joints.b2MouseJointDef
, b2Math = Box2D.Common.Math.b2Math
, b2RevoluteJointDef = Box2D.Dynamics.Joints.b2RevoluteJointDef;


(function(){
    var sim = function(world){
        var that = {};
        // Set the canvas to the current page size.
        var worldCanvas = document.getElementById("world1")
        
        
        // Set the canvas size to the current window size
        function updateCanvasSize(){
            b2D.canvasContext = worldCanvas.getContext("2d");
            b2D.canvasHeight = window.innerHeight;
            b2D.canvasWidth = window.innerWidth;   
            worldCanvas.width = b2D.canvasWidth;
            worldCanvas.height = b2D.canvasHeight;
        }

        // Onload run this to get initial values.
        updateCanvasSize();

        // This is our current simulated environment definition.
        var world = World1();
        
        // Return current world vehicle in this case a car.
        that.getVehicle = function(){
            return world.getVehicle();
        }

        that.update = function(){
            world.update();
        }

        return that;
    }
    Simulator = sim;
})();