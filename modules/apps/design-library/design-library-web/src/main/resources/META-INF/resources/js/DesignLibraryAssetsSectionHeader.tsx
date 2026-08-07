/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {loadModule} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {
	DesignLibraryCreationItem,
	DesignLibraryCreationItemsFactory,
	DesignLibraryResourceType,
} from './types';

export default function DesignLibraryAssetsSectionHeader({
	resourceTypes = [],
}: {
	resourceTypes?: DesignLibraryResourceType[];
}) {
	const [creationItems, setCreationItems] = useState<
		DesignLibraryCreationItem[]
	>([]);

	useEffect(() => {
		let cancelled = false;

		Promise.all(
			resourceTypes
				.filter((resourceType) => resourceType.creationItemsModule)
				.map((resourceType) =>
					loadModule(resourceType.creationItemsModule as string)
						.then(
							(
								getCreationItems: DesignLibraryCreationItemsFactory
							) =>
								getCreationItems(
									resourceType.creationItemsProps || {}
								)
						)
						.catch((error: Error) => {
							console.error(
								`Unable to load creation items for ${resourceType.entryClassName}`,
								error
							);

							return [] as DesignLibraryCreationItem[];
						})
				)
		).then((items) => {
			if (!cancelled) {
				setCreationItems(items.flat());
			}
		});

		return () => {
			cancelled = true;
		};
	}, [resourceTypes]);

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
