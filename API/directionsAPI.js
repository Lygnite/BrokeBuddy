var fs = require('fs');
var mapsApi = require('@google/maps');
var mapsApiClient = null;
fs.readFile('./.apiKeys/googleMaps', 'utf8', function(err, data) {
  if(err) {
    return console.log(err);
  }
  mapsApiClient = mapsApi.createClient({
    key: data.substring(0,data.length - 1)
  });
});

//location start;
//location dest;
//travelMode mode; //one of 'driving', 'walking', 'bicycling', 'transit'
//locations points; //separate with '|'
//avoids avoid; //any of 'tolls', 'highways', 'ferries', separate with '|'
exports.getDirection = function(start,dest,mode,callback)
{
  mapsApiClient.directions({
    origin: start,
    destination: dest
  }, function(err,res){
    if(!err)
    {
      callback(res.json);
    }
    else {
			console.log(err);
    }
  });
}
