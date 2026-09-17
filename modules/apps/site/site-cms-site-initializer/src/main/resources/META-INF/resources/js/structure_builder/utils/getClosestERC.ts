/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import findChild from './findChild';
import isRepeatableGroup from './isRepeatableGroup';

// A group that is not repeatable only exists in the object layout, so the
// closest ancestor that the server knows about is the nearest repeatable group,
// or the structure itself.

export default function getClosestERC({
	structure,
	uuid,
}: {
	structure: Structure;
	uuid: Uuid;
}): string {
	let currentUuid = uuid;

	while (currentUuid && currentUuid !== structure.uuid) {
		const child = findChild({root: structure, uuid: currentUuid});

		if (!child) {
			break;
		}

		if (isRepeatableGroup(child)) {
			return child.erc;
		}

		currentUuid = child.parent;
	}

	return structure.erc;
}
