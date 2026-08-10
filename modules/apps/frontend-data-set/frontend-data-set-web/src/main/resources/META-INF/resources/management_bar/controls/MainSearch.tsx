/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import {cancelDebounce, debounce} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {SEARCH_AS_YOU_TYPE_DEBOUNCE_DELAY} from '../../constants';

function MainSearch({onClear}: {onClear: () => void}) {
	const {apiURL, appURL, onSearch, searchAsYouType, searchParam} = useContext(
		FrontendDataSetContext
	);

	const [inputValue, setInputValue] = useState(searchParam || '');

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

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem>
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
					onKeyDown={(event) => {
						if (event.key !== 'Enter') {
							return;
						}

						event.preventDefault();

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

							doSearch(inputValue);
						}}
						symbol="search"
						type="submit"
					/>
				</ClayInput.GroupInsetItem>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export default MainSearch;
