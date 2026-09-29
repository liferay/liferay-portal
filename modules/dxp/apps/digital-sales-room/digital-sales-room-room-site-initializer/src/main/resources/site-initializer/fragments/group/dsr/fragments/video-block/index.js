/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const videoContainer = fragmentElement.querySelector('.video-container');

const rawProvider = {
	getParameters(url) {
		return {url};
	},

	showVideo(parameters) {
		const video = document.createElement('video');
		const source = document.createElement('source');

		source.src = parameters.url;

		video.autoplay = configuration.autoPlay;
		video.playsInline = true;
		video.style.height = '100%';
		video.style.width = '100%';

		video.appendChild(source);
		videoContainer.appendChild(video);
	},
};

const youtubeProvider = {
	getParameters(url) {
		const start = url.searchParams.get('start');
		const regex =
			/(youtu.*be.*)\/(watch\?v=|embed\/|v|shorts|)(.*?((?=[&#?])|$))/gm;
		const match = regex.exec(url);
		const videoId = match ? match[3] : null;

		if (videoId) {
			return {
				start,
				videoId,
			};
		}
	},

	showVideo(parameters) {
		const handleAPIReady = function () {
			new YT.Player(videoContainer, {
				height: configuration.videoHeight.replace('px', ''),
				playerVars: {
					autoplay: configuration.autoPlay,
					controls: configuration.hideControls ? 0 : 1,
					loop: configuration.loop ? 1 : 0,
					playlist: configuration.loop
						? parameters.videoId
						: undefined,
					start: !parameters.start ? 0 : parameters.start,
				},
				videoId: parameters.videoId,
				width: configuration.videoWidth.replace('px', '') || '100%',
			});
		};

		if ('YT' in window) {
			handleAPIReady();
		}
		else {
			const oldCallback =
				window.onYouTubeIframeAPIReady || function () {};

			window.onYouTubeIframeAPIReady = function () {
				oldCallback();
				handleAPIReady();
			};

			const apiSrc = '//www.youtube.com/iframe_api';

			let script = Array.from(document.querySelectorAll('script')).find(
				(script) => {
					return script.src === apiSrc;
				}
			);

			if (!script) {
				script = document.createElement('script');
				script.src = apiSrc;
				document.body.appendChild(script);
			}
		}
	},
};

function main() {
	let matched = false;
	const url = new URL(configuration.url, window.location.origin);
	const providers = [youtubeProvider, rawProvider];

	for (let i = 0; i < providers.length && !matched; i++) {
		const provider = providers[i];
		const parameters = provider.getParameters(url);

		if (parameters) {
			provider.showVideo(parameters);
			matched = true;
		}
	}
}

if (videoContainer) {
	main();
}
