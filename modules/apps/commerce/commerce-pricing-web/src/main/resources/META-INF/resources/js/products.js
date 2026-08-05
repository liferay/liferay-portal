/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CommerceServiceProvider,
	ItemFinder,
	commerceEvents,
} from 'commerce-frontend-js';

export default function ({
	commerceDiscountRuleId,
	namespace,
	portletId,
	pricingFDSName,
	spritemap,
}) {
	const CommerceDiscountRuleResource =
		CommerceServiceProvider.AdminPricingAPI('v2');

	const form = document.getElementById(`${namespace}fm`);

	const name = form.querySelector(`#${namespace}name`).value;

	const commerceDiscountRuleType = form.querySelector(
		`#${namespace}type`
	).value;
	let typeSettings = form.querySelector(`#${namespace}typeSettings`).value;

	function addProductDefinition(externalReferenceCode) {
		if (typeSettings === '') {
			return typeSettings.concat(externalReferenceCode);
		}

		return typeSettings.concat(',').concat(externalReferenceCode);
	}

	function selectItem(product) {
		const ruleData = {
			name,
			type: commerceDiscountRuleType,
			typeSettings: addProductDefinition(product.externalReferenceCode),
		};

		return CommerceDiscountRuleResource.updateDiscountRule(
			commerceDiscountRuleId,
			ruleData
		)
			.then((payload) => {
				Liferay.fire(commerceEvents.FDS_UPDATE_DISPLAY, {
					id: pricingFDSName,
				});

				typeSettings = payload.typeSettings
					.replace(/(\r\n|\n|\r)/gm, '')
					.split('=')[1];

				document.getElementById(`${namespace}typeSettings`).value =
					typeSettings;
			})
			.catch((error) => {
				return Promise.reject(error);
			});
	}

	function getSelectedItems() {
		return Promise.resolve([]);
	}

	ItemFinder('itemFinder', 'item-finder-root', {
		apiUrl: '/o/headless-commerce-admin-catalog/v1.0/products?nestedFields=catalog',
		getSelectedItems,
		inputPlaceholder: Liferay.Language.get('find-a-product'),
		itemCreation: false,
		itemSelectedMessage: Liferay.Language.get('product-selected'),
		itemsKey: 'id',
		linkedDataSetsId: [pricingFDSName],
		onItemSelected: selectItem,
		pageSize: 10,
		panelHeaderLabel: Liferay.Language.get('add-products'),
		portletId,
		schema: [
			{
				fieldName: ['name', 'LANG'],
			},
			{
				fieldName: 'externalReferenceCode',
			},
			{
				fieldName: 'skuFormatted',
			},
			{
				fieldName: ['catalog', 'name'],
			},
		],
		spritemap,
		titleLabel: Liferay.Language.get('add-existing-product'),
	});
}
