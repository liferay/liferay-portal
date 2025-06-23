/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const baseDelay = 1000;
const maxRetries = 15;
let retryCount = 0;

async function liferayStreamHubConnect() {
	try {
		const oAuthApplication = Liferay.OAuth2Client.FromUserAgentApplication(
			'liferay-streamhub-etc-spring-boot-oauth-application-user-agent'
		);

		const [protocol, hostname] = oAuthApplication.homePageURL.split('//');

		const token = Liferay.ThemeDisplay.isSignedIn()
			? await oAuthApplication._getOrRequestToken()
			: 'GUEST';

		const socket = new WebSocket(
			`ws://${hostname}/server`,
			token.access_token
		);

		socket.onopen = () => {
			console.log('Connected to Liferay Stream Hub');
			retryCount = 0;
		};

		socket.onmessage = (event) => {
			const message = JSON.parse(event.data);

			if (message.type === 'Event') {
				const customEvent = new CustomEvent(message.name, {
					detail: JSON.parse(message.data),
				});
				window.dispatchEvent(customEvent);
			}
		};

		socket.onerror = (error) => {
			console.error('Socket error:', error);
		};

		socket.onclose = (event) => {
			console.warn(
				'Disconnected from server, trying to reconnect...',
				event.reason
			);
			retryWithBackoff();
		};
	}
	catch (error) {
		console.error('Unable to connect to Liferay Stream Hub Server', error);
		retryWithBackoff();
	}
}

function retryWithBackoff() {
	if (retryCount < maxRetries) {
		const delay = Math.min(baseDelay * 2 ** retryCount, 30000); // cap at 30s
		retryCount++;

		console.log(
			`⏳ Retrying in ${delay / 1000}s... (attempt ${retryCount})`
		);

		setTimeout(() => {
			liferayStreamHubConnect();
		}, delay);
	}
	else {
		console.error('Max retries reached. Could not reconnect.');
	}
}

liferayStreamHubConnect();
