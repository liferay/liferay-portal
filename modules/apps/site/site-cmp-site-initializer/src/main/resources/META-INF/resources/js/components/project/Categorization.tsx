/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import React, {useEffect, useState} from 'react';

import VocabularyMultiSelect, {Category} from '../VocabularyMultiSelect';

import './Categorization.scss';

import {AssetTags, IAssetObjectEntry} from '@liferay/site-cms-site-initializer';

interface CategorizationProps {
	cmsGroupId: number;
	funnelStagesVocabularyERC: string;
	hasUpdatePermission: boolean;
	objectEntryKeywords: string[];
	personasVocabularyERC: string;
	selectedFunnelStageCategories: Category[];
	selectedPersonaCategories: Category[];
}

export default function Categorization({
	cmsGroupId,
	funnelStagesVocabularyERC,
	hasUpdatePermission,
	objectEntryKeywords,
	personasVocabularyERC,
	selectedFunnelStageCategories,
	selectedPersonaCategories,
}: CategorizationProps) {
	const [formId, setFormId] = useState<string | undefined>();
	const [funnelStageCategories, setFunnelStageCategories] = useState<
		Category[]
	>(selectedFunnelStageCategories);
	const [keywords, setKeywords] = useState<string[]>(objectEntryKeywords);
	const [personaCategories, setPersonaCategories] = useState<Category[]>(
		selectedPersonaCategories
	);

	const allIds = [...personaCategories, ...funnelStageCategories].map(
		(category) => category.id
	);

	useEffect(() => {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		if (form) {
			setFormId(form.id);
		}
	}, []);

	return (
		<ClayPanel
			className="lfr-cmp__categorization"
			collapsable
			defaultExpanded
			displayTitle={Liferay.Language.get('categorization')}
			displayType="default"
			showCollapseIcon
		>
			<div className="lfr-cmp__categorization-grid">
				<VocabularyMultiSelect
					disabled={!hasUpdatePermission}
					label={Liferay.Language.get('personas')}
					onChange={setPersonaCategories}
					placeholder={Liferay.Language.get('add-personas')}
					value={personaCategories}
					vocabularyERC={personasVocabularyERC}
				/>

				<VocabularyMultiSelect
					disabled={!hasUpdatePermission}
					label={Liferay.Language.get('funnel-stages')}
					onChange={setFunnelStageCategories}
					placeholder={Liferay.Language.get('add-stages')}
					value={funnelStageCategories}
					vocabularyERC={funnelStagesVocabularyERC}
				/>
			</div>

			{allIds.map((id) => (
				<input
					form={formId}
					key={id}
					name="assetCategoryIds"
					type="hidden"
					value={id}
				/>
			))}

			<div className="lfr-cmp__tags-container">
				<AssetTags
					cmsGroupId={cmsGroupId}
					collapsable={false}
					hasUpdatePermission={hasUpdatePermission}
					inputSize="regular"
					key={keywords.join(',') || 'tags'}
					objectEntry={
						{
							keywords,
						} as IAssetObjectEntry
					}
					updateObjectEntry={async ({
						keywords: updatedKeywords,
					}: Partial<IAssetObjectEntry>): Promise<void> => {
						setKeywords(
							updatedKeywords ? [...updatedKeywords] : []
						);
					}}
				/>

				<input
					form={formId}
					name="assetTagNames"
					type="hidden"
					value={keywords.join(',')}
				/>
			</div>
		</ClayPanel>
	);
}
