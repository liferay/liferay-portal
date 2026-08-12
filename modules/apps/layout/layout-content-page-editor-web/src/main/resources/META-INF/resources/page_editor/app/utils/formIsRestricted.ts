/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormLayoutDataItem} from '../../types/layout_data/FormLayoutDataItem';
import {config} from '../config/index';
import {formIsMapped} from './formIsMapped';

export function formIsRestricted(item: FormLayoutDataItem) {
	const {classNameId, classTypeId} = item.config;
	const {formTypes} = config;

	if (classNameId === '0' || !formIsMapped(item)) {
		return false;
	}

	const type = formTypes.find((formType) => formType.value === classNameId);

	if (!type) {
		return false;
	}

	const subtype = type.subtypes?.find(
		(formSubtype) => formSubtype.value === classTypeId
	);

	return Boolean(type.isRestricted || subtype?.isRestricted);
}
