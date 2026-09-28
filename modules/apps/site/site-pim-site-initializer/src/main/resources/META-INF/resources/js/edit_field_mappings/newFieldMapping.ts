/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFieldMapping, TYPE_DYNAMIC_VALUE} from './types';

let counter = 0;

export default function newFieldMapping(): IFieldMapping {
	return {
		key: `new-${++counter}`,
		sourceClassName: '',
		sourceFieldName: '',
		type: TYPE_DYNAMIC_VALUE,
		value: '',
	};
}
