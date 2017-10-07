var fs = require('fs');
var http = require('http');
var querystring = require('querystring');

var apiKey = null;
fs.readFile('./.apiKeys/sqoot', 'utf8', function(err, data) {
  if(err) {
    return console.log(err);
  }
	apiKey = data.substring(0,data.length - 1);
  //console.log(apiKey);
});

var per_page = 1;

exports.sqoot = function(loca, rad, pagenum, callback)
{
	var path = '/v2/deals?api_key=' + apiKey + '&category_slugs=restaurants&online=false&location=' + loca + '&radius=' + rad + '&page=' + pagenum + '&per_page=' + per_page + '&order=distance';
  console.log(path);
  options = {
				hostname:'api.sqoot.com',
				method: 'GET',
				path: path,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
			}
	var req = http.request(options,function(res){
    var chunks = "";
    res.on('data', function(chunk){
      chunks = chunks + chunk;
    });
    res.on('end', function(){
      callback(JSON.parse(chunks));
    });
    console.log(res);
	});

  req.end();
}
