/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../types/Structure';
import {Field} from './field';
import getOwnChildren from './getOwnChildren';
import isField from './isField';

export default function getOwnFields(children: Group['children']): Field[] {
	return getOwnChildren(children).filter(isField);
}
