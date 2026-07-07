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
 * The related-assets data set's vocabulary-scoped category filters (added in
 * LPD-97796). The matrix writes the persona and funnel stage to these as
 * SEPARATE filters so their clauses are AND'd — a cell then matches only the
 * assets tagged with that persona AND that funnel stage. Values within a single
 * filter are OR'd, so the older combined "taxonomyCategoryIds" filter cannot
 * express the AND; the matrix leaves it untouched for manual filtering.
 */
const PERSONA_FILTER_ID = 'cmpPersonaCategoryIds';

const FUNNEL_STAGE_FILTER_ID = 'cmpFunnelStageCategoryIds';

interface CoverageFilter {

	/**
	 * Filters the project's asset data set by a persona and a funnel-stage
	 * category.
	 */
	applyFilter: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;

	/**
	 * Category ids currently selected across the data set's category filters,
	 * used to highlight the matching cell. Empty when the filters are inactive or
	 * set to exclude.
	 */
	selectedCategoryIds: Set<string>;
}

/**
 * Bridges the matrix to the project's asset data set: it writes to the data
 * set's own state atom, resolved by its id, and reads back which categories are
 * filtered so the matrix can highlight the selected cell.
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
						if (filter.id === PERSONA_FILTER_ID) {
							return {
								...filter,
								active: true,
								selectedData: {
									exclude: false,
									selectedItems: [
										{
											label: persona.name,
											value: persona.id,
										},
									],
								},
							};
						}

						if (filter.id === FUNNEL_STAGE_FILTER_ID) {
							return {
								...filter,
								active: true,
								selectedData: {
									exclude: false,
									selectedItems: [
										{
											label: funnelStage.name,
											value: funnelStage.id,
										},
									],
								},
							};
						}

						return filter;
					}
				),
			});
		},
		[assetFDSState, setAssetFDSState]
	);

	const selectedCategoryIds = useMemo(() => {
		const categoryFilters = (assetFDSState?.filters ?? []).filter(
			(filter: IBaseFilterState) =>
				filter.id === PERSONA_FILTER_ID ||
				filter.id === FUNNEL_STAGE_FILTER_ID
		);

		return new Set<string>(
			categoryFilters
				.filter((filter) => filter.active)
				.flatMap((filter) => {
					const selectedData = filter.selectedData as
						| {
								exclude?: boolean;
								selectedItems?: Array<{value: string}>;
						  }
						| undefined;

					if (selectedData?.exclude) {
						return [];
					}

					return (selectedData?.selectedItems ?? []).map((item) =>
						String(item.value)
					);
				})
		);
	}, [assetFDSState]);

	return {applyFilter, selectedCategoryIds};
}
