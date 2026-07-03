/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface TaxonomyTerm {
	description?: string;
	externalReferenceCode: string | null;
	id: string;
	name: string;
	uncategorized?: boolean;
}

export interface MatrixCell {
	funnelStageId: string;
	personaId: string;
	totalCount: number;
}

export interface MatrixData {
	cells: MatrixCell[];
	funnelStages: TaxonomyTerm[];
	personas: TaxonomyTerm[];
	totalAssetCount: number;
}

/**
 * Id the content-coverage endpoint uses for the filters aggregation's "other"
 * bucket — the assets with no persona or no funnel stage. The sentinel axes
 * reuse it so those cells land in the "No Persona" row / "No Funnel" column.
 */
export const UNCATEGORIZED_ID = '-1';

export const NO_FUNNEL_STAGE: TaxonomyTerm = {
	externalReferenceCode: null,
	id: UNCATEGORIZED_ID,
	name: Liferay.Language.get('no-funnel'),
	uncategorized: true,
};

export const NO_PERSONA: TaxonomyTerm = {
	externalReferenceCode: null,
	id: UNCATEGORIZED_ID,
	name: Liferay.Language.get('no-persona'),
	uncategorized: true,
};
