/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../types/Structure';

export default function isReferenced({
	hasReferencedParent = false,
	item,
	root,
}: {
	hasReferencedParent?: boolean;
	item: StructureChild;
	root: ReferencedStructure | RepeatableGroup | Structure;
}): boolean {
	for (const child of root.children.values()) {
		if (child.uuid === item.uuid) {
			return hasReferencedParent;
		}

		if (child.type === 'referenced-structure') {
			const referenced = isReferenced({
				hasReferencedParent: true,
				item,
				root: child,
			});

			if (referenced) {
				return true;
			}
		}
	}

	return false;
}
