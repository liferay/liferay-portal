/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import React, {useEffect, useRef, useState} from 'react';

interface IMatch {
	index: number;
	length: number;
}

interface IProps {
	active: boolean;
	alignElementRef: React.RefObject<HTMLElement>;
	onActiveChange: (active: boolean) => void;
	onClearAll: () => void;
	onQueryClick: (query: string) => void;
	onQueryRemove: (query: string) => void;
	queries: Array<string>;
	value: string;
}

function RecentSearches({
	active,
	alignElementRef,
	onActiveChange,
	onClearAll,
	onQueryClick,
	onQueryRemove,
	queries,
	value,
}: IProps) {
	const menuRef = useRef<HTMLDivElement>(null);

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
		if (active && searchBoxWidth === 0) {
			onActiveChange(false);
		}
	}, [active, onActiveChange, searchBoxWidth]);

	const matchedQueries = queries
		.map((query) => ({match: _getMatch(query, value), query}))
		.filter(({match}) => !!match) as Array<{match: IMatch; query: string}>;

	// The menu keeps its markup once mounted, so it is only mounted while it
	// has something to show and leaves nothing for a screen reader otherwise.

	if (!active || !matchedQueries.length) {
		return null;
	}

	return (
		<ClayDropDown.Menu
			active={active}
			alignElementRef={alignElementRef}
			className="fds-recent-searches"
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
			<ClayDropDown.Caption className="fds-recent-searches-caption">
				<span className="text-secondary text-uppercase">
					{Liferay.Language.get('recent-searches')}
				</span>

				<ClayButton
					className="fds-recent-searches-clear-all"
					displayType="link"
					onClick={onClearAll}
					small
				>
					{Liferay.Language.get('clear-all')}
				</ClayButton>
			</ClayDropDown.Caption>

			<ClayDropDown.ItemList>
				{matchedQueries.map(({match, query}) => (
					<li
						className="fds-recent-searches-item"
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
							className="fds-recent-searches-item-remove"
							displayType="unstyled"
							onClick={() => onQueryRemove(query)}
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

export default RecentSearches;
