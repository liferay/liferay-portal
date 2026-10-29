/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {getProductsItemSelectorProps} from 'commerce-frontend-js';
import {openConfirmModal} from 'frontend-js-components-web';

export function CPDefinitionGroupedManagementToolbarPropsTransformer({
	portletNamespace,
	...props
}) {
	return {
		...props,
		onActionButtonClick(event, {item}) {
			if (item?.data?.action === 'deleteEntries') {
				openConfirmModal({
					message: Liferay.Language.get(
						'are-you-sure-you-want-to-delete-the-selected-entries'
					),
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							const form = document.getElementById(
								`${portletNamespace}fm`
							);

							if (!form) {
								return;
							}

							submitForm(form);
						}
					},
				});
			}
		},
		onCreateButtonClick: (event, {item}) => {
			const data = item?.data;

			openItemSelectorModal({
				...getProductsItemSelectorProps({
					excludedProductIds: data?.excludedProductIds,
					excludedProductTypes: data?.excludedProductTypes,
					portletNamespace,
					title: data?.dialogTitle,
				}),
				items: [],
				onItemsChange(selectedProducts) {
					if (!selectedProducts || !selectedProducts.length) {
						return;
					}

					Liferay.Util.postForm(
						document[
							`${portletNamespace}addCPDefinitionGroupedEntryFm`
						],
						{
							data: {
								entryCPDefinitionIds: selectedProducts.map(
									(selectedProduct) => selectedProduct.id
								),
							},
						}
					);
				},
			});
		},
	};
}
