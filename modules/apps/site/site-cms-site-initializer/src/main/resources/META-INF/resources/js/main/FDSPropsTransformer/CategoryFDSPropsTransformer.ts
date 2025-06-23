/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import MoveCategoryModalContent from '../categorization/categories/components/MoveCategoryModalContent';
import {openGenericFDSDeleteConfirmationModal} from '../util/GenericOpenModalUtil';

export default function CategoryFDSPropsTransformer({
	...otherProps
}: {
	otherProps: any;
}) {
	return {
		...otherProps,
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: {data: {id: string}};
			itemData: TaxonomyCategory;
			loadData: () => {};
		}) {
			if (action.data.id === 'delete') {
				let confirmationText = Liferay.Language.get(
					'delete-category-confirmation'
				);

				if (itemData.parentTaxonomyCategory?.id) {
					confirmationText = Liferay.Language.get(
						'delete-subcategory-confirmation'
					);
				}

				openGenericFDSDeleteConfirmationModal(
					confirmationText,
					itemData.actions?.delete?.method,
					itemData.actions?.delete?.href,
					itemData.name,
					loadData
				);
			}
			if (action.data.id === 'move') {
				openModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						MoveCategoryModalContent({
							closeModal,
							itemData,
							loadData,
						}),
					size: 'md',
				});
			}
		},
	};
}
