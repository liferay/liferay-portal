/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import openCreationModal from './openCreationModal';
import {DesignLibraryResourceType} from './types';

type CreationMenuItem = {label: string; onClick: () => void};

export default function getCreationMenuItems(
	resourceTypes: DesignLibraryResourceType[]
): CreationMenuItem[] {
	return resourceTypes.flatMap(
		(resourceType) =>
			resourceType.creationItems?.map(
				(designLibraryResourceCreationItem) => ({
					label: designLibraryResourceCreationItem.label,
					onClick: () =>
						openCreationModal(designLibraryResourceCreationItem),
				})
			) ?? []
	);
}
