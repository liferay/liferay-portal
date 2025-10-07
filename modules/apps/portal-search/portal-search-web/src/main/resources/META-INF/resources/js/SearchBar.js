/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FacetUtil} from './FacetUtil';

export default function ({formId, initialKeywords, retainFacetSelections}) {
	const form = document.getElementById(formId);

	if (!form) {
		return;
	}

	const emptySearchInput = form.querySelector(
		'.search-bar-empty-search-input'
	);

	const emptySearchEnabled =
		emptySearchInput && emptySearchInput.value === 'true';

	const keywordsInput = form.querySelector('.search-bar-keywords-input');

	const keywordsInputSearchButton = form.querySelector(
		'.search-bar-submit-button'
	);

	const resetStartPage = form.querySelector('.search-bar-reset-start-page');

	const scopeSelect = form.querySelector('.search-bar-scope-select');

	function enableKeywordsInput() {
		if (keywordsInput) {
			keywordsInput.disabled = false;
			keywordsInput.classList.remove('disabled');
		}

		if (keywordsInputSearchButton) {
			keywordsInputSearchButton.disabled = false;
		}

		if (scopeSelect) {
			scopeSelect.disabled = false;
			scopeSelect.classList.remove('disabled');
		}
	}

	function getKeywords() {
		if (!keywordsInput) {
			return '';
		}

		const keywords = keywordsInput.value;

		return keywords.trim();
	}

	function isSubmitEnabled() {
		return getKeywords() !== '' || emptySearchEnabled;
	}

	function updateQueryString(queryString) {
		const searchParams = new URLSearchParams(queryString);

		if (keywordsInput) {
			searchParams.set(keywordsInput.name, getKeywords());
		}

		if (resetStartPage) {
			searchParams.delete(resetStartPage.name);
		}

		if (scopeSelect) {
			searchParams.set(scopeSelect.name, scopeSelect.value);
		}

		searchParams.delete('p_p_id');
		searchParams.delete('p_p_state');
		searchParams.delete('start');

		return '?' + searchParams.toString();
	}

	function search() {
		if (isSubmitEnabled()) {
			const keywords = getKeywords();
			const searchURL = form.action;

			let queryString = updateQueryString(window.location.search);

			/*
			 * Refer to LPD-19994 for acceptance criteria regarding
			 * retaining facet selections across searches. Default behavior
			 * is to clear all facet selections after searching a new
			 * keyword.
			 */

			if (
				(initialKeywords !== keywords || keywords === '') &&
				!retainFacetSelections
			) {
				queryString = FacetUtil.removeAllFacetParameters(queryString);
			}

			Liferay.Util.navigate(searchURL + queryString);
		}
	}

	function onSubmit(event) {
		event.preventDefault();
		event.stopPropagation();

		search();
	}

	form.addEventListener('submit', onSubmit);

	enableKeywordsInput();

	return {
		dispose() {
			form.removeEventListener('submit', onSubmit);
		},
	};
}
