/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import LoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import SpacesDisplay from '../../../common/components/SpacesDisplay';
import SpaceService from '../../../common/services/SpaceService';
import {Space} from '../../../common/types/Space';

export interface MultipleSpacesRendererProps {
	itemData: {
		assetLibraries: {
			externalReferenceCode: string;
			id: number;
			name: string;
		}[];
	};
}

export default function MultipleSpacesRenderer({
	itemData,
}: MultipleSpacesRendererProps) {
	const {assetLibraries} = itemData;
	const [spaces, setSpaces] = useState<Space[]>([]);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		let isMounted = true;
		const isAllSpaces =
			!assetLibraries.length || assetLibraries.some(({id}) => id === -1);

		const fetchAndSetSpaces = async () => {
			if (isAllSpaces) {
				return;
			}

			setLoading(true);

			const spacePromises = assetLibraries.map(async (assetLib) => {
				try {
					return await SpaceService.getSpaceWithCache(
						assetLib.externalReferenceCode
					);
				}
				catch (error) {
					return {
						externalReferenceCode: assetLib.externalReferenceCode,
						id: assetLib.id,
						name: assetLib.name,
						settings: {},
					} as Space;
				}
			});

			const fetchedOrFallbackSpaces = await Promise.all(spacePromises);

			if (isMounted) {
				setSpaces(fetchedOrFallbackSpaces);
				setLoading(false);
			}
		};

		fetchAndSetSpaces();

		return () => {
			isMounted = false;
		};
	}, [assetLibraries]);

	if (loading) {
		return (
			<LoadingIndicator
				data-testid="space-renderer-loading"
				displayType="secondary"
				size="sm"
			/>
		);
	}

	return <SpacesDisplay spaces={spaces} />;
}
