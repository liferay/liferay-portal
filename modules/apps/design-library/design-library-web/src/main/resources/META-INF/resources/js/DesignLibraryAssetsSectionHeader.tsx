/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React from 'react';

import getCreationMenuItems from './getCreationMenuItems';
import {DesignLibraryResourceType} from './types';

export default function DesignLibraryAssetsSectionHeader({
	resourceTypes = [],
}: {
	resourceTypes?: DesignLibraryResourceType[];
}) {
	const creationItems = getCreationMenuItems(resourceTypes);

	return (
		<div className="align-items-center d-flex justify-content-between mb-3">
			<h2 className="font-weight-semi-bold m-0 text-4">
				{Liferay.Language.get('design-assets')}
			</h2>

			{!!creationItems.length && (
				<ClayDropDownWithItems
					items={creationItems}
					trigger={
						<ClayButton displayType="secondary" size="sm">
							<ClayIcon
								className="inline-item inline-item-before"
								symbol="plus"
							/>

							{Liferay.Language.get('add-asset')}
						</ClayButton>
					}
				/>
			)}
		</div>
	);
}
