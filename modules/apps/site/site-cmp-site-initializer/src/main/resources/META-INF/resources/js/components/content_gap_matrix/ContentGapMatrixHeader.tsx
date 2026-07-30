/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {
	AIAssistantTriggerButton,
	ObjectField,
	getObjectFields,
	getSpaces,
} from '@liferay/ai-hub-cell-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {MatrixData, SpaceOption} from './types';
import {computeCoveragePercentage, countCriticalGaps} from './utils';

const CMS_BASIC_WEB_CONTENT_EXTERNAL_REFERENCE_CODE = 'L_CMS_BASIC_WEB_CONTENT';

const CMS_BASIC_WEB_CONTENT_NAME = 'CMSBasicWebContent';

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
	const [objectFields, setObjectFields] = useState<ObjectField[]>();
	const [spaces, setSpaces] = useState<SpaceOption[]>();

	useEffect(() => {
		if (!Liferay.FeatureFlags['LPD-62272']) {
			return;
		}

		const makeFetch = async () => {
			const {items: objectFields} = await getObjectFields(
				CMS_BASIC_WEB_CONTENT_EXTERNAL_REFERENCE_CODE
			);
			const spaces = (await getSpaces()).map((space) => ({
				label: space.name,
				value: String(space.siteId),
			}));

			setObjectFields(objectFields);
			setSpaces(spaces);
		};

		makeFetch().catch(() => {});
	}, []);

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
					getContext={() => ({
						cmpProjectScopeKey,
						focusScope: 'full-matrix',
						objectDefinitionName: CMS_BASIC_WEB_CONTENT_NAME,
						objectFields,
						projectId: cmpProjectObjectEntryId,
						spaceIdsJSONArray: spaces,
					})}
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
