/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MatrixData, NO_FUNNEL_STAGE, NO_PERSONA, TaxonomyTerm} from '../types';

const FUNNEL_STAGES: TaxonomyTerm[] = [
	{
		description: 'Attract strangers and surface the problem (TOFU).',
		externalReferenceCode: 'STAGE_AWARENESS',
		id: '50001',
		name: 'Awareness',
	},
	{
		description: 'Educate evaluators comparing solutions (MOFU).',
		externalReferenceCode: 'STAGE_CONSIDERATION',
		id: '50002',
		name: 'Consideration',
	},
	{
		description: 'Convert qualified prospects ready to buy (BOFU).',
		externalReferenceCode: 'STAGE_DECISION',
		id: '50003',
		name: 'Decision',
	},
	{
		description:
			'Drive adoption, expansion, and reduce churn for existing customers.',
		externalReferenceCode: 'STAGE_RETENTION',
		id: '50004',
		name: 'Retention',
	},
	NO_FUNNEL_STAGE,
];

const PERSONAS: TaxonomyTerm[] = [
	{
		description:
			'Holds final purchase authority; weighs strategic fit, ROI, and business outcomes (typically C-level or VP).',
		externalReferenceCode: 'PERSONA_DECISION_MAKER',
		id: '40001',
		name: 'Decision Maker',
	},
	{
		description:
			'Internal advocate who drives the evaluation, rallies stakeholders, and pushes the deal forward.',
		externalReferenceCode: 'PERSONA_CHAMPION',
		id: '40002',
		name: 'Champion',
	},
	{
		description:
			'Assesses technical fit: architecture, integrations, security, and scalability.',
		externalReferenceCode: 'PERSONA_TECHNICAL_EVALUATOR',
		id: '40003',
		name: 'Technical Evaluator',
	},
	{
		description:
			'Day-to-day user of the product; cares about usability, productivity, and workflow fit.',
		externalReferenceCode: 'PERSONA_END_USER',
		id: '40004',
		name: 'End User',
	},
	NO_PERSONA,
];

/**
 * No assets at all.
 */
export const EMPTY_MATRIX: MatrixData = {
	cells: [],
	funnelStages: FUNNEL_STAGES,
	personas: PERSONAS,
	totalAssetCount: 0,
};

/**
 * Full coverage. Every real combination has at least one asset.
 */
export const FULL_COVERAGE_MATRIX: MatrixData = {
	cells: [
		{funnelStageId: '50001', personaId: '40001', totalCount: 7},
		{funnelStageId: '50002', personaId: '40001', totalCount: 4},
		{funnelStageId: '50003', personaId: '40001', totalCount: 9},
		{funnelStageId: '50004', personaId: '40001', totalCount: 3},

		{funnelStageId: '50001', personaId: '40002', totalCount: 5},
		{funnelStageId: '50002', personaId: '40002', totalCount: 6},
		{funnelStageId: '50003', personaId: '40002', totalCount: 2},
		{funnelStageId: '50004', personaId: '40002', totalCount: 4},

		{funnelStageId: '50001', personaId: '40003', totalCount: 8},
		{funnelStageId: '50002', personaId: '40003', totalCount: 3},
		{funnelStageId: '50003', personaId: '40003', totalCount: 5},
		{funnelStageId: '50004', personaId: '40003', totalCount: 2},

		{funnelStageId: '50001', personaId: '40004', totalCount: 6},
		{funnelStageId: '50002', personaId: '40004', totalCount: 7},
		{funnelStageId: '50003', personaId: '40004', totalCount: 4},
		{funnelStageId: '50004', personaId: '40004', totalCount: 6},
	],
	funnelStages: FUNNEL_STAGES,
	personas: PERSONAS,
	totalAssetCount: 55,
};

/**
 * Partial coverage with gaps
 */
export const PARTIAL_COVERAGE_MATRIX: MatrixData = {
	cells: [
		{funnelStageId: '50001', personaId: '40001', totalCount: 8},
		{funnelStageId: '50002', personaId: '40001', totalCount: 5},
		{funnelStageId: '50003', personaId: '40001', totalCount: 6},
		{funnelStageId: '50004', personaId: '40001', totalCount: 2},

		{funnelStageId: '50001', personaId: '40002', totalCount: 4},
		{funnelStageId: '50002', personaId: '40002', totalCount: 7},
		{funnelStageId: '50003', personaId: '40002', totalCount: 3},
		{funnelStageId: '50004', personaId: '40002', totalCount: 1},

		{funnelStageId: '50001', personaId: '40003', totalCount: 6},
		{funnelStageId: '50002', personaId: '40003', totalCount: 9},
		{funnelStageId: '50003', personaId: '40003', totalCount: 4},
		{funnelStageId: '50004', personaId: '40003', totalCount: 0},

		{funnelStageId: '50001', personaId: '40004', totalCount: 10},
		{funnelStageId: '50002', personaId: '40004', totalCount: 3},
		{funnelStageId: '50003', personaId: '40004', totalCount: 0},
		{funnelStageId: '50004', personaId: '40004', totalCount: 5},

		{
			funnelStageId: '50001',
			personaId: NO_PERSONA.id,
			totalCount: 2,
		},
		{
			funnelStageId: NO_FUNNEL_STAGE.id,
			personaId: NO_PERSONA.id,
			totalCount: 3,
		},
	],
	funnelStages: FUNNEL_STAGES,
	personas: PERSONAS,
	totalAssetCount: 42,
};

/**
 * Assets exist but none are categorized.
 */
export const UNCATEGORIZED_MATRIX: MatrixData = {
	cells: [
		{
			funnelStageId: NO_FUNNEL_STAGE.id,
			personaId: NO_PERSONA.id,
			totalCount: 18,
		},
	],
	funnelStages: FUNNEL_STAGES,
	personas: PERSONAS,
	totalAssetCount: 18,
};
