/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import React, {useEffect, useMemo, useState} from 'react';

import VocabularyService from '../../../common/services/VocabularyService';
import {IAssetObjectEntry} from '../../../common/types/AssetType';
import AssetCategories from './AssetCategories';
import {CategorizationInputSize} from './AssetCategorization';
import AssetTags from './AssetTags';

import type {EntryCategorizationDTO} from '../services/ObjectEntryService';

const FUNNEL_STAGE_VOCABULARY_ERC = 'L_CMP_FUNNEL_STAGE';
const PERSONAS_VOCABULARY_ERC = 'L_CMP_PERSONAS';

export default function AssetCategorizationSections({
	assetLibraryId,
	cmsGroupId,
	errorMessage,
	getContent,
	hasUpdatePermission,
	inputSize,
	objectEntry,
	updateObjectEntry,
}: {
	assetLibraryId: number | string;
	cmsGroupId: number | string;
	errorMessage?: string;
	getContent?: (
		objectDefinitionExternalReferenceCode?: string
	) => Promise<string>;
	hasUpdatePermission: boolean;
	inputSize?: CategorizationInputSize;
	objectEntry: IAssetObjectEntry | EntryCategorizationDTO;
	updateObjectEntry: (object: EntryCategorizationDTO) => void | Promise<void>;
}) {
	const [funnelStageVocabularyId, setFunnelStageVocabularyId] = useState<
		number | null
	>(null);
	const [personaVocabularyId, setPersonaVocabularyId] = useState<
		number | null
	>(null);

	const systemVocabularyIds = useMemo(
		() =>
			[personaVocabularyId, funnelStageVocabularyId].filter(
				(id): id is number => id !== null
			),
		[funnelStageVocabularyId, personaVocabularyId]
	);

	useEffect(() => {
		const loadVocabularyIds = async () => {
			const vocabularyIds =
				await VocabularyService.getVocabularyIdsByExternalReferenceCode(
					{
						assetLibraryId,
						externalReferenceCodes: [
							PERSONAS_VOCABULARY_ERC,
							FUNNEL_STAGE_VOCABULARY_ERC,
						],
						siteGroupId: Liferay.ThemeDisplay.getSiteGroupId(),
					}
				);

			setPersonaVocabularyId(
				vocabularyIds[PERSONAS_VOCABULARY_ERC] ?? null
			);
			setFunnelStageVocabularyId(
				vocabularyIds[FUNNEL_STAGE_VOCABULARY_ERC] ?? null
			);
		};

		loadVocabularyIds();
	}, [assetLibraryId]);

	return (
		<ClayPanel.Group flush>
			<AssetCategories
				cmsGroupId={cmsGroupId}
				errorMessage={errorMessage}
				getContent={getContent}
				hasUpdatePermission={hasUpdatePermission}
				inputSize={inputSize}
				objectEntry={objectEntry}
				systemVocabularyIds={systemVocabularyIds}
				updateObjectEntry={updateObjectEntry}
			/>

			<AssetTags
				assetLibraryId={assetLibraryId}
				cmsGroupId={cmsGroupId}
				getContent={getContent}
				hasUpdatePermission={hasUpdatePermission}
				inputSize={inputSize}
				key={objectEntry.keywords?.join(',') || 'tags'}
				objectEntry={objectEntry}
				updateObjectEntry={updateObjectEntry}
			/>

			{personaVocabularyId !== null && (
				<AssetCategories
					cmsGroupId={cmsGroupId}
					hasUpdatePermission={hasUpdatePermission}
					inputSize={inputSize}
					objectEntry={objectEntry}
					placeholder={Liferay.Language.get('add-personas')}
					title={Liferay.Language.get('personas')}
					updateObjectEntry={updateObjectEntry}
					vocabularyId={personaVocabularyId}
				/>
			)}

			{funnelStageVocabularyId !== null && (
				<AssetCategories
					cmsGroupId={cmsGroupId}
					hasUpdatePermission={hasUpdatePermission}
					inputSize={inputSize}
					objectEntry={objectEntry}
					placeholder={Liferay.Language.get('add-stages')}
					title={Liferay.Language.get('funnel-stages')}
					updateObjectEntry={updateObjectEntry}
					vocabularyId={funnelStageVocabularyId}
				/>
			)}
		</ClayPanel.Group>
	);
}
