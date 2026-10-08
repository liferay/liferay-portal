/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const ITEM_TYPES_SYMBOL = {
	KBArticle: 'document-text',
	KBFolder: 'folder',
};

const SEARCH_DELTA = 2;

const highlithKeywordInText = (text, keyword) => {
	const parts = text.split(new RegExp(`(${keyword})`, 'gi'));

	return (
		<span>
			{parts.map((part, index) =>
				part.toLowerCase() === keyword.toLowerCase() ? (
					<b key={index}>{part}</b>
				) : (
					part
				)
			)}
		</span>
	);
};

const SearchResult = ({filteredItems, handleOnclickItem, keyword}) => {
	const [selectedResultId, setSelectedResultId] = useState();

	return filteredItems.length ? (
		<ClayList role="list">
			{filteredItems.map((item) => {
				return (
					<ClayList.ItemField
						className={classnames({
							'knowledge-base-navigation-item-active':
								item.id === selectedResultId,
						})}
						expand
						key={item.id}
					>
						<ClayLink
							className="p-1"
							decoration="none"
							displayType="secondary"
							href={handleOnclickItem ? '#' : item.href}
							onClick={() => {
								setSelectedResultId(item.id);
								if (handleOnclickItem) {
									handleOnclickItem(item);
								}
							}}
						>
							<ClayIcon
								className="mr-2"
								symbol={ITEM_TYPES_SYMBOL[item.type]}
							/>

							{highlithKeywordInText(item.name, keyword)}
						</ClayLink>
					</ClayList.ItemField>
				);
			})}
		</ClayList>
	) : (
		<ClayEmptyState
			description={sub(
				Liferay.Language.get(
					'no-content-was-found-that-matched-keyword-x'
				),
				[keyword]
			)}
			imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
			small
			title={Liferay.Language.get('no-results-found')}
		/>
	);
};

export default function SearchField({
	handleOnclickItem,
	handleSearchChange,
	items,
}) {
	const initialSearchInfo = {
		filteredItems: [],
		query: '',
	};

	const [searchInfo, setSearchInfo] = useState(initialSearchInfo);

	const [searchActive, setSearchActive] = useState(false);

	useEffect(() => {
		handleSearchChange({isSearchActive: searchActive});
	}, [handleSearchChange, searchActive]);

	const handleQueryChange = (event) => {
		const newQuery = event.target.value;
		let results = [];

		if (newQuery.length > SEARCH_DELTA) {
			results = items.filter((item) =>
				item.name.toLowerCase().includes(newQuery.toLowerCase())
			);
		}

		setSearchActive(newQuery.length > SEARCH_DELTA);
		setSearchInfo({
			filteredItems: results,
			query: newQuery,
		});
	};

	const handleSearchClick = () => {
		if (searchInfo.query) {
			setSearchInfo(initialSearchInfo);
			setSearchActive(false);
		}
	};

	const iconTitle = searchInfo.query
		? Liferay.Language.get('clear')
		: Liferay.Language.get('search');

	return (
		<>
			<ClayInput.Group small>
				<ClayInput.GroupItem>
					<ClayInput
						aria-label={Liferay.Language.get('search-for')}
						className="form-control input-group-inset input-group-inset-after"
						onChange={handleQueryChange}
						placeholder={Liferay.Language.get('search-for')}
						type="text"
						value={searchInfo.query}
					/>

					<ClayInput.GroupInsetItem after tag="span">
						<ClayButtonWithIcon
							aria-label={iconTitle}
							displayType="unstyled"
							onClick={handleSearchClick}
							small
							symbol={searchInfo.query ? 'times' : 'search'}
							title={iconTitle}
						/>
					</ClayInput.GroupInsetItem>
				</ClayInput.GroupItem>
			</ClayInput.Group>

			<hr className="separator" />

			{searchActive && (
				<SearchResult
					filteredItems={searchInfo.filteredItems}
					handleOnclickItem={handleOnclickItem}
					keyword={searchInfo.query}
				/>
			)}
		</>
	);
}
