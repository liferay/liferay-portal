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

import {MatrixData, TaxonomyTerm, UNCATEGORIZED_ID} from './types';
import {isSentinel} from './utils';

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

type SelectedData = {
	exclude: boolean;
	selectedItems: Array<{label?: string; value: string}>;
};

interface CoverageSelection {

	/**
	 * The filtered funnel stage's category id, UNCATEGORIZED_ID when the filter
	 * excludes every funnel stage, or null when the data set is not filtered to a
	 * single cell.
	 */
	funnelStageId: string | null;

	personaId: string | null;
}

interface CoverageFilter {

	/**
	 * Filters the project's asset data set by a persona and a funnel-stage
	 * category.
	 */
	applyFilter: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;

	/**
	 * The persona and funnel stage the data set is currently filtered by, used to
	 * highlight the matching cell.
	 */
	selection: CoverageSelection;
}

function getRealTerms(terms: TaxonomyTerm[]): TaxonomyTerm[] {
	return terms.filter((term) => !isSentinel(term));
}

/**
 * The data set's selection for one axis of the matrix. A real term filters the
 * assets down to that category; an uncategorized sentinel instead EXCLUDES every
 * real term of its axis, which the data set turns into "has none of these
 * categories" — the same query the content-coverage endpoint aggregates the
 * uncategorized bucket with. Null when the axis holds no real term, in which
 * case its filter is deactivated rather than left active with nothing to match.
 */
function getSelectedData(
	term: TaxonomyTerm,
	terms: TaxonomyTerm[]
): SelectedData | null {
	if (!isSentinel(term)) {
		return {
			exclude: false,
			selectedItems: [{label: term.name, value: term.id}],
		};
	}

	const realTerms = getRealTerms(terms);

	if (!realTerms.length) {
		return null;
	}

	return {
		exclude: true,
		selectedItems: realTerms.map((realTerm) => ({
			label: realTerm.name,
			value: realTerm.id,
		})),
	};
}

/**
 * The term id one axis of the matrix is filtered by, mirroring what
 * getSelectedData writes. Anything else the user set up by hand in the data set's
 * own filter menu — several terms at once, a partial exclusion — matches no
 * single cell and reads as null.
 */
function getSelectedTermId(
	filters: readonly IBaseFilterState[],
	filterId: string,
	terms: TaxonomyTerm[]
): string | null {
	const filter = filters.find(
		(currentFilter) => currentFilter.id === filterId
	);

	if (!filter?.active) {
		return null;
	}

	const selectedData = filter.selectedData as SelectedData | undefined;

	const selectedItems = selectedData?.selectedItems ?? [];

	if (selectedData?.exclude) {
		const realTerms = getRealTerms(terms);

		if (
			realTerms.length &&
			realTerms.length === selectedItems.length &&
			realTerms.every((realTerm) =>
				selectedItems.some(
					(selectedItem) => String(selectedItem.value) === realTerm.id
				)
			)
		) {
			return UNCATEGORIZED_ID;
		}

		return null;
	}

	if (selectedItems.length !== 1) {
		return null;
	}

	return String(selectedItems[0].value);
}

/**
 * Bridges the matrix to the project's asset data set: it writes to the data
 * set's own state atom, resolved by its id, and reads back which categories are
 * filtered so the matrix can highlight the selected cell.
 */
export function useCoverageFilter(
	assetFDSId: string,
	data: MatrixData
): CoverageFilter {
	const assetFDSAtom = useMemo(
		() => getOrCreateFDSAtom({fdsName: assetFDSId}),
		[assetFDSId]
	);

	const [assetFDSState, setAssetFDSState] =
		useLiferayState<IFDSState>(assetFDSAtom);

	const applyFilter = useCallback(
		(persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => {
			const selectedDataMap = new Map([
				[
					FUNNEL_STAGE_FILTER_ID,
					getSelectedData(funnelStage, data.funnelStages),
				],
				[PERSONA_FILTER_ID, getSelectedData(persona, data.personas)],
			]);

			setAssetFDSState({
				...assetFDSState,
				filters: (assetFDSState?.filters ?? []).map(
					(filter: IBaseFilterState) => {
						if (!selectedDataMap.has(filter.id)) {
							return filter;
						}

						const selectedData = selectedDataMap.get(filter.id);

						if (!selectedData) {
							return {
								...filter,
								active: false,
								odataFilterString: undefined,
								selectedData: undefined,
							};
						}

						return {...filter, active: true, selectedData};
					}
				),
			});
		},
		[assetFDSState, data, setAssetFDSState]
	);

	const selection = useMemo(() => {
		const filters = assetFDSState?.filters ?? [];

		return {
			funnelStageId: getSelectedTermId(
				filters,
				FUNNEL_STAGE_FILTER_ID,
				data.funnelStages
			),
			personaId: getSelectedTermId(
				filters,
				PERSONA_FILTER_ID,
				data.personas
			),
		};
	}, [assetFDSState, data]);

	return {applyFilter, selection};
}
