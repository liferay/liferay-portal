/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MatrixCell, MatrixData, TaxonomyTerm} from './types';

export const CELL_TIER_COUNT = 4;

/**
 * Indexes the cells by getCellKey so the grid can read a cell's count in
 * constant time instead of scanning the list for every persona/funnel-stage
 * pair.
 */
export function buildCountsByCellKey(cells: MatrixCell[]): Map<string, number> {
	const lookup = new Map<string, number>();

	cells.forEach((cell) =>
		lookup.set(
			getCellKey(cell.personaId, cell.funnelStageId),
			cell.totalCount
		)
	);

	return lookup;
}

/**
 * Percentage of the real persona/funnel-stage combinations that hold at least
 * one asset. The uncategorized sentinels are excluded, so this reflects real
 * coverage only.
 */
export function computeCoveragePercentage(data: MatrixData): number {
	const realCombinations = countRealCombinations(data);

	if (realCombinations === 0) {
		return 0;
	}

	return Math.round((countFilledRealCells(data) / realCombinations) * 100);
}

/**
 * Real persona/funnel-stage combinations with no assets. The uncategorized
 * sentinel row and column are excluded: an empty sentinel cell is not a
 * coverage gap.
 */
export function countCriticalGaps(data: MatrixData): number {
	return countRealCombinations(data) - countFilledRealCells(data);
}

function countFilledRealCells(data: MatrixData): number {
	return getRealCells(data).filter((cell) => cell.totalCount > 0).length;
}

function countRealCombinations(data: MatrixData): number {
	const personaCount = data.personas.filter(
		(term) => !isSentinel(term)
	).length;
	const stageCount = data.funnelStages.filter(
		(term) => !isSentinel(term)
	).length;

	return personaCount * stageCount;
}

/**
 * Composite key for a persona/funnel-stage cell.
 */
export function getCellKey(personaId: string, funnelStageId: string): string {
	return `${personaId}:${funnelStageId}`;
}

/**
 * Maps the count to a fill tier relative to the busiest real
 * cell, so the scale auto-fits any project
 */
export function getCellTier(count: number, maxRealCount: number): number {
	if (count <= 0 || maxRealCount <= 0) {
		return 0;
	}

	const intensity = Math.min(1, count / maxRealCount);

	return Math.ceil(intensity * CELL_TIER_COUNT);
}

/**
 * Highest asset count among the real cells, which sets the top
 * of the color scale. Sentinels are excluded so a large uncategorized bucket
 * cannot wash out the intensity of the real grid.
 */
export function getMaxRealCount(data: MatrixData): number {
	return getRealCells(data).reduce(
		(max, cell) => Math.max(max, cell.totalCount),
		0
	);
}

/**
 * The cells for real persona/funnel-stage combinations, excluding the
 * uncategorized "No Persona" / "No Funnel" sentinels.
 */
function getRealCells(data: MatrixData): MatrixCell[] {
	const sentinelPersonaIds = getSentinelIds(data.personas);
	const sentinelStageIds = getSentinelIds(data.funnelStages);

	return data.cells.filter((cell) =>
		isRealCell(cell, sentinelPersonaIds, sentinelStageIds)
	);
}

function getSentinelIds(terms: TaxonomyTerm[]): Set<string> {
	return new Set(terms.filter(isSentinel).map((term) => term.id));
}

function isRealCell(
	cell: MatrixCell,
	sentinelPersonaIds: Set<string>,
	sentinelStageIds: Set<string>
): boolean {
	return (
		!sentinelPersonaIds.has(cell.personaId) &&
		!sentinelStageIds.has(cell.funnelStageId)
	);
}

export function isSentinel(term: TaxonomyTerm): boolean {
	return Boolean(term.uncategorized);
}
