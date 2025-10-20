/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';

import {IVocabulary} from '../../common/types/IVocabulary';
import {openGenericFDSDeleteConfirmationModal} from '../../common/utils/genericOpenModalUtil';
import MultipleSpacesRenderer from './cell_renderers/MultipleSpacesRenderer';
import VocabularyRenderer from './cell_renderers/VocabularyRenderer';

export default function VocabularyFDSPropsTransformer({
	itemsActions,
	...otherProps
}: {
	itemsActions: IItemsActions[];
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: VocabularyRenderer,
					name: 'customVocabularyRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: MultipleSpacesRenderer,
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'delete') {
				return {
					...action,
					className: 'text-danger',
				};
			}

			return action;
		}),
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: {data: {id: string}};
			itemData: IVocabulary;
			loadData: any;
		}) {
			if (action.data.id === 'delete') {
				openGenericFDSDeleteConfirmationModal(
					Liferay.Language.get('delete-vocabulary-confirmation'),
					itemData.actions?.delete?.method,
					itemData.actions?.delete?.href,
					itemData.name,
					loadData
				);
			}
		},
	};
}
