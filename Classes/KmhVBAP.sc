KmhVBAP {

	classvar <setup, <room, <dimensions, <buffer, <spkrArray, <server,
	<>loadOnServerBoot, <>remapChannels = true;

	*initClass {

		var r108118 = [27.167560, 0.0, -27.167560, -63.823243, -107.595814, -152.448538, 152.551462, 107.404186, 63.817162];

		setup = IdentityDictionary.newFrom((

			kmhLillaSalen:  IdentityDictionary[
				2 -> [-34.689614, -13.383763,  10.440725,  32.117788,  55.741675,  78.207673,  101.49442,  124.85167,  147.91193,  169.17789, -167.82013, -145.63454, -123.784, -102.64182, -79.887731, -57.926139],

				3 -> [[ -34.689614 , 12.910417 ], [ -13.383763 , 12.910417 ], [ 10.440725 , 12.910417 ], [ 32.117788 , 12.910417 ],
	[ 55.741675 , 12.910417 ], [ 78.207673 , 12.910417 ], [ 101.49442 , 12.910417 ], [ 124.85167 , 12.910417 ],
	[ 147.91193 , 12.910417 ], [ 169.17789 , 12.910417 ], [ -167.82013 , 12.910417 ], [ -145.63454 , 12.910417 ],
	[ -123.784 , 12.910417 ], [ -102.64182 , 12.910417 ], [ -79.887731 , 12.910417 ], [ -57.926139 , 12.910417 ],
	[ -22.349553 , 34.696822 ], [ 22.843958 , 34.696822 ], [ 69.013292 , 34.696822 ], [ 115.56544 , 34.696822 ],
	[ 158.89992 , 34.696822 ], [ -158.89763 , 34.696822 ], [ -114.65354 , 34.696822 ], [ -68.170128 , 34.696822 ],
	[ -45 , 69.185799 ], [ 45 , 69.185799 ], [ 135 , 69.185799 ], [ -135 , 69.185799 ], [ 0 , 90 ]],
				\channelMap -> (0..28)
			],

			kmh114: IdentityDictionary[
				2 -> [24.6, -26.34, -58.7, -106, -129.35, 129.35, 106, 58.7],
				3 ->  [ [ 24.6, 0.0 ], [ -26.34, -0.0 ], [ -58.7, -0.0 ], [ -106, 0 ], [ -129.35, -0.0 ], [ 129.35, 0.0 ], [ 106, 0 ], [ 58.7, 0.0 ], [ 45, 18 ], [ -45, 18 ], [ -135, 18 ], [ 135, 18 ], [ 0, 90 ] ],
				//Array index = VBAP channel
				//Number = Output channel
				\channelMap -> [0, 1, 7, 5, 9, 8, 4, 6, 10, 11, 13, 12, 14]
			],

			kmh108: IdentityDictionary[
				2 -> r108118,
				3 -> r108118,
				//FIXME
				\channelMap -> [0, 9, 2, 8, 3, 7, 4, 6, 5]
			],


			kmh118: IdentityDictionary[
				2 -> r108118,
				3 -> r108118,
				//FIXME
				\channelMap -> [0, 9, 2, 8, 3, 7, 4, 6, 5]
			]

		));


		this.switchSetup(\kmhLillaSalen);
		this.server_(Server.default);
		loadOnServerBoot = true;

	}

	*server_ { |newServer|
		if (server.notNil) {
			ServerBoot.remove(this, server);
		};
		ServerBoot.add(this, server);

	}

	*switchSetup { |newRoom, dim=3, remapChannels=true|
		var speakers;
		if (setup.keys.includes(newRoom).not) {
			Error("No such setup").throw;
		};
		if (#[2, 3].includes(dim).not) {
			Error("Wrong kind of dimension argument").throw;
		};
		room = newRoom;
		dimensions = dim;
		speakers = setup[room][dim];
		spkrArray = VBAPSpeakerArray(dim, speakers);
		if (server.notNil and: { server.serverRunning }) {
			this.loadBuffers;
		};
	}

	*loadBuffers {
		buffer.free;
		buffer = spkrArray.loadToBuffer(server);
	}

	*doOnServerBoot {
		if (loadOnServerBoot) {
			this.loadBuffers;
		}
	}

	*ar { |in, azimuth=0.0, elevation=1.0, spread=0.0|
		var snd = VBAP.ar(spkrArray.numSpeakers, in, buffer.bufnum, azimuth, elevation, spread);
		^if (remapChannels) {
			(setup[room][\channelMap].maxItem + 1).collect { |num|
				setup[room][\channelMap].indexOf(num) !? snd[_] ?? { DC.ar };
			}
		} {
			snd
		}
	}
}