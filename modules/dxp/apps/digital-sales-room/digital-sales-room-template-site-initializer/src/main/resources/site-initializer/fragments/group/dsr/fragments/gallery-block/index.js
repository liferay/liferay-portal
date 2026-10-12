/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

if (
	configuration.numberOfItems > 1 &&
	!fragmentElement.dataset.dsrGalleryInitialized &&
	layoutMode !== 'edit'
) {
	fragmentElement.dataset.dsrGalleryInitialized = true;

	let dsrGalleryTimeout = null;

	const slide = (index) => {
		clearTimeout(dsrGalleryTimeout);

		const currentElement = fragmentElement.querySelector(
			`.dsr-gallery-entry-${index}`
		);

		currentElement.classList.remove('active');
		fragmentElement
			.querySelector(`.dsr-gallery-navigation-entry-${index}`)
			.classList.remove('current');

		index = index + 1;

		if (index > configuration.numberOfItems) {
			index = 1;
		}

		fragmentElement
			.querySelector(`.dsr-gallery-entry-${index}`)
			.classList.add('active', 'current');
		fragmentElement
			.querySelector(`.dsr-gallery-navigation-entry-${index}`)
			.classList.add('current');

		setTimeout(() => {
			currentElement.classList.remove('current');
		}, 1500);

		dsrGalleryTimeout = setTimeout(() => {
			slide(index);
		}, 5000);
	};

	dsrGalleryTimeout = setTimeout(() => {
		slide(1);
	}, 5000);
}
