/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBaseFilterState,
	IFDSState,
	getOrCreateFDSAtom,
} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {useCallback, useMemo} from 'react';

import {TaxonomyTerm} from './types';

/**
 * Id of the asset data set's category filter
 *  which spans the persona and funnel-stage
 * vocabularies.
 */
const ASSET_CATEGORY_FILTER_ID = 'taxonomyCategoryIds';

interface CoverageFilter {

	/**
	 * Filters the project's asset data set by a persona and a funnel-stage
	 * category.
	 */
	applyFilter: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;
}

/**
 * Bridges the matrix to the project's asset data set: it writes to the data
 * set's own state atom, resolved by its id, to filter it by the clicked cell's
 * persona and funnel-stage categories.
 */
export function useCoverageFilter(assetFDSId: string): CoverageFilter {
	const assetFDSAtom = useMemo(
		() => getOrCreateFDSAtom({fdsName: assetFDSId}),
		[assetFDSId]
	);

	const [assetFDSState, setAssetFDSState] =
		useLiferayState<IFDSState>(assetFDSAtom);

	const applyFilter = useCallback(
		(persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => {
			setAssetFDSState({
				...assetFDSState,
				filters: (assetFDSState?.filters ?? []).map(
					(filter: IBaseFilterState) => {
						if (filter.id !== ASSET_CATEGORY_FILTER_ID) {
							return filter;
						}

						return {
							...filter,
							active: true,
							selectedData: {
								exclude: false,
								selectedItems: [
									{label: persona.name, value: persona.id},
									{
										label: funnelStage.name,
										value: funnelStage.id,
									},
								],
							},
						};
					}
				),
			});
		},
		[assetFDSState, setAssetFDSState]
	);

	return {applyFilter};
}
