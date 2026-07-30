/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {AIAssistantTriggerButton} from '@liferay/ai-hub-cell-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {MatrixData} from './types';
import {useAIInsightsChatContext} from './useAIInsightsChatContext';
import {computeCoveragePercentage, countCriticalGaps} from './utils';

export default function ContentGapMatrixHeader({
	cmpProjectObjectEntryId,
	cmpProjectObjectEntryTitle,
	cmpProjectScopeKey,
	data,
}: {
	cmpProjectObjectEntryId?: string;
	cmpProjectObjectEntryTitle?: string;
	cmpProjectScopeKey?: string;
	data?: MatrixData;
}) {
	const getAIInsightsChatContext = useAIInsightsChatContext({
		cmpProjectObjectEntryId,
		cmpProjectScopeKey,
	});

	const coveragePercentage = data ? computeCoveragePercentage(data) : 0;
	const coverageDisplayType =
		coveragePercentage === 0
			? 'warning'
			: coveragePercentage === 100
				? 'success'
				: 'secondary';
	const criticalGaps = data ? countCriticalGaps(data) : 0;
	const noAssets = data ? data.totalAssetCount === 0 : false;

	return (
		<div className="lfr-cmp__content-gap-matrix-header">
			<div>
				<h5 className="lfr-cmp__content-gap-matrix-header-title text-uppercase">
					<ClayIcon symbol="diagram" />

					{Liferay.Language.get('content-coverage-matrix')}
				</h5>

				{data ? (
					<div className="lfr-cmp__content-gap-matrix-header-stats">
						<ClayLabel displayType={coverageDisplayType} inverse>
							{sub(
								Liferay.Language.get('x-covered'),
								`${coveragePercentage}%`
							)}
						</ClayLabel>

						{noAssets ? (
							<ClayLabel displayType="danger" inverse>
								{Liferay.Language.get('no-assets-found')}
							</ClayLabel>
						) : criticalGaps > 0 ? (
							<ClayLabel displayType="warning" inverse>
								{sub(
									Liferay.Language.get('x-critical-gaps'),
									String(criticalGaps)
								)}
							</ClayLabel>
						) : null}
					</div>
				) : null}
			</div>

			{Liferay.FeatureFlags['LPD-62272'] && (
				<AIAssistantTriggerButton
					getContext={getAIInsightsChatContext}
					initialMessage={sub(
						Liferay.Language.get(
							'get-ai-insights-for-the-x-content-coverage-matrix'
						),
						cmpProjectObjectEntryTitle
					)}
					instructionDefinitionScope="cms"
					label={Liferay.Language.get('get-ai-insights')}
				/>
			)}
		</div>
	);
}
