/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {MatrixData, NO_FUNNEL_STAGE, NO_PERSONA, TaxonomyTerm} from '../types';

export interface ContentCoverageService {
	getMatrix(cmpProjectObjectEntryId: string): Promise<MatrixData>;
}

interface ContentCoverageTermResponse {
	description?: string;
	externalReferenceCode: string;
	id: number;
	name: string;
}

interface ContentCoverageEntryResponse {
	assetCount: number;
	funnelStageId: number | null;
	personaId: number | null;
}

interface ContentCoverageResponse {
	assetCount?: number;
	contentCoverageEntries?: ContentCoverageEntryResponse[];
	funnelStages?: ContentCoverageTermResponse[];
	personas?: ContentCoverageTermResponse[];
}

function toTaxonomyTerm(term: ContentCoverageTermResponse): TaxonomyTerm {
	return {
		description: term.description,
		externalReferenceCode: term.externalReferenceCode,
		id: String(term.id),
		name: term.name,
	};
}

/**
 * Adapts the REST response to MatrixData. The endpoint returns the real personas
 * and funnel stages plus cells keyed by numeric category id, using
 * UNCATEGORIZED_ID (-1) for the uncategorized "other" bucket and omitting
 * zero-count cells (the grid defaults a missing cell to 0). This appends the
 * localized sentinel axes (which reuse that id) so those cells land in the "No
 * Persona" row / "No Funnel" column; a missing id defensively falls back to the
 * same sentinel.
 */
export function toMatrixData(response: ContentCoverageResponse): MatrixData {
	return {
		cells: (response.contentCoverageEntries ?? []).map((entry) => ({
			funnelStageId: String(entry.funnelStageId ?? NO_FUNNEL_STAGE.id),
			personaId: String(entry.personaId ?? NO_PERSONA.id),
			totalCount: entry.assetCount,
		})),
		funnelStages: [
			...(response.funnelStages ?? []).map(toTaxonomyTerm),
			NO_FUNNEL_STAGE,
		],
		personas: [
			...(response.personas ?? []).map(toTaxonomyTerm),
			NO_PERSONA,
		],
		totalAssetCount: response.assetCount ?? 0,
	};
}

/**
 * Real implementation for the headless-cmp content-coverage endpoint
 * (LPD-96935).
 */
export const ContentCoverageServiceImpl: ContentCoverageService = {
	async getMatrix(cmpProjectObjectEntryId: string): Promise<MatrixData> {
		const response = await fetch(
			`/o/headless-cmp/v1.0/projects/${cmpProjectObjectEntryId}/content-coverage`
		);

		if (!response.ok) {
			throw new Error(
				`Unable to load content coverage for project ${cmpProjectObjectEntryId}`
			);
		}

		return toMatrixData(await response.json());
	},
};
