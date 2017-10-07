/*var logicLevel = require('./logicLevel.js');
var cManager = require('cache-manager');
var rStore = require('cache-manager-redis');

var rCache = cacheManager.caching({
    store: redisStore,
    host: 'localhost', // default value
    port: 6379, // default value
    auth_pass: 1360,
    db: 0,
    ttl: 86400
});

var ttl = 60;

// listen for redis connection error event
redisCache.store.events.on('redisError', function(error) {
    // handle error here
    console.log(error);
});

redisCache.set('foo', 'bar', { ttl: ttl }, function(err) {
    if (err) {
      throw err;
    }

    redisCache.get('foo', function(err, result) {
        console.log(result);
        // >> 'bar'
        redisCache.del('foo', function(err) {});
    });
});

function getUser(id, cb) {
    setTimeout(function () {
        console.log("Returning user from slow database.");
        cb(null, {id: id, name: 'Bob'});
    }, 100);
}

var userId = 123;
var key = 'user_' + userId;

// Note: ttl is optional in wrap()
redisCache.wrap(key, function (cb) {
    getUser(userId, cb);
}, { ttl: ttl }, function (err, user) {
    console.log(user);

    // Second time fetches user from redisCache
    redisCache.wrap(key, function (cb) {
        getUser(userId, cb);
    }, function (err, user) {
        console.log(user);
    });
});
*/
//dependencies needed
var redis = require('redis');
var distanceAPI = require('./distanceAPI.js');
var SqootAPI = require('./SqootAPI.js');
const RedisServer = require('redis-server');
const server = new RedisServer(6379);

server.open(function(err){
  if(err === null)
  {
    console.log('Redis listening on port 6379');
  }
  else {
    console.log(err);
  }
});

var client = redis.createClient({port: 6379});

client.on('error', function(err) {
  console.log("Error " + err);
});

exports.sqoot = function(loca, rad, pagenum, callback)
{
  client.get(`sqoot-${loca}:${rad}:${pagenum}`, function(error, result){
	console.log(result);
    if(result == null)
    {
      SqootAPI.sqoot(loca, rad, pagenum, function(response){
        client.set(`sqoot-${loca}:${rad}:${pagenum}`,JSON.stringify(response));
	console.log(response);
        callback(response);
      });
    }
    else {
	console.log(JSON.parse(result));
      callback(JSON.parse(result));
    }
  });
}

exports.getDistance = function(location, address, mode, callback)
{
  client.get(`dist-${location}:${address}:${mode}`, function(error, result){
    if(result == null)
    {
      distanceAPI.getDistance(location, address, mode, function(response){
        client.set(`dist-${location}:${address}:${mode}`, JSON.stringify(response));
        callback(response);
      });
    }
    else {
      callback(JSON.parse(result));
    }
  });
}
