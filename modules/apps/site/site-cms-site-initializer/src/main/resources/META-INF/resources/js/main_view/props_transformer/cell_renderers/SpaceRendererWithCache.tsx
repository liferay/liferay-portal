/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import LoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import {Space} from '../../../common/types/Space';
import SpaceRenderer from './SpaceRenderer';

const SpaceRendererWithCache = ({
	spaceExternalReferenceCode,
}: {
	spaceExternalReferenceCode: string;
}) => {
	const [loading, setLoading] = useState(true);
	const [space, setSpace] = useState<Space | null>(null);

	useEffect(() => {
		let isMounted = true;

		SpaceService.getSpaceWithCache(spaceExternalReferenceCode)
			.then((space) => {
				if (isMounted) {
					setSpace(space);
				}
			})
			.catch((error) => {
				console.error(
					`Failed to fetch space data for spaceId: ${spaceExternalReferenceCode}`,
					error
				);
			})
			.finally(() => {
				if (isMounted) {
					setLoading(false);
				}
			});

		return () => {
			isMounted = false;
		};
	}, [spaceExternalReferenceCode]);

	if (loading) {
		return (
			<LoadingIndicator
				data-testid="space-renderer-loading"
				displayType="secondary"
				size="sm"
			/>
		);
	}

	if (!space) {
		return null;
	}

	return (
		<SpaceRenderer
			logoColor={space.settings?.logoColor}
			value={space.name}
		/>
	);
};

export default SpaceRendererWithCache;
