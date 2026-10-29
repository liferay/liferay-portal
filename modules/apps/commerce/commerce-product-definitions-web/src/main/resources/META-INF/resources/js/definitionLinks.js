/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {getProductsItemSelectorProps} from 'commerce-frontend-js';

export default function ({excludedProductIdsMap, namespace, title}) {
	const eventHandlers = Object.entries(excludedProductIdsMap).map(
		([type, typeExcludedProductIds]) =>
			Liferay.on(
				`${namespace}addCommerceProductDefinitionLink${type}`,
				() => {
					openItemSelectorModal({
						...getProductsItemSelectorProps({
							excludedProductIds: typeExcludedProductIds,
							portletNamespace: namespace,
							title,
						}),
						items: [],
						onItemsChange(selectedProducts) {
							if (!selectedProducts || !selectedProducts.length) {
								return;
							}

							Liferay.Util.postForm(
								document[`${namespace}addCPDefinitionLinkFm`],
								{
									data: {
										cpDefinitionIds: selectedProducts.map(
											(selectedProduct) =>
												selectedProduct.id
										),
										type,
									},
								}
							);
						},
					});
				}
			)
	);

	return {
		dispose() {
			eventHandlers.forEach((eventHandler) => eventHandler.detach());
		},
	};
}
