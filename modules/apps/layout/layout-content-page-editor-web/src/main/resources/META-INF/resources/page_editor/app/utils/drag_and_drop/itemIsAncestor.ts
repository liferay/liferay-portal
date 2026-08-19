/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	LayoutData,
	LayoutDataItem,
} from '../../../types/layout_data/LayoutData';

/**
 * Checks if the given parent is indeed parent of given child.
 */

export default function itemIsAncestor(
	parent: LayoutDataItem | undefined,
	child: LayoutDataItem | undefined,
	layoutDataRef: React.MutableRefObject<LayoutData>
): boolean {
	if (child && parent) {
		return child.itemId !== parent.itemId
			? itemIsAncestor(
					parent,
					layoutDataRef.current.items[child.parentId],
					layoutDataRef
				)
			: true;
	}

	return false;
}
