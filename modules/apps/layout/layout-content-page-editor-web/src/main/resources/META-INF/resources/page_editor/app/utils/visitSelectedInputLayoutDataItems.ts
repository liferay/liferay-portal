/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../types/State';
import {BaseLayoutDataItem} from '../../types/layout_data/BaseLayoutDataItem';
import {FormLayoutDataItem} from '../../types/layout_data/FormLayoutDataItem';
import {FragmentLayoutDataItem} from '../../types/layout_data/FragmentLayoutDataItem';
import {FRAGMENT_ENTRY_TYPES} from '../config/constants/fragmentEntryTypes';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/freemarkerFragmentEntryProcessor';
import {
	LAYOUT_DATA_ITEM_TYPES,
	LayoutDataItemType,
} from '../config/constants/layoutDataItemTypes';
import selectFragmentEntryLink from '../selectors/selectFragmentEntryLink';

const FIELD_ID_CONFIGURATION_KEY = 'inputFieldId';

export function visitSelectedInputLayoutDataItems(
	state: State,
	formId: FormLayoutDataItem['itemId'],
	fn: (item: FragmentLayoutDataItem, fieldId: string) => void
) {
	const visitSelectedInputItems = (
		itemId: BaseLayoutDataItem<LayoutDataItemType, any>['itemId']
	) => {
		const inputItem = state.layoutData.items[itemId];

		if (inputItem?.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
			const {editableValues, fragmentEntryType} = selectFragmentEntryLink(
				state,
				inputItem
			);

			const fieldId =
				editableValues[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]?.[
					FIELD_ID_CONFIGURATION_KEY
				];

			if (
				fragmentEntryType === FRAGMENT_ENTRY_TYPES.input &&
				typeof fieldId === 'string' &&
				fieldId
			) {
				fn(inputItem, fieldId);
			}
		}

		inputItem?.children.forEach(visitSelectedInputItems);
	};

	visitSelectedInputItems(formId);
}
