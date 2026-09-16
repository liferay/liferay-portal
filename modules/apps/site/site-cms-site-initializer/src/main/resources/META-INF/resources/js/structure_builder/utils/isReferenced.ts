/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, ReferencedStructure, Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';

export default function isReferenced({
	hasReferencedParent = false,
	root,
	uuid,
}: {
	hasReferencedParent?: boolean;
	root: ReferencedStructure | Group | Structure;
	uuid: Uuid;
}): boolean {
	for (const child of root.children.values()) {
		if (child.uuid === uuid) {
			return hasReferencedParent;
		}

		if (child.type === 'referenced-structure') {
			const referenced = isReferenced({
				hasReferencedParent: true,
				root: child,
				uuid,
			});

			if (referenced) {
				return true;
			}
		}
	}

	return false;
}
