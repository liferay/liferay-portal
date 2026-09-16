/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import React from 'react';

import SearchPaginatorBar, {
	ARIA_LABELS,
	ISearchPaginatorProps,
	createHrefConstructor,
} from './SearchPaginatorBar';

const ELLIPSIS_BUFFER = 1;

/**
 * A search paginator that shows every page the total accounts for.
 *
 * Shape resembles that of `liferay-ui:search-paginator` markup: the first and
 * last pages are links, and the pages between them sit behind an ellipsis
 * dropdown. It uses an exact total to calculate page count and render the
 * last page link and the ellipsis contents.
 */
const ClassicSearchPaginator = (props: ISearchPaginatorProps) => {
	const {activeDelta, activePage, paginationURLTemplate, totalItems} = props;

	// The active page is controlled by the server and never by Clay. Since this
	// is a link based paginator, component needs to hold `active` fixed to
	// keep every item a real link. There is genuinely nothing to handle
	// in the `onActiveChange` callback, since the server will re-render the
	// component with the new active page when the user clicks a link.

	const ignoreActiveChange = () => {};

	return (
		<SearchPaginatorBar {...props}>
			<ClayPaginationWithBasicItems
				active={activePage}
				aria-label={Liferay.Language.get('pagination')}
				ariaLabels={ARIA_LABELS}
				ellipsisBuffer={ELLIPSIS_BUFFER}
				ellipsisProps={{
					'aria-label': Liferay.Language.get(
						'show-intermediate-pages'
					),
					'title': Liferay.Language.get('show-intermediate-pages'),
				}}
				hrefConstructor={createHrefConstructor(paginationURLTemplate)}
				onActiveChange={ignoreActiveChange}
				totalPages={Math.ceil(totalItems / activeDelta)}
			/>
		</SearchPaginatorBar>
	);
};

export default ClassicSearchPaginator;
