/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Autocomplete,
	CommerceServiceProvider,
	modalUtils,
} from 'commerce-frontend-js';
import {createPortletURL} from 'frontend-js-web';

export default function main({
	defaultSku,
	draft,
	editProductDefinitionURL,
	namespace,
	ppState,
	productTypeName,
}) {
	let defaultLanguageId = null;
	const productData = {
		active: true,
		productStatus: draft,
		productType: productTypeName,
	};

	const AdminCatalogResource = CommerceServiceProvider.AdminCatalogAPI('v1');

	Liferay.provide(window, `${namespace}apiSubmit`, () => {
		modalUtils.isSubmitting();

		const formattedData = {...productData, defaultSku, name: {}};
		const nameInput = document.getElementById(`${namespace}name`).value;

		formattedData.name[defaultLanguageId] = nameInput;

		if (
			defaultLanguageId === Liferay.ThemeDisplay.getDefaultLanguageId() &&
			Liferay.ThemeDisplay.getLanguageId() !==
				Liferay.ThemeDisplay.getDefaultLanguageId()
		) {
			formattedData.name[Liferay.ThemeDisplay.getLanguageId()] =
				nameInput;
		}

		AdminCatalogResource.createProduct(formattedData)
			.then((cpDefinition) => {
				const redirectURL = createPortletURL(editProductDefinitionURL, {
					cpDefinitionId: cpDefinition.id,
					p_p_state: ppState,
				});

				modalUtils.closeAndRedirect(redirectURL);
			})
			.catch(modalUtils.onSubmitFail);
	});

	Autocomplete('autocomplete', 'autocomplete-root', {
		apiUrl: '/o/headless-commerce-admin-catalog/v1.0/catalogs',
		inputId: `${namespace}catalogId`,
		inputName: `${namespace}catalogId`,
		itemsKey: 'id',
		itemsLabel: 'name',
		onValueUpdated(value, catalogData) {
			if (value) {
				productData.catalogId = catalogData.id;
				defaultLanguageId = catalogData.defaultLanguageId;
			}
		},
		required: true,
	});
}
