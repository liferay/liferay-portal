/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {getProductsItemSelectorProps} from 'commerce-frontend-js';

export default function propsTransformer({
	additionalProps,
	portletNamespace,
	...props
}) {
	const {currentURL} = additionalProps;

	return {
		...props,
		onClick(event) {
			event.preventDefault();

			openItemSelectorModal({
				...getProductsItemSelectorProps({portletNamespace}),
				items: [],
				onItemsChange(selectedProducts) {
					if (!selectedProducts || !selectedProducts.length) {
						return;
					}

					Liferay.Util.postForm(document[`${portletNamespace}fm`], {
						data: {
							cmd: 'add-selection',
							cpDefinitionIds: selectedProducts.map(
								(selectedProduct) => selectedProduct.id
							),
							redirect: currentURL,
						},
					});
				},
			});
		},
	};
}
