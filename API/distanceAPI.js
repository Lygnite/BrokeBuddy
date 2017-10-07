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

exports.getDistance = function(location, address, mode, callback)
{
  mapsApiClient.distanceMatrix({
    origins: location,
    destinations: address,
    mode: mode
  }, function(err, response){
    if(!err)
    {
      callback(response.json);
    }
    else {
      console.log(err);
      callback(err);
    }
  }
  );
}
