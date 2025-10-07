/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

document.addEventListener('DOMContentLoaded', () => {
	const searchBarKeywordsInput = document.querySelector(
		'.search-bar-keywords-input'
	);
	const searchButton = document.querySelector('.search-button');

	function redirectToSearchResults() {
		window.location.href =
			'/search?q=' +
			encodeURIComponent(searchBarKeywordsInput.value.trim()) +
			'&' +
			configuration.customParameter.trim();
	}

	searchBarKeywordsInput.addEventListener('keydown', (event) => {
		if (event.key === 'Enter') {
			event.preventDefault();

			redirectToSearchResults();
		}
	});

	searchButton.addEventListener('click', (event) => {
		event.preventDefault();

		redirectToSearchResults();
	});
});
