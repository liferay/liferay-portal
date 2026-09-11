/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import React, {useContext, useEffect, useRef, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import recentSearches from '../../utils/recentSearches';
import recentlyVisited, {
	IRecentlyVisitedItem,
} from '../../utils/recentlyVisited';

interface IMatch {
	index: number;
	length: number;
}

interface IProps {
	alignElementRef: React.RefObject<HTMLElement>;
	onActiveChange: (active: boolean) => void;
	onQueryClick: (query: string) => void;
	onVisitedItemClick: () => void;
	value: string;
}

function SearchSuggestionsMenu({
	alignElementRef,
	onActiveChange,
	onQueryClick,
	onVisitedItemClick,
	value,
}: IProps) {
	const {id} = useContext(FrontendDataSetContext);

	const menuRef = useRef<HTMLDivElement>(null);

	const [queries, setQueries] = useState(() => recentSearches.get(id));
	const [visitedItems, setVisitedItems] = useState(() =>
		recentlyVisited.get(id)
	);

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

	const matchedVisitedItems = visitedItems
		.map((visitedItem) => ({
			...visitedItem,
			match: _getMatch(visitedItem.label, value),
		}))
		.filter(({match}) => !!match) as Array<
		IRecentlyVisitedItem & {match: IMatch}
	>;

	// Clay keeps the menu's markup in the page once it is mounted, so it is
	// rendered only while something matches and leaves nothing for a screen
	// reader otherwise

	if (!matchedQueries.length && !matchedVisitedItems.length) {
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
			<ul className="list-unstyled" role="menu">
				{!!matchedQueries.length && (
					<ClayDropDown.Group
						header={Liferay.Language.get('recent-searches')}
					>
						{matchedQueries.map(({match, query}) => (
							<Entry
								className="fds-search-suggestions-query-item"
								key={query}
								label={query}
								match={match}
								onClick={() => onQueryClick(query)}
								onRemove={() =>
									setQueries(recentSearches.remove(id, query))
								}
								removeTitle={Liferay.Language.get(
									'clear-search'
								)}
							/>
						))}
					</ClayDropDown.Group>
				)}

				{!!matchedVisitedItems.length && (
					<ClayDropDown.Group
						header={Liferay.Language.get('recently-visited')}
					>
						{matchedVisitedItems.map(({href, label, match}) => (
							<Entry
								className="fds-search-suggestions-visited-item"
								href={href}
								key={href}
								label={label}
								match={match}
								onClick={onVisitedItemClick}
								onRemove={() =>
									setVisitedItems(
										recentlyVisited.remove(id, href)
									)
								}
								removeTitle={Liferay.Language.get('remove')}
							/>
						))}
					</ClayDropDown.Group>
				)}

				<ClayDropDown.Divider />

				<li
					className="fds-search-suggestions-clear-all"
					role="presentation"
				>
					<button
						className="dropdown-item"
						onClick={() => {
							setQueries(recentSearches.clear(id));
							setVisitedItems(recentlyVisited.clear(id));
						}}
						role="menuitem"
						type="button"
					>
						{Liferay.Language.get('clear-all')}
					</button>
				</li>
			</ul>
		</ClayDropDown.Menu>
	);
}

/**
 * A stored query fills the search box, so it is a button, while a visited item
 * takes the user somewhere and is a link, which also lets them open it in
 * another tab the way they would open the row it came from.
 */
function Entry({
	className,
	href,
	label,
	match,
	onClick,
	onRemove,
	removeTitle,
}: {
	className: string;
	href?: string;
	label: string;
	match: IMatch;
	onClick: () => void;
	onRemove: () => void;
	removeTitle: string;
}) {
	const content = match.length ? (
		<>
			{label.slice(0, match.index)}

			<strong>
				{label.slice(match.index, match.index + match.length)}
			</strong>

			{label.slice(match.index + match.length)}
		</>
	) : (
		label
	);

	return (
		<li
			className={`fds-search-suggestions-item ${className}`}
			role="presentation"
		>
			{href ? (
				<a
					aria-label={label}
					className="dropdown-item"
					href={href}
					onClick={onClick}
					role="menuitem"
				>
					{content}
				</a>
			) : (
				<button
					aria-label={label}
					className="dropdown-item"
					onClick={onClick}
					role="menuitem"
					type="button"
				>
					{content}
				</button>
			)}

			<ClayButtonWithIcon
				aria-label={removeTitle}
				className="fds-search-suggestions-item-remove"
				displayType="unstyled"
				onClick={onRemove}
				role="menuitem"
				size="sm"
				symbol="times-small"
				title={removeTitle}
			/>
		</li>
	);
}

/**
 * Returns the range of a stored label the input matches, or null when it does
 * not. An empty input matches every label with an empty range.
 */
function _getMatch(label: string, value: string): IMatch | null {
	const search = value.trim().toLowerCase();

	if (!search) {
		return {index: 0, length: 0};
	}

	const index = label.toLowerCase().indexOf(search);

	return index === -1 ? null : {index, length: search.length};
}

export default SearchSuggestionsMenu;
