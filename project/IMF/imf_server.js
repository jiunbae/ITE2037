var net = require('net');
var PORT = 8282;

var connections = {};

var soloQueue = [];
var partner = {};


function sendTo(data, conn) {
	conn.write( 1 + JSON.stringify(data) + "\r\n" );
}
function makePartner(sess1, sess2) {
	partner[sess1] = sess2;
	partner[sess2] = sess1;
}
function getSessID(conn) {
	for (var key in connections) {
		if (connections[key] == conn) return key;
	}
	return null;
}
function getPartnerConn(conn) {
	var ss = getSessID(conn);
	if (ss == null) return null;
	else {
		var p = partner[ss];
		if (p == null) return null;
		else
			return connections[p];
	}
}

function printListConnected() {
	var sessions = [];

	for (var sess in connections) {
		sessions.push(sess);
	}

	console.log("users: [" + sessions.join(", ") + "]");
}


var server = net.createServer(function(conn) {
	//connections.push(conn);

	conn.on('data', function (rawData) {
		var rawStr = rawData.toString();
		var rawStrArr = rawStr.split("$\n");
		var str;
		var data;

		//console.log( rawStrArr );

		for (var i=0; i<rawStrArr.length; i++) {
			str = rawStrArr[i];

			if (str == '' || str.indexOf('{') == -1) continue;

			str = str.substr( str.indexOf('{') );

			try {
				data = JSON.parse( str );

			}catch(e) {
				console.log("Error occurs on parsing data: " + str);
				console.log(e);
				return;
			}

			switch (data.type) {
				case 'connect' :
					connections[ data.session_id ] = conn;

					console.log('  connected: ' + data.session_id);
					printListConnected();


					sendTo( {'type':'connected'}, conn );
					soloQueue.push( data.session_id );
					break;

				case 'send_partner':
				case 'send_partner_info':
					if ( !getPartnerConn(conn) ) return;

					var message = {
						'type': 'partner' + (data.type == 'send_partner_info' ? '_info' : '') + '_sent',
						'data': data.data
					};

					sendTo( message, getPartnerConn(conn) );
					break;
			}

		}

	});

	conn.on('close', function () {
		console.log('  disconnected: ' + getSessID(conn));

		var sessID = getSessID(conn);
		var partnerConn = getPartnerConn(conn);

		if (partnerConn != null) {
			sendTo({
				'type': 'partner_disconnected'
			}, partnerConn);

			soloQueue.push( getSessID(partnerConn) );

			partner[ partner[ sessID ] ] = null;
			partner[ sessID ] = null;
		}

		var sqid = soloQueue.indexOf(sessID);
		if (sqid != -1)
			soloQueue.splice(sqid, 1);

		delete connections[ sessID ];

		printListConnected();
	});

	conn.on('error', function (exception) {
		console.log(exception);
	});

}).listen(PORT);



setInterval(function() {
	if (soloQueue.length >= 2) {
		var p1 = soloQueue[0];
		var p2 = soloQueue[1];

		makePartner(p1, p2);

		sendTo({
			'type': 'partner_found',
			'partner_sess_id': p2
		}, connections[p1]);

		sendTo({
			'type': 'partner_found',
			'partner_sess_id': p1
		}, connections[p2]);

		soloQueue.splice(0, 2);
	}

}, 1000);


console.log('START LISTENING on PORT ' + PORT + '\n');

