/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const dsrPDFDisplay = fragmentElement.querySelector('#dsr-pdf-display');
const dsrPDFFile = fragmentElement.querySelector('#dsr-pdf-file');
const dsrPDFLoader = fragmentElement.querySelector('#dsr-pdf-loader');
const dsrPDFTemplate = fragmentElement.querySelector('#dsr-pdf-template');

if (dsrPDFFile) {
	const href = dsrPDFFile.getAttribute('href');

	if (href && href.toLowerCase().includes('.pdf')) {
		const pdfURL = new URL(href, window.location.origin);

		if (pdfURL.protocol === 'http:' || pdfURL.protocol === 'https:') {
			dsrPDFDisplay.innerHTML = '';
			dsrPDFLoader.classList.remove('d-none');
			const clone = dsrPDFTemplate.content.cloneNode(true);
			const embed = clone.querySelector('embed');

			embed.setAttribute(
				'src',
				href.replace(/download=true/gi, '') +
					'#toolbar=1&navpanes=0&scrollbar=1'
			);

			dsrPDFDisplay.appendChild(clone);
		}
	}
	else {
		dsrPDFLoader.classList.add('d-none');
	}
}
