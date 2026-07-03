/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import LoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import SpacesDisplay from '../../../common/components/SpacesDisplay';
import SpaceService from '../../../common/services/SpaceService';
import {Space as Scope} from '../../../common/types/Space';

interface ScopeData {
	externalReferenceCode: string;
	id: number;
	name: string;
}

export interface MultipleScopesRendererProps {
	itemData: {
		assetLibraries: ScopeData[];
		projects?: ScopeData[];
	};
}

const isAllScopes = (scopes: ScopeData[] | undefined) =>
	!scopes?.length || scopes.some(({id}) => id === -1);

const getScopes = (scopes: ScopeData[] | undefined): Promise<Scope[]> => {
	if (!scopes || isAllScopes(scopes)) {
		return Promise.resolve([]);
	}

	return Promise.all(
		scopes.map(async (scope) => {
			try {
				return await SpaceService.getSpaceWithCache(
					scope.externalReferenceCode,
					scope.name
				);
			}
			catch (error) {
				return {
					externalReferenceCode: scope.externalReferenceCode,
					id: scope.id,
					name: scope.name,
					settings: {},
				} as Scope;
			}
		})
	);
};

export default function MultipleScopesRenderer({
	itemData,
}: MultipleScopesRendererProps) {
	const {assetLibraries, projects} = itemData;

	const renderProjects =
		Array.isArray(projects) &&
		!!Liferay.FeatureFlags['LPD-58677'] &&
		!!Liferay.FeatureFlags['LPD-86291'];

	const [projectScopes, setProjectScopes] = useState<Scope[]>([]);
	const [spaceScopes, setSpaceScopes] = useState<Scope[]>([]);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		let isMounted = true;

		const fetchAndSetScopes = async () => {
			setLoading(
				(renderProjects && !isAllScopes(projects)) ||
					!isAllScopes(assetLibraries)
			);

			const [fetchedProjects, fetchedSpaces] = await Promise.all([
				renderProjects ? getScopes(projects) : Promise.resolve([]),
				getScopes(assetLibraries),
			]);

			if (isMounted) {
				setProjectScopes(fetchedProjects);
				setSpaceScopes(fetchedSpaces);
				setLoading(false);
			}
		};

		fetchAndSetScopes();

		return () => {
			isMounted = false;
		};
	}, [assetLibraries, projects, renderProjects]);

	if (loading) {
		return (
			<LoadingIndicator
				data-testid="space-renderer-loading"
				displayType="secondary"
				size="sm"
			/>
		);
	}

	if (!renderProjects) {
		return (
			<SpacesDisplay
				allScopesLabel={Liferay.Language.get('all-spaces')}
				availableInScopeLabel={Liferay.Language.get(
					'available-in-spaces-x'
				)}
				spaces={spaceScopes}
			/>
		);
	}

	return (
		<span className="align-items-center c-gap-3 d-flex flex-wrap">
			<SpacesDisplay
				allScopesLabel={Liferay.Language.get('all-spaces')}
				availableInScopeLabel={Liferay.Language.get(
					'available-in-spaces-x'
				)}
				spaces={spaceScopes}
			/>

			<SpacesDisplay
				allScopesLabel={Liferay.Language.get('all-projects')}
				availableInScopeLabel={Liferay.Language.get(
					'available-in-projects-x'
				)}
				spaces={projectScopes}
			/>
		</span>
	);
}
