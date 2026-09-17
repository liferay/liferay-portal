/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import {
	AlignPoints,
	InternalDispatch,
	sub,
	useControlledState,
	useId,
} from '@clayui/shared';
import React, {useEffect} from 'react';

import {PaginationBar} from './PaginationBar';

const defaultDeltas = [
	{
		label: 10,
	},
	{
		label: 20,
	},
	{
		label: 30,
	},
	{
		label: 50,
	},
];

interface IDelta {

	/**
	 * Path or URL to be used for some SPA focused use cases.
	 */
	href?: string;

	/**
	 * Label of the delta
	 */
	label: number;
}

interface IProps extends React.ComponentProps<typeof PaginationBar> {

	/**
	 * Sets the currently active page (controlled).
	 */
	active?: number;

	/**
	 * The value of delta that is currently selected
	 */
	activeDelta?: number;

	/**
	 * Initialize the page that is currently active. The first page is `1`.
	 * @deprecated since v3.52.0 - use `active` instead.
	 */
	activePage?: number;

	/**
	 * Sets the default DropDown position of the component. The component
	 * receives the Align constant values from the `@clayui/drop-down` package.
	 */
	alignmentPosition?: number | AlignPoints;

	/**
	 * Sets the default active page (uncontrolled).
	 */
	defaultActive?: number;

	/**
	 * Possible values of items per page.
	 */
	deltas?: Array<IDelta>;

	/**
	 * Flag to disable ellipsis button
	 */
	disableEllipsis?: boolean;

	/**
	 * The page numbers that should be disabled. For example, `[2,5,6]`.
	 */
	disabledPages?: Array<number>;

	/**
	 * The number of pages to show on each side of the active page before
	 * using an ellipsis dropdown.
	 */
	ellipsisBuffer?: number;

	/**
	 * Properties to pass to the ellipsis trigger.
	 */
	ellipsisProps?: Object;

	/**
	 * Function used to create the href provided for each page link.
	 */
	hrefConstructor?: (page?: number) => string;

	/**
	 * Labels for changing some texts inside the component.
	 * Use this property for i18n.
	 */
	labels?: Partial<typeof DEFAULT_LABELS>;

	/**
	 * Callback called when the state of the active page changes (controlled).
	 * This is only used if an href is not provided.
	 */
	onActiveChange?: InternalDispatch<number>;

	/**
	 * Callback for when the number of elements per page changes. This is only used if
	 * an href is not provided.
	 */
	onDeltaChange?: (page: number) => void;

	/**
	 * Callback for when the active page changes. This is only used if
	 * an href is not provided.
	 * @deprecated since v3.52.0 - use `onActiveChange` instead.
	 */
	onPageChange?: (page: number) => void;

	/**
	 * Flags indicating if the DropDown should be rendered.
	 */
	showDeltasDropDown?: boolean;

	/**
	 * Path to spritemap from clay-css.
	 */
	spritemap?: string;

	/**
	 * The total number of items in the pagination list.
	 */
	totalItems: number;
}

const DEFAULT_LABELS = {
	ellipsisAriaLabel: 'Show Pages {0} Through {1}',
	itemsPerPagePickerAriaLabel: 'Items Per Page',
	nextPageAriaLabel: 'Go to the Next Page, {0}',
	pageLinkAriaLabel: 'Go to Page, {0}',
	paginationResults: 'Showing {0} to {1} of {2}',
	perPageItems: '{0} items',
	previousPageAriaLabel: 'Go to the Previous Page, {0}',
	selectPerPageItems: '{0} items',
};

const Trigger = React.forwardRef<HTMLButtonElement>(
	(
		{activeDelta, labels, spritemap, ...otherProps}: Record<string, any>,
		ref
	) => {
		return (
			<ClayButton
				{...otherProps}
				className="dropdown-toggle"
				displayType="unstyled"
				ref={ref}
			>
				{sub(labels.perPageItems, [activeDelta])}

				<ClayIcon spritemap={spritemap} symbol="caret-double-l" />
			</ClayButton>
		);
	}
);

Trigger.displayName = 'Trigger';

export function ClayPaginationBarWithBasicItems({
	active,
	activeDelta,
	activePage,
	alignmentPosition,
	defaultActive = 1,
	deltas = defaultDeltas,
	disabledPages,
	disableEllipsis = false,
	ellipsisBuffer,
	ellipsisProps,
	hrefConstructor,
	labels: externalLabels,
	onActiveChange,
	onDeltaChange,
	onPageChange,
	showDeltasDropDown = true,
	spritemap,
	totalItems,
	...otherProps
}: IProps) {
	if (totalItems === 0) {
		totalItems = 1;
	}
	const [internalActive, setActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'value',
		onChange: onActiveChange ?? onPageChange,
		value: typeof active === 'undefined' ? activePage : active,
	});
	const labels = {...DEFAULT_LABELS, ...externalLabels};
	if (!activeDelta) {
		activeDelta = deltas[0]!.label;
	}
	const totalPages = Math.ceil(totalItems / activeDelta);
	useEffect(() => {
		if (internalActive > totalPages) {
			setActive(1);
		}
	}, [totalPages]);
	const ariaDescribedby = useId();

	return (
		<PaginationBar {...otherProps}>
			{showDeltasDropDown && (
				<div className="dropdown pagination-items-per-page">
					<Picker
						activeDelta={activeDelta}
						aria-describedby={ariaDescribedby}
						aria-label={labels.itemsPerPagePickerAriaLabel}
						as={Trigger}
						items={deltas}
						labels={labels}
						onSelectionChange={(key) => {
							onDeltaChange && onDeltaChange(Number(key));
						}}
						selectedKey={String(activeDelta)}
						spritemap={spritemap}
					>
						{(item) => (
							<Option href={item.href} key={item.label}>
								{sub(labels.selectPerPageItems, [
									String(item.label),
								])}
							</Option>
						)}
					</Picker>
				</div>
			)}

			<PaginationBar.Results id={ariaDescribedby}>
				{sub(labels.paginationResults, [
					(internalActive - 1) * activeDelta + 1,
					internalActive * activeDelta < totalItems
						? internalActive * activeDelta
						: totalItems,
					totalItems,
				])}
			</PaginationBar.Results>

			<ClayPaginationWithBasicItems
				active={internalActive}
				alignmentPosition={alignmentPosition}
				ariaLabels={{
					ellipsis: labels.ellipsisAriaLabel,
					link: labels.pageLinkAriaLabel,
					next: labels.nextPageAriaLabel,
					previous: labels.previousPageAriaLabel,
				}}
				disableEllipsis={disableEllipsis}
				disabledPages={disabledPages}
				ellipsisBuffer={ellipsisBuffer}
				ellipsisProps={ellipsisProps}
				hrefConstructor={hrefConstructor}
				onActiveChange={setActive}
				spritemap={spritemap}
				totalPages={totalPages}
			/>
		</PaginationBar>
	);
}
