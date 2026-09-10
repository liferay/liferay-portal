/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import React, {useContext, useEffect, useRef, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import recentSearches from '../../utils/recentSearches';

interface IMatch {
	index: number;
	length: number;
}

interface IProps {
	alignElementRef: React.RefObject<HTMLElement>;
	onActiveChange: (active: boolean) => void;
	onQueryClick: (query: string) => void;
	value: string;
}

function SearchSuggestionsMenu({
	alignElementRef,
	onActiveChange,
	onQueryClick,
	value,
}: IProps) {
	const {id} = useContext(FrontendDataSetContext);

	const menuRef = useRef<HTMLDivElement>(null);

	const [queries, setQueries] = useState(() => recentSearches.get(id));

	// The menu is as wide as the search box, so it watches the box rather than
	// the viewport: the box also reports no width once the management bar hides
	// it behind a button, which the viewport alone would not tell us.

	const [searchBoxWidth, setSearchBoxWidth] = useState<number | null>(null);

	useEffect(() => {
		const searchBox = alignElementRef.current;

		if (!searchBox || !window.ResizeObserver) {
			return;
		}

		const resizeObserver = new ResizeObserver(([entry]) =>
			setSearchBoxWidth(entry.contentRect.width)
		);

		resizeObserver.observe(searchBox);

		return () => resizeObserver.disconnect();
	}, [alignElementRef]);

	// A menu hanging from a box that is no longer displayed would sit against
	// the edge of the page, so it closes with the box

	useEffect(() => {
		if (searchBoxWidth === 0) {
			onActiveChange(false);
		}
	}, [onActiveChange, searchBoxWidth]);

	const matchedQueries = queries
		.map((query) => ({match: _getMatch(query, value), query}))
		.filter(({match}) => !!match) as Array<{match: IMatch; query: string}>;

	// Clay keeps the menu's markup in the page once it is mounted, so it is
	// rendered only while a query matches and leaves nothing for a screen
	// reader otherwise

	if (!matchedQueries.length) {
		return null;
	}

	return (
		<ClayDropDown.Menu
			active
			alignElementRef={alignElementRef}
			className="fds-search-suggestions"
			onActiveChange={onActiveChange}
			ref={menuRef}
			style={{
				maxWidth: 'none',
				width: `${alignElementRef.current?.clientWidth}px`,
			}}

			// An open menu hides the rest of the page from assistive
			// technology, which suits a menu the user opened on purpose but not
			// one that merely follows the focus. Naming the search box keeps it
			// and its search button reachable while the list is open.

			suppress={[menuRef, alignElementRef]}
			triggerRef={alignElementRef}
		>
			<ClayDropDown.Caption className="fds-search-suggestions-caption">
				<span className="text-secondary text-uppercase">
					{Liferay.Language.get('recent-searches')}
				</span>

				<ClayButton
					className="fds-search-suggestions-clear-all"
					displayType="link"
					onClick={() => setQueries(recentSearches.clear(id))}
					small
				>
					{Liferay.Language.get('clear-all')}
				</ClayButton>
			</ClayDropDown.Caption>

			<ClayDropDown.ItemList>
				{matchedQueries.map(({match, query}) => (
					<li
						className="fds-search-suggestions-item"
						key={query}
						role="presentation"
					>
						<button
							aria-label={query}
							className="dropdown-item"
							onClick={() => onQueryClick(query)}
							role="menuitem"
							type="button"
						>
							{match.length ? (
								<>
									{query.slice(0, match.index)}

									<strong>
										{query.slice(
											match.index,
											match.index + match.length
										)}
									</strong>

									{query.slice(match.index + match.length)}
								</>
							) : (
								query
							)}
						</button>

						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('clear-search')}
							className="fds-search-suggestions-item-remove"
							displayType="unstyled"
							onClick={() =>
								setQueries(recentSearches.remove(id, query))
							}
							role="menuitem"
							size="sm"
							symbol="times-small"
							title={Liferay.Language.get('clear-search')}
						/>
					</li>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown.Menu>
	);
}

/**
 * Returns the range of a stored query the input matches, or null when it does
 * not. An empty input matches every query with an empty range.
 */
function _getMatch(query: string, value: string): IMatch | null {
	const search = value.trim().toLowerCase();

	if (!search) {
		return {index: 0, length: 0};
	}

	const index = query.toLowerCase().indexOf(search);

	return index === -1 ? null : {index, length: search.length};
}

export default SearchSuggestionsMenu;
