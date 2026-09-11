/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import {cancelDebounce, debounce} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {SEARCH_AS_YOU_TYPE_DEBOUNCE_DELAY} from '../../constants';
import SearchSuggestionsMenu from './SearchSuggestionsMenu';

function MainSearch({onClear}: {onClear: () => void}) {
	const {
		apiURL,
		appURL,
		onSearch,
		searchAsYouType,
		searchParam,
		searchSuggestionsEnabled,
	} = useContext(FrontendDataSetContext);

	const [inputValue, setInputValue] = useState(searchParam || '');
	const [searchSuggestionsActive, setSearchSuggestionsActive] =
		useState(false);
	const [searchSuggestionsOpenCount, setSearchSuggestionsOpenCount] =
		useState(0);

	const inputGroupItemRef = useRef<HTMLDivElement>(null);

	const debouncedSearch = useMemo(
		() =>
			debounce(
				(query: string) => onSearch({query}),
				SEARCH_AS_YOU_TYPE_DEBOUNCE_DELAY
			),
		[onSearch]
	);

	useEffect(() => {
		setInputValue(searchParam || '');
	}, [searchParam]);

	useEffect(() => () => cancelDebounce(debouncedSearch), [debouncedSearch]);

	const uncontrolledItems = Boolean(apiURL || appURL);

	const doSearch = (query: string) => {
		if (searchAsYouType) {
			return;
		}

		onSearch({query});
	};

	// Clicking counts as well as focusing, because an input that already holds
	// the focus fires no focus event, and it does hold it after a search or
	// after Escape closed the dropdown

	const openSearchSuggestions = () => {
		if (!searchSuggestionsEnabled) {
			return;
		}

		// The menu reads the history as it mounts, so counting the requests to
		// open it buys each one a mount of its own and a reading of its own. The
		// box is clickable while the menu is already open, and an item visited in
		// between has to show up when it is.

		setSearchSuggestionsOpenCount((count) => count + 1);

		setSearchSuggestionsActive(true);
	};

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem ref={inputGroupItemRef}>
				<ClayInput
					aria-label={Liferay.Language.get('search')}
					className="input-group-inset input-group-inset-after"
					onChange={(event) => {
						const query = event.target.value;

						setInputValue(query);

						if (!query) {
							onClear();
						}

						if (!searchAsYouType) {
							return;
						}

						if (uncontrolledItems) {
							if (query) {
								debouncedSearch(query);
							}
							else {
								cancelDebounce(debouncedSearch);
							}
						}
						else {
							onSearch({query});
						}
					}}
					onClick={openSearchSuggestions}
					onFocus={openSearchSuggestions}
					onKeyDown={(event) => {
						if (event.key !== 'Enter') {
							return;
						}

						event.preventDefault();

						setSearchSuggestionsActive(false);

						doSearch(inputValue);
					}}
					placeholder={Liferay.Language.get('search')}
					type="search"
					value={inputValue}
				/>

				<ClayInput.GroupInsetItem after tag="div">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('search')}
						displayType="unstyled"
						monospaced={false}
						onClick={(event) => {
							event.preventDefault();

							setSearchSuggestionsActive(false);

							doSearch(inputValue);
						}}
						symbol="search"
						type="submit"
					/>
				</ClayInput.GroupInsetItem>

				{searchSuggestionsActive && (
					<SearchSuggestionsMenu
						alignElementRef={inputGroupItemRef}
						key={searchSuggestionsOpenCount}
						onActiveChange={setSearchSuggestionsActive}
						onQueryClick={(query) => {
							setSearchSuggestionsActive(false);

							setInputValue(query);

							doSearch(query);
						}}
						onVisitedItemClick={() =>
							setSearchSuggestionsActive(false)
						}
						value={inputValue}
					/>
				)}
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export default MainSearch;
