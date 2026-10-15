/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import {
	StatusLabel,
	addOnClickToCreationMenuItems,
	confirmAndDeleteEntryAction,
} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';
import React from 'react';

import ProductRelationshipSelectorModal from './ProductRelationshipSelectorModal';
import ProductRelationshipSelectorTitleRenderer from './cell_renderers/ProductRelationshipSelectorTitleRenderer';

const ACTIONS = {
	createProductRelationship: (data: any, loadData?: () => void) =>
		render(
			ProductRelationshipSelectorModal,
			{...data, loadData},
			document.createElement('div')
		),
};

export default function propsTransformer({
	additionalProps,
	creationMenu,
	itemsActions,
	...props
}: {
	additionalProps?: {objectEntryId: string};
	creationMenu?: any;
	itemsActions?: any[];
	[key: string]: any;
}) {
	const isCurrent = (itemData: {id: number}) =>
		String(itemData?.id) === additionalProps?.objectEntryId;

	return {
		...props,
		creationMenu: creationMenu && {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({itemData, value}: any) => (
						<ProductRelationshipSelectorTitleRenderer
							current={isCurrent(itemData)}
							value={value}
						/>
					),
					name: 'titleTableCellRenderer',
					type: 'internal',
				},
				{
					component: ({value}: any) =>
						value ? Liferay.Language.get('yes') : '-',
					name: 'primaryTableCellRenderer',
					type: 'internal',
				},
				{
					component: ({value}: any) => (
						<StatusLabel label={value?.label} />
					),
					name: 'statusTableCellRenderer',
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
		itemsActions: itemsActions?.map((itemsAction) => {
			if (itemsAction?.data?.id !== 'view') {
				return itemsAction;
			}

			return {
				...itemsAction,
				isVisible: (itemData: {id: number}) => !isCurrent(itemData),
			};
		}),
		onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {data?: {id?: string}};
			event: Event;
			itemData: {
				actions: {delete: {href: string; method: string}};
				title: string;
			};
			loadData: () => void;
		}) {
			if (action?.data?.id !== 'delete') {
				return;
			}

			event.preventDefault();

			confirmAndDeleteEntryAction({
				bodyHTML: Liferay.Language.get(
					'the-relationship-will-be-removed.-this-action-cannot-be-undone'
				),
				confirmButtonLabel: Liferay.Language.get('remove'),
				deleteAction: itemData.actions.delete,
				loadData,
				status: 'warning',
				successMessage: sub(
					Liferay.Language.get('x-was-removed-successfully'),
					Liferay.Util.escapeHTML(itemData.title)
				),
				title: sub(Liferay.Language.get('remove-x'), itemData.title),
			});
		},
	};
}
