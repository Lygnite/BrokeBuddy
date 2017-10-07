var httpLevel = require('./httpLevel.js');
var directionsApi = require('./directionsAPI.js');
var distanceApi = require('./cacheServer.js');
var sqootApi = require('./cacheServer.js');

exports.getRests = function(latitude, longitude, radius, index, callback)
{
  var location = `${latitude},${longitude}`;
  sqootApi.sqoot(location, radius, index, function(response){
    console.log(response.query);
    if(response.hasOwnProperty('error'))
    {
      console.log(response.error);
      callback(response);
      return;
    }
    if(index >= response.query.total)
    {
      console.log(`Index ${index} out of bounds: ${response.query.total}`);
      callback(`Index ${index} out of bounds: ${response.query.total}`);
      return;
    }
    var numDeals = response.deals.length;
    var deals = {
      restaurants: []
    }
    for(i = 0; i < numDeals; ++i)
    {
      var deal = response.deals[i].deal;
      var rest = {
        name: deal.merchant.name,
        address: (deal.merchant.address != null ? deal.merchant.address : ""),
        location: `${deal.merchant.latitude},${deal.merchant.longitude}`,
        image_url: deal.image_url + "&geometry=150x150",
        distance: 2,
        short_title: deal.short_title,
        title: deal.title,
        fine_print: deal.fine_print,
        url: deal.url,
        price: parseMinPrice(deal.fine_print)
      }

      deals.restaurants.push(rest);
    }
    getDistances(location, deals, function(response){
    //var response = deals;
      //sortByPrice(response);
      callback(response.restaurants[0]);
    });
  });
}

function getDistances(location, deals, callback)
{
  var cnt = 0;
  for(i = 0; i < deals.restaurants.length; ++i)
  {
    distanceApi.getDistance(location, deals.restaurants[i].location, 'driving', function(index, response){
      deals.restaurants[index].distance = response.rows[0].elements[0].distance.value;
      deals.restaurants[index].address = response.destination_addresses[0];
      ++cnt;
      if(cnt >= deals.restaurants.length - 1)
        callback(deals);
    }.bind(this, i));
  }
}

function parseMinPrice(str)
{
  var res = str.match(/\$\d+\.?(\d+)?/i);
  if(res != null)
    var ret = {text: res[0], value: parseFloat(res[0].substring(1)) };
  else {
    var ret = {text: 'No Price Found', value: -1};
  }
  return ret;
}

function sortByPrice(deals)
{
  deals.restaurants.sort(function(a,b) {
    return (a.price.value <= b.price.value ? -1 : 1);
  });
}

exports.getDir = function(latitude, longitude, address, callback)
{
  var location = `${latitude},${longitude}`;
  directionsApi.getDirection(location, address, 'driving', callback);
}
