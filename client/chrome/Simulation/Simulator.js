/*
 * Begin box2D simulated environment, intiazing the choosen world.
 */

/* Globals @TODO: Make these not global, due to copying from playground */
var keys = {};
var keypresses = {
    '38':'DRIVE',
    '87':'DRIVE',
    '40':'REVERSE',
    '83':'REVERSE',
    '39':'RIGHT',
    '68':'RIGHT',
    '37':'LEFT',
    '65':'LEFT'
};
var keypressed;
var DEGTORAD = 0.0174532925199432957;
var RADTODEG = 57.295779513082320876;
var scale = 100.0;
var canvasWidth = 900;
var canvasHeight = 600;

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
        var world = World1();
        // Return current world vehicle in this case a car.
        that.getVehicle = function(){
            return world.getVehicle();
        }
        return that;
    }
    Simulator = sim;
})();