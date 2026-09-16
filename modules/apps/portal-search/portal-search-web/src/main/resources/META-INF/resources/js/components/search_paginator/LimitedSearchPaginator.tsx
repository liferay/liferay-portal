/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {Pagination} from '@clayui/pagination';
import {sub} from 'frontend-js-web';
import React from 'react';

import SearchPaginatorBar, {
	ARIA_LABELS,
	ISearchPaginatorProps,
	createHrefConstructor,
} from './SearchPaginatorBar';

const DEFAULT_VISIBLE_PAGE_COUNT = 5;

interface ILimitedPaginationProps {
	activePage: number;
	ariaLabels: {
		link: string;
		next: string;
		previous: string;
	};
	hrefConstructor: (page: number) => string;
	label: string;

	/**
	 * The last page the total accounts for. When the total is a floor, pages
	 * may exist beyond it, so this only bounds which pages can be linked.
	 */
	lastKnownPage: number;

	/**
	 * Whether results exist past `lastKnownPage`.
	 */
	moreItemsAvailable: boolean;

	/**
	 * How many pages are visible to the user in the limited band.
	 */
	visiblePageCount: number;
}

const LimitedPagination = ({
	activePage,
	ariaLabels,
	hrefConstructor,
	label,
	lastKnownPage,
	moreItemsAvailable,
	visiblePageCount,
}: ILimitedPaginationProps) => {
	const firstVisiblePage = Math.max(
		1,
		activePage - Math.floor(visiblePageCount / 2)
	);

	const lastVisiblePage = Math.min(
		lastKnownPage,
		firstVisiblePage + visiblePageCount - 1
	);

	const visiblePages = [];

	for (let page = firstVisiblePage; page <= lastVisiblePage; page++) {
		visiblePages.push(page);
	}

	const hasPreviousPage = activePage > 1;

	// Paging forward stays available past the last page that has been counted,
	// because an approximate total is a floor: the results behind it are real,
	// they merely have not been counted.

	const hasNextPage = activePage < lastKnownPage || moreItemsAvailable;

	return (
		<Pagination aria-label={label}>
			<Pagination.Item
				aria-label={
					hasPreviousPage
						? sub(ariaLabels.previous, [activePage - 1])
						: undefined
				}
				as={hasPreviousPage ? undefined : 'div'}
				disabled={!hasPreviousPage}
				href={
					hasPreviousPage
						? hrefConstructor(activePage - 1)
						: undefined
				}
			>
				<ClayIcon symbol="angle-left" />
			</Pagination.Item>

			{visiblePages.map((page) => (
				<Pagination.Item
					active={page === activePage}
					aria-label={sub(ariaLabels.link, [page])}
					href={hrefConstructor(page)}
					key={page}
				>
					{page}
				</Pagination.Item>
			))}

			<Pagination.Item
				aria-label={
					hasNextPage
						? sub(ariaLabels.next, [activePage + 1])
						: undefined
				}
				as={hasNextPage ? undefined : 'div'}
				disabled={!hasNextPage}
				href={hasNextPage ? hrefConstructor(activePage + 1) : undefined}
			>
				<ClayIcon symbol="angle-right" />
			</Pagination.Item>
		</Pagination>
	);
};

interface IProps extends ISearchPaginatorProps {

	/**
	 * How many pages the window holds.
	 */
	visiblePageCount?: number;
}

/**
 * A search paginator that shows a limited range of pages.
 *
 * Built for a total that is only accurate up to the accurate count limit,
 * which leaves the real last page unknown. Nothing here links an end page or
 * enumerates the pages behind an ellipsis; it renders a window around the
 * active page instead, sliding to keep that page in the middle and clamping at
 * the start. The forward arrow is then the only sign that more may follow.
 *
 * It is still correct for an exact total, where the arrow disables on the last
 * page like any other paginator.
 */
const LimitedSearchPaginator = ({
	visiblePageCount = DEFAULT_VISIBLE_PAGE_COUNT,
	...otherProps
}: IProps) => {
	const {
		activeDelta,
		activePage,
		paginationURLTemplate,
		totalItems,
		totalItemsApproximate = false,
	} = otherProps;

	return (
		<SearchPaginatorBar {...otherProps}>
			<LimitedPagination
				activePage={activePage}
				ariaLabels={ARIA_LABELS}
				hrefConstructor={createHrefConstructor(paginationURLTemplate)}
				label={Liferay.Language.get('pagination')}
				lastKnownPage={Math.ceil(totalItems / activeDelta)}
				moreItemsAvailable={totalItemsApproximate}
				visiblePageCount={visiblePageCount}
			/>
		</SearchPaginatorBar>
	);
};

export default LimitedSearchPaginator;
