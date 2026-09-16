/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {PaginationBar} from '@clayui/pagination-bar';
import {useId} from '@clayui/shared';
import {sub} from 'frontend-js-web';
import React from 'react';

export interface IDelta {

	/**
	 * URL that switches the results to this page size.
	 */
	href: string;

	label: number;
}

export interface ISearchPaginatorProps {
	activeDelta: number;
	activePage: number;

	deltas: Array<IDelta>;

	/**
	 * The page link with `{0}` where the page number goes. The server sorts it
	 * with the page parameter already in place, so substituting the number
	 * cannot disturb the parameter order.
	 */
	paginationURLTemplate: string;

	showDeltasDropDown?: boolean;
	totalItems: number;

	/**
	 * Whether `totalItems` is a floor rather than an exact figure, which
	 * is what the search returns once the results outgrow its accurate
	 * count limit.
	 */
	totalItemsApproximate?: boolean;
}

const Trigger = React.forwardRef<HTMLButtonElement>(
	({activeDelta, label, ...otherProps}: Record<string, any>, ref) => (
		<ClayButton
			{...otherProps}
			className="dropdown-toggle"
			displayType="unstyled"
			ref={ref}
		>
			{sub(label, [activeDelta])}

			<ClayIcon symbol="caret-double-l" />
		</ClayButton>
	)
);

Trigger.displayName = 'Trigger';

/**
 * Builds the link to a page. The server owns the URL and hands over a finished
 * one, so nothing here assembles a query string.
 *
 * The page is optional only to match the signature Clay declares for
 * `hrefConstructor`. Every caller, Clay's included, passes one.
 */
export function createHrefConstructor(paginationURLTemplate: string) {
	return (page?: number) => sub(paginationURLTemplate, [page]);
}

export const ARIA_LABELS = {
	link: Liferay.Language.get('page-x'),
	next: Liferay.Language.get('next-page'),
	previous: Liferay.Language.get('previous-page'),
};

interface IProps extends ISearchPaginatorProps {

	/**
	 * The pagination itself, which is all that separates one paginator from
	 * another.
	 */
	children: React.ReactNode;
}

/**
 * The frame both search paginators share: the items per page picker, the
 * result summary, and whichever pagination the caller puts inside it.
 *
 * Everything here is link based. The server owns the URLs, so each items per
 * page option is an ordinary link and nothing fetches data or holds state.
 */
const SearchPaginatorBar = ({
	activeDelta,
	activePage,
	children,
	deltas,
	showDeltasDropDown = true,
	totalItems,
	totalItemsApproximate = false,
}: IProps) => {

	// Clay's own `useId` rather than React's, which only exists from React 18.

	const resultsId = useId();
	const reloadsId = useId();

	const numberFormat = new Intl.NumberFormat(
		Liferay.ThemeDisplay.getBCP47LanguageId()
	);

	// `totalItems` stays an exact number so the page count and the upper bound
	// below remain arithmetic. When the search only counted up to its accurate
	// count limit, that number is a floor rather than a true total, so it is
	// the rendered label that gains the "or more" marker, not the value.

	const totalItemsLabel = totalItemsApproximate
		? sub(Liferay.Language.get('x-plus'), [numberFormat.format(totalItems)])
		: numberFormat.format(totalItems);

	return (
		<PaginationBar>
			{showDeltasDropDown && (
				<div className="dropdown pagination-items-per-page">
					<Picker
						activeDelta={activeDelta}
						aria-describedby={`${resultsId} ${reloadsId}`}
						aria-label={Liferay.Language.get('items-per-page')}
						as={Trigger}
						defaultSelectedKey={String(activeDelta)}
						items={deltas}
						label={Liferay.Language.get('x-entries')}
					>
						{(item: IDelta) => (
							<Option
								href={item.href}
								key={item.label}
								textValue={`${item.label}\u00a0${Liferay.Language.get('entries-per-page')}`}
							>
								{item.label}

								<span className="sr-only">
									{'\u00a0'}

									{Liferay.Language.get('entries-per-page')}
								</span>
							</Option>
						)}
					</Picker>

					<span className="sr-only" id={reloadsId}>
						{Liferay.Language.get(
							'selecting-an-option-will-reload-the-page'
						)}
					</span>
				</div>
			)}

			<PaginationBar.Results id={resultsId}>
				{sub(Liferay.Language.get('showing-x-to-x-of-x-entries'), [
					numberFormat.format((activePage - 1) * activeDelta + 1),
					numberFormat.format(
						Math.min(activePage * activeDelta, totalItems)
					),
					totalItemsLabel,
				])}
			</PaginationBar.Results>

			{children}
		</PaginationBar>
	);
};

export default SearchPaginatorBar;
