/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LAYOUT_DATA_ITEM_TYPES} from '../app/config/constants/layoutDataItemTypes';
import {FormLayoutDataItem} from '../types/layout_data/FormLayoutDataItem';

export default function getMockFormItem({
	config = {},
	itemId = 'form-id',
}: {
	config?: Partial<FormLayoutDataItem['config']>;
	itemId?: string;
} = {}): FormLayoutDataItem {
	return {
		children: [],
		config: {
			classNameId: '0',
			classTypeId: '0',
			formConfig: 0,
			formType: 'simple',
			landscapeMobile: {},
			numberOfSteps: 1,
			portraitMobile: {},
			tablet: {},
			...config,
		},
		itemId,
		parentId: '',
		type: LAYOUT_DATA_ITEM_TYPES.form,
	};
}
