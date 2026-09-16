/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import findChild from './findChild';

export default function isRenamable({
	structure,
	uuid,
}: {
	structure: Structure;
	uuid: Uuid;
}): boolean {
	if (uuid === structure.uuid) {
		return true;
	}

	const child = findChild({root: structure, uuid})!;

	if (child.type === 'referenced-structure') {
		return false;
	}
	else if (child.type === 'related-content') {
		return false;
	}
	else if (child.type === 'group') {
		return true;
	}
	else if (child.locked) {
		return false;
	}

	return true;
}
