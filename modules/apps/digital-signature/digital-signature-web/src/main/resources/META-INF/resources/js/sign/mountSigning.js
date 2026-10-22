/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch, getOpener} from 'frontend-js-web';

export default function mountSigning({
	backURL,
	completeDSRecipientViewURL,
	containerId,
	integrationKey,
	jsURL,
	recipientViewURL,
}) {
	const onPageHide = () => {
		if (navigator.sendBeacon) {
			navigator.sendBeacon(completeDSRecipientViewURL);
		}
	};

	const onBeforeNavigate = () => {
		Liferay.detach('beforeNavigate', onBeforeNavigate);

		onPageHide();
	};

	const script = document.createElement('script');

	script.onload = () => {
		window.DocuSign.loadDocuSign(integrationKey).then((docuSign) => {
			const signing = docuSign.signing({
				displayFormat: 'default',
				url: recipientViewURL,
			});

			signing.on('sessionEnd', (event) => {
				window.removeEventListener('pagehide', onPageHide);
				Liferay.detach('beforeNavigate', onBeforeNavigate);

				const hasOutcome = [
					'decline',
					'signing_complete',
					'viewing_complete',
				].includes(event?.sessionEndType);

				const opener = getOpener();

				if (opener && opener !== window) {
					if (!hasOutcome) {
						opener.Liferay.fire('closeModal', {redirect: backURL});

						return;
					}

					fetch(completeDSRecipientViewURL, {method: 'POST'}).then(
						() => {
							opener.Liferay.fire('closeModal', {
								redirect: backURL,
							});
						}
					);

					return;
				}

				window.location.href = hasOutcome
					? completeDSRecipientViewURL
					: backURL;
			});

			signing.mount(`#${containerId}`);

			window.addEventListener('pagehide', onPageHide);
			Liferay.on('beforeNavigate', onBeforeNavigate);
		});
	};

	script.src = jsURL;

	document.head.appendChild(script);
}
