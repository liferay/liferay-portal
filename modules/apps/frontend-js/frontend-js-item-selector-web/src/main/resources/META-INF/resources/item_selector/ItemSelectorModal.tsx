/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayModal from '@clayui/modal';
import {InternalDispatch} from '@clayui/shared';
import {
	ACTION_ITEM_TARGETS,
	FrontendDataSet,
	IFrontendDataSetProps,
} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import {getObjectValueFromPath, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

type IItemSelectorModalFDSProps = Omit<
	IFrontendDataSetProps,
	| 'apiURL'
	| 'selectedItems'
	| 'selectedItemsKey'
	| 'onSelectedItemsChange'
	| 'selectedItems'
	| 'selectedItemsKey'
	| 'selectionType'
	| 'showNavBarWhenSelected'
	| 'style'
>;

export interface IItemSelectorModalProps<T> {

	/**
	 * The URL that will be fetched to return the items.
	 */
	apiURL: string;

	/**
	 * Configuration of @clayui/breadcrumb items to show above the FDS table
	 */
	breadcrumbs?: React.ComponentProps<typeof ClayBreadcrumb>['items'];

	/**
	 * URL for item creation used to open a new tab.
	 */
	createItemURL?: string;

	/**
	 * Configuration properties of the Frontend Data Set used to display data.
	 */
	fdsProps: IItemSelectorModalFDSProps;

	/**
	 * The displayed label for the type of item being selected. Used in the
	 * modal title.
	 */
	itemTypeLabel: string;

	/**
	 * Items that are currently selected (controlled).
	 */
	items: T[];

	/**
	 * A string key used to locate the id, label, or value within each item.
	 * Can be used as a period separated path (e.g.: 'embedded.id').
	 */
	locator?: {
		id: string;
		label: string;
		value: string;
	};

	/**
	 * Flag for if multiple items can be selected.
	 */
	multiSelect?: boolean;

	/**
	 * Expects the 'observer' property from the Clay useModal hook.
	 */
	observer: any;

	/**
	 * Callback for when items are added or removed. Only called when modal selection is confirmed (controlled).
	 */
	onItemsChange: InternalDispatch<T[]>;

	/**
	 * Expects the 'onOpenChange' property from the Clay useModal hook.
	 */
	onOpenChange: (value: boolean) => void;

	/**
	 * Expects the 'open' property from the Clay useModal hook.
	 */
	open: boolean;
}

const EMPTY_STATE_PROPS = {
	description: Liferay.Language.get(
		'fortunately-it-is-very-easy-to-add-new-ones'
	),
	title: Liferay.Language.get('no-items-were-found'),
};

function ItemSelectorModal<T extends Record<string, any>>({
	apiURL,
	breadcrumbs,
	createItemURL,
	fdsProps,
	itemTypeLabel,
	items: externalItems,
	locator = {
		id: 'id',
		label: 'title',
		value: 'id',
	},
	multiSelect = false,
	observer,
	onItemsChange,
	onOpenChange,
	open,
}: IItemSelectorModalProps<T>) {
	const [selectedItems, setSelectedItems] = useState(externalItems);

	useEffect(() => {
		if (!open) {
			setSelectedItems(externalItems);
		}
	}, [externalItems, open]);

	const getSelectedItemLabel = function (selectedItem: T) {
		return getObjectValueFromPath({
			object: selectedItem,
			path: locator.label,
		});
	};

	const hasSelectedItems = !!selectedItems.length;

	return open ? (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header>
				{sub(Liferay.Language.get('select-x'), itemTypeLabel)}
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				{breadcrumbs && (
					<ClayLayout.Container fluid>
						<h2 className="mb-0 mt-2">
							{breadcrumbs[breadcrumbs.length - 1].label}
						</h2>

						<ClayBreadcrumb
							items={breadcrumbs.map((breadcrumb, index) => ({
								...breadcrumb,
								active: index === breadcrumbs.length - 1,
							}))}
						/>
					</ClayLayout.Container>
				)}

				<FrontendDataSet
					{...fdsProps}
					apiURL={apiURL}
					creationMenu={
						createItemURL
							? {
									primaryItems: [
										{
											href: createItemURL,
											label: Liferay.Language.get(
												'add-new-item'
											),
											target: ACTION_ITEM_TARGETS.BLANK,
										},
									],
								}
							: undefined
					}
					emptyState={createItemURL ? EMPTY_STATE_PROPS : undefined}
					onSelectedItemsChange={setSelectedItems}
					selectedItems={selectedItems}
					selectedItemsKey={locator.id}
					selectionType={multiSelect ? 'multiple' : 'single'}
					showNavBarWhenSelected={true}
					style="fluid"
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				className={classNames({
					'bg-primary-l3 border-primary border-top': hasSelectedItems,
				})}
				first={
					hasSelectedItems ? (
						<div className="align-items-center d-flex">
							{selectedItems.length > 1
								? sub(
										Liferay.Language.get(
											'x-items-selected'
										),
										selectedItems.length
									)
								: sub(
										Liferay.Language.get('x-selected'),
										getSelectedItemLabel(selectedItems[0])
									)}

							<ClayButton
								className="ml-3 text-secondary"
								displayType="link"
								onClick={() => {
									setSelectedItems([]);
								}}
							>
								<strong>{Liferay.Language.get('clear')}</strong>
							</ClayButton>
						</div>
					) : undefined
				}
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="btn-cancel"
							displayType="secondary"
							onClick={() => {
								setSelectedItems([]);

								onOpenChange(false);
							}}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="item-preview selector-button"
							disabled={!hasSelectedItems}
							onClick={() => {
								onItemsChange(
									multiSelect
										? selectedItems
										: selectedItems.slice(0, 1)
								);

								onOpenChange(false);
							}}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : (
		<></>
	);
}

export default ItemSelectorModal;
