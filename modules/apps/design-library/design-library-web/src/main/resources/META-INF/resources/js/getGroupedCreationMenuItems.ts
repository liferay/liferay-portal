/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayDropDownWithItems} from '@clayui/drop-down';
import {ComponentProps} from 'react';

import getCreationMenuItems from './getCreationMenuItems';
import {DesignLibraryResourceType} from './types';

type CreationMenuItems = ComponentProps<typeof ClayDropDownWithItems>['items'];

export default function getGroupedCreationMenuItems(
	resourceTypes: DesignLibraryResourceType[]
): CreationMenuItems {
	return resourceTypes.flatMap<CreationMenuItems[number]>((resourceType) => {
		const creationMenuItems = getCreationMenuItems([resourceType]);

		if (creationMenuItems.length > 1) {
			return [
				{
					items: creationMenuItems,
					label: resourceType.creationItemsGroupLabel,
					type: 'contextual',
				},
			];
		}

		return creationMenuItems;
	});
}
