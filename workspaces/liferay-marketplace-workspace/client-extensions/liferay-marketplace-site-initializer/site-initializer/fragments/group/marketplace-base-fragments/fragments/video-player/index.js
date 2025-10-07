/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* global Plyr */

const autoplay = configuration.autoplay;
const lazy = configuration.lazy;
const loop = configuration.loop;
const muted = configuration.muted;
const URLjs = configuration.URLjs;
const URLcss = configuration.URLcss;

function loadPlyr() {
	if (typeof Plyr !== 'undefined') {
		return initPlayer();
	}

	const link = document.createElement('link');

	link.rel = 'stylesheet';
	link.href = URLcss;

	document.head.appendChild(link);

	const script = document.createElement('script');

	script.src = URLjs;
	script.onload = initPlayer;

	document.body.appendChild(script);
}

function extractYouTubeId(url) {
	const match = url.match(/(?:v=|\/)([0-9A-Za-z_-]{11})/);

	return match ? match[1] : null;
}

function initPlayer() {
	const plyrPlaceholder = document.getElementById('plyr-placeholder');
	if (!plyrPlaceholder) {
		return;
	}

	plyrPlaceholder.innerHTML = '';

	const plyrWrapper = document.createElement('div');
	const plyrPlayer = document.createElement('div');

	plyrPlayer.id = 'plyr-player';
	plyrWrapper.classList.add('plyr__video-embed');
	plyrWrapper.appendChild(plyrPlayer);

	const rawVideoUrl = configuration.videoURL?.trim();
	const videoId = extractYouTubeId(rawVideoUrl);

	if (!videoId) {
		console.error(rawVideoUrl);

		return;
	}

	plyrPlayer.setAttribute('data-plyr-provider', 'youtube');
	plyrPlayer.setAttribute('data-plyr-embed-id', videoId);

	plyrPlaceholder.appendChild(plyrWrapper);

	new Plyr('#plyr-player', {
		autoplay,
		displayDuration: false,
		loop: {active: loop},
		muted,
		youtube: {
			modestbranding: 1,
			noCookie: true,
			rel: 0,
		},
	});
}

if (!lazy) {
	loadPlyr();
}

Liferay.on('plyr:play', ({details}) => {
	const [props = {}] = details;

	if (props.videoURL) {
		configuration.videoURL = props.videoURL;

		loadPlyr();
	}
});
