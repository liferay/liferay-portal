/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React from 'react';

import ContentGapCell from './ContentGapCell';
import {MatrixData, TaxonomyTerm} from './types';
import {useAIInsightsChatContext} from './useAIInsightsChatContext';
import {useAssetFDSFilter} from './useAssetFDSFilter';
import {buildCountsByCellKey, getCellKey, getMaxRealCount} from './utils';

export default function ContentGapMatrixGrid({
	assetFDSId,
	cmpProjectObjectEntryId,
	cmpProjectScopeKey,
	data,
}: {
	assetFDSId: string;
	cmpProjectObjectEntryId?: string;
	cmpProjectScopeKey?: string;
	data: MatrixData;
}) {
	const {funnelStages, personas} = data;

	const countsByCellKey = buildCountsByCellKey(data.cells);
	const maxRealCount = getMaxRealCount(data);

	const {applyFilter, filteredFunnelStageId, filteredPersonaId} =
		useAssetFDSFilter(assetFDSId, data);

	const getCount = (personaId: string, funnelStageId: string) =>
		countsByCellKey.get(getCellKey(personaId, funnelStageId)) ?? 0;

	const getAIInsightsChatContext = useAIInsightsChatContext({
		cmpProjectObjectEntryId,
		cmpProjectScopeKey,
	});

	const handleGenerate = (
		persona: TaxonomyTerm,
		funnelStage: TaxonomyTerm
	) => {
		Liferay.fire('openAIAssistantChat', {
			context: {
				...getAIInsightsChatContext(),
				gaps: [
					{
						funnelStage: funnelStage.name,
						funnelStageId: funnelStage.id,
						persona: persona.name,
						personaId: persona.id,
					},
				],
			},
			message: sub(
				Liferay.Language.get(
					'generate-content-for-the-x-persona-and-the-x-funnel-stage'
				),
				persona.name,
				funnelStage.name
			),
		});
	};

	return (
		<ClayTooltipProvider>
			<div
				aria-colcount={funnelStages.length + 1}
				aria-rowcount={personas.length + 1}
				className="lfr-cmp__content-gap-matrix-grid"
				role="grid"
				style={{
					gridTemplateColumns: `minmax(8rem, auto) repeat(${funnelStages.length}, minmax(0, 1fr))`,
				}}
			>
				<div className="lfr-cmp__content-gap-matrix-row" role="row">
					<div
						className="lfr-cmp__content-gap-matrix-corner"
						role="columnheader"
					/>

					{funnelStages.map((funnelStage) => (
						<div
							className="lfr-cmp__content-gap-matrix-column-header"
							key={funnelStage.id}
							role="columnheader"
							title={funnelStage.description}
						>
							{funnelStage.name}
						</div>
					))}
				</div>

				{personas.map((persona) => (
					<div
						className="lfr-cmp__content-gap-matrix-row"
						key={persona.id}
						role="row"
					>
						<div
							className="lfr-cmp__content-gap-matrix-row-header"
							role="rowheader"
							title={persona.description}
						>
							{persona.name}
						</div>

						{funnelStages.map((funnelStage) => (
							<ContentGapCell
								funnelStage={funnelStage}
								key={funnelStage.id}
								maxRealCount={maxRealCount}
								onFilter={applyFilter}
								onGenerate={handleGenerate}
								persona={persona}
								selected={
									filteredPersonaId === persona.id &&
									filteredFunnelStageId === funnelStage.id
								}
								totalCount={getCount(
									persona.id,
									funnelStage.id
								)}
							/>
						))}
					</div>
				))}
			</div>
		</ClayTooltipProvider>
	);
}
