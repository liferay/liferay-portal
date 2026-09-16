/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, ReferencedStructure, Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import findChild from './findChild';
import isField from './isField';

export default function isLocked({
	root,
	uuid,
}: {
	root: ReferencedStructure | Group | Structure;
	uuid: Uuid;
}): boolean {
	const item = findChild({root, uuid});

	return !!item && isField(item) && !!item.locked;
}
