/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '@liferay/frontend-data-set-web';
import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import ProductRelationshipSelectorNameRenderer from './cell_renderers/ProductRelationshipSelectorNameRenderer';
import addProductRelationships, {
	getRelatedObjectEntryIds,
} from './services/ProductRelationshipService';

import '../css/ProductRelationshipSelector.scss';

const NAME_TABLE_CELL_RENDERER = 'nameTableCellRenderer';

type ProductRelationshipData = {
	className: string;
	externalReferenceCode: string;
	filters: string;
	name: string;
	scopeKey: string;
	searchAPIURL: string;
};

type Product = {
	embedded: {externalReferenceCode: string};
};

export default async function openProductRelationshipSelectorModal(
	{
		className,
		externalReferenceCode,
		filters,
		name,
		scopeKey,
		searchAPIURL,
	}: ProductRelationshipData,
	loadData?: () => void
) {
	const relatedObjectEntryIds = await getRelatedObjectEntryIds({
		className,
		externalReferenceCode,
		scopeKey,
	}).catch(() => []);

	const apiURL =
		`${window.location.origin}${Liferay.ThemeDisplay.getPathContext()}${searchAPIURL}`.replace(
			'{relatedObjectEntryIds}',
			relatedObjectEntryIds
				.map((relatedObjectEntryId) => `,${relatedObjectEntryId}`)
				.join('')
		);

	openItemSelectorModal({
		apiURL,
		confirmButtonLabel: Liferay.Language.get('add'),
		fdsProps: {
			customRenderers: {
				tableCell: [
					{
						component: ProductRelationshipSelectorNameRenderer,
						name: NAME_TABLE_CELL_RENDERER,
						type: 'internal',
					},
				],
			},
			filters: JSON.parse(filters || '[]'),
			id: 'productRelationshipSelector',
			views: [
				{
					contentRenderer: 'table',
					label: Liferay.Language.get('table'),
					name: 'table',
					schema: {
						fields: [
							{
								contentRenderer: NAME_TABLE_CELL_RENDERER,
								fieldName: 'embedded.name',
								label: Liferay.Language.get('name'),
							},
							{
								fieldName: 'embedded.code',
								label: Liferay.Language.get('sku'),
							},
							{
								fieldName:
									'embedded.systemProperties.objectDefinitionBrief.label',
								label: Liferay.Language.get('type'),
							},
							{
								contentRenderer: 'status',
								fieldName: 'embedded.status',
								label: Liferay.Language.get('status'),
							},
						],
					},
					thumbnail: 'table',
				},
			] as IView[],
		},
		items: [],
		locator: {
			id: 'embedded.externalReferenceCode',
			label: 'embedded.name',
			value: 'embedded.externalReferenceCode',
		},
		multiSelect: true,
		onItemsChange: async (products: Product[]) => {
			const response = await addProductRelationships({
				scopeKey,
				sourceLinkReference: {className, externalReferenceCode},
				targetLinkReferences: products.map((product) => ({
					className,
					externalReferenceCode:
						product.embedded.externalReferenceCode,
				})),
				type: 'variant',
			});

			if (!response.ok) {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});

				return;
			}

			openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});

			loadData?.();
		},
		size: 'lg',
		title: sub(Liferay.Language.get('create-relationship-with-x'), name),
	});
}
