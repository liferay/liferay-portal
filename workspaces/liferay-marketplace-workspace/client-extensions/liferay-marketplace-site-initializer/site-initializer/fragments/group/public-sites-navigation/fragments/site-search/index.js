/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const searchSuggestionsInput = fragmentElement.querySelector(
	'.search-suggestions-input'
);
const suggestions = fragmentElement.querySelector('.suggestions');
const searchSuggestions = fragmentElement.querySelector('.search-suggestions');

const searchSuggestionItemTemplate = suggestions.querySelector('template');

const seeAllResultsLink = fragmentElement.querySelector(
	'.search-suggestions-see-all-results-text'
);
const searchSuggestionItem =
	searchSuggestionItemTemplate.content.querySelector('a');

const getSiteName = () => {
	const {pathname} = new URL(Liferay.ThemeDisplay.getCanonicalURL());
	const pathSplit = pathname.split('/').filter(Boolean);

	return `/${pathSplit[0]}/${pathSplit[1]}`;
};

const redirectTo = (url = '', currentSiteName = getSiteName()) => {
	const pagePreviewEnabled = false;

	const queryParams = pagePreviewEnabled ? '?p_l_mode=preview' : '';
	window.location.href = `${Liferay.ThemeDisplay.getPathContext()}${currentSiteName}/${url}${queryParams}`;
};

const searchIcon = document.querySelector('#searchSubmitBtn');
const closeSearchButton = document.querySelector('#closeSearch');

searchIcon.onclick = () => redirectTo('search');
closeSearchButton.onclick = () => {
	const dropdownContainer = document.querySelector(
		'.dropdown-wide-container .dropdown-menu'
	);

	dropdownContainer.classList.remove('show');
};

const updateSearch = () => {
	searchSuggestions.innerHTML = '';

	const searchSuggestionsInputValue = searchSuggestionsInput.value;

	if (searchSuggestionsInputValue) {
		seeAllResultsLink.href = '/search?q=' + searchSuggestionsInputValue;
		suggestions.classList.add('performing-search');

		return performSearch(searchSuggestionsInputValue);
	}

	return suggestions.classList.remove(
		'loading-search',
		'performing-search',
		'search-error',
		'search-results-found'
	);
};

let debounceTimer;

const debounce = (callback, time) => {
	window.clearTimeout(debounceTimer);
	debounceTimer = window.setTimeout(callback, time);
};

searchSuggestionsInput.addEventListener(
	'input',
	() => {
		suggestions.classList.add('loading-search');
		debounce(updateSearch, 250);
	},
	false
);

const performSearch = (query) => {
	const postDataURL = `/o/portal-search-rest/v1.0/suggestions?currentURL=${
		window.location.href
	}&destinationFriendlyURL=/search&groupId=${Liferay.ThemeDisplay.getScopeGroupId()}&plid=${Liferay.ThemeDisplay.getPlid()}&scope=this-site&search=${query}`;

	postData(postDataURL, [
		{
			attributes: {
				includeAssetSearchSummary: true,
				includeAssetURL: true,
				sxpBlueprintId: configuration.searchBlueprintId,
			},
			contributorName: 'sxpBlueprint',
			displayGroupName: 'Public Nav Search Recommendations',
			size: '3',
		},
	])
		.then((data) => {
			if (data && data.items && data.items[0]) {
				const items = JSON.parse(JSON.stringify(data.items[0]));

				if (items) {
					searchSuggestions.innerHTML = '';

					const searchTermRegExp = new RegExp(
						'(' + query + ')',
						'gi'
					);

					for (const suggestion of items.suggestions) {
						const suggestionLink = document.importNode(
							searchSuggestionItem,
							true
						);

						const assetURL = suggestion.attributes.assetURL.replace(
							/\?.*$/,
							''
						);

						suggestionLink.href = assetURL;

						const suggestionTitle = suggestionLink.querySelector(
							'.search-suggestion-item-title'
						);

						suggestionTitle.appendChild(
							document.createTextNode(suggestion.text)
						);

						const suggestionContent = suggestionLink.querySelector(
							'.search-suggestion-item-content'
						);

						let suggestionContentTextValue =
							suggestion.attributes.assetSearchSummary;

						if (suggestionContentTextValue) {
							suggestionContentTextValue =
								suggestionContentTextValue.substring(0, 500);

							suggestionContent.innerHTML =
								suggestionContentTextValue.replace(
									searchTermRegExp,
									`<b>$1</b>`
								);
						}

						const suggestionURL = suggestionLink.querySelector(
							'.search-suggestion-item-link'
						);

						suggestionURL.appendChild(
							document.createTextNode(
								getBreadcrumbFromURL(assetURL)
							)
						);

						searchSuggestions.appendChild(suggestionLink);

						suggestions.classList.add('search-results-found');
						suggestions.classList.remove('loading-search');
					}
				}
			}
			else {
				suggestions.classList.remove('search-results-found');
				suggestions.classList.remove('loading-search');
			}

			suggestions.classList.remove('search-error');
		})
		.catch(() => {
			suggestions.classList.remove('loading-search');
			suggestions.classList.add('search-error');
		});
};

const postData = async (url = '', data = {}) => {
	const response = await Liferay.Util.fetch(url, {
		body: JSON.stringify(data),
		credentials: 'include',
		headers: {
			'Accept': 'application/json',
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Content-Type': 'application/json',
			'x-csrf-token': Liferay.authToken,
		},
		method: 'POST',
	});

	return response.json();
};

const getBreadcrumbFromURL = (url) => {
	if (!url) {
		return '';
	}

	url = url
		.replaceAll('/web/guest/w/', 'home/')
		.replaceAll('/web/guest/', 'home/')
		.replaceAll('/', ' / ')
		.replaceAll('-', ' ');

	const ancronymList = ['api', 'ccr', 'dxp', 'mvc', ' ui ', 'url'];

	ancronymList.forEach((word) => {
		if (url.includes(word)) {
			const regEx = new RegExp(word, 'ig');
			url = url.replace(regEx, word.toUpperCase());
		}
	});

	let breadcrumbs = [];
	const excludeWords = ['a', 'and', 'of', 'the', 'to', 'via'];
	breadcrumbs = url.split(' ');

	return breadcrumbs
		.map((word, i) => {
			return excludeWords.includes(word) && i !== 0
				? [word]
				: word.charAt(0).toUpperCase() + word.slice(1);
		})
		.join(' ');
};
