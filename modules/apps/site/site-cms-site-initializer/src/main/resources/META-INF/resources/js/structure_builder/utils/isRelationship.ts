/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {StructureChild} from '../types/Structure';
import isRepeatableGroup from './isRepeatableGroup';

export default function isRelationship(child: StructureChild): boolean {
	return (
		isRepeatableGroup(child) ||
		child.type === 'referenced-structure' ||
		child.type === 'related-content'
	);
}
