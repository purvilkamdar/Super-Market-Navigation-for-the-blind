var express = require('express');
var app = express();
var path = require('path');
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var bodyParser = require('body-parser');
var port = process.env.PORT || 3000;

server.listen(port, function () {
  console.log('Server listening at port %d', port);
});


app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())


io.on('connection',function(socket){
  console.log("Connected to frontend");
});
// Routing
app.use(express.static(path.join(__dirname, 'public')));
app.post('/co-ordinates',function(req,res){

  var co_ord={};

    var request_body=JSON.parse(JSON.stringify(req.body));
    console.log("Reqeust body="+request_body)
    var lat=request_body.lat;
    var lon=request_body.lon;
    console.log("lat="+lat);
    console.log("lon="+lon);
    co_ord['lat']=lat;
    co_ord['lon']=lon;
    io.sockets.emit("coords",co_ord);
    console.log("Payload sent="+JSON.stringify(co_ord,null,4));
    console.log("Request Received");
    res.sendStatus(200);
});

app.post('/compass',function(req,res){

    var co_ord={};

    var request_body=JSON.parse(JSON.stringify(req.body));
    console.log("Reqeust body="+request_body)
    var compass=request_body.compass;
    console.log("compass="+compass);
    co_ord['compass']=compass;
    io.sockets.emit("comp",compass);
    console.log("Payload sent="+JSON.stringify(co_ord,null,4));
    res.sendStatus(200);
});

app.post('/sensor',function(req,res){

    var co_ord={};

    var request_body=JSON.parse(JSON.stringify(req.body));
    console.log("Reqeust body="+request_body)
    var left=request_body.left;
    var center=request_body.center;
    var right=request_body.right;
    console.log("left="+left);
    console.log("center="+center);
    console.log("right="+right);
    co_ord['left']=left;
    co_ord['center']=center;
    co_ord['right']=right;
    io.sockets.emit("sensor",co_ord);
    console.log("Payload sent="+JSON.stringify(co_ord,null,4));
    console.log("Request Received");
    res.sendStatus(200);
});