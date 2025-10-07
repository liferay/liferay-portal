/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function embedded({url}) {
	const iframe = document.getElementById('embeddedIframe');

	iframe.addEventListener('load', () => {
		try {
			const iframeDocument =
				iframe.contentDocument || iframe.contentWindow.document;
			const iframeBody = iframeDocument.body;

			iframe.style.height = iframeBody.scrollHeight + 'px';

			const resizeObserver = new ResizeObserver(() => {
				const scrollY = window.scrollY;

				iframe.style.height = iframeBody.scrollHeight + 'px';
				window.scrollTo({top: scrollY});
			});

			resizeObserver.observe(iframeBody);
		}
		catch (error) {
			if (process.env.NODE_ENV === 'development') {
				console.error(error);

				iframe.style.height = '600px';
			}
		}
	});

	iframe.src = url;
}
