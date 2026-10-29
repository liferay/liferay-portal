/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import ServiceProvider from '../ServiceProvider/index';

const AdminCatalogAPIServiceProvider = ServiceProvider.AdminCatalogAPI('v1');

export default function getProductsItemSelectorProps({
	channelId,
	excludedProductIds,
	excludedProductTypes,
	multiSelect = true,
	portletNamespace,
	title = sub(
		Liferay.Language.get('select-x'),
		Liferay.Language.get('product')
	),
}) {
	return {
		apiURL: AdminCatalogAPIServiceProvider.getProductsURL({
			channelId,
			excludedProductIds,
			excludedProductTypes,
		}),
		fdsProps: {
			configInURLBehavior: 'OFF',
			id: `${portletNamespace}productsItemSelector`,
			pagination: {
				deltas: [{label: 20}, {label: 40}, {label: 60}],
				initialDelta: 20,
			},
			views: [
				{
					contentRenderer: 'table',
					label: '',
					name: 'table',
					schema: {
						fields: [
							{
								contentRenderer: 'image',
								fieldName: 'thumbnail',
								label: Liferay.Language.get('image'),
							},
							{
								fieldName: ['name', 'LANG'],
								label: Liferay.Language.get('name'),
							},
							{
								fieldName: 'skuFormatted',
								label: Liferay.Language.get('sku'),
							},
							{
								fieldName: 'catalog.name',
								label: Liferay.Language.get('catalog'),
							},
							{
								contentRenderer: 'status',
								fieldName: 'workflowStatusInfo',
								label: Liferay.Language.get('status'),
							},
						],
					},
				},
			],
		},
		itemTypeLabel: Liferay.Language.get('product'),
		locator: {
			id: 'id',
			label: `name.${Liferay.ThemeDisplay.getLanguageId()}`,
			value: 'id',
		},
		multiSelect,
		title,
	};
}
