/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {AIAssistantTriggerButton} from '@liferay/ai-hub-cell-js-components-web';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import TagService from '../../../common/services/TagService';
import {IAssetObjectEntry} from '../../../common/types/AssetType';
import {AI_ASSISTANT_TOOLBAR_TRIGGER_ID} from '../../../common/utils/constants';
import {EntryCategorizationDTO} from '../services/ObjectEntryService';
import {CategorizationInputSize} from './AssetCategorization';
import {
	CATEGORIZE_EVENT,
	GENERATE_TAGS_AGENT,
} from './categorizationAgentEvents';

type TKeyword = {
	name: string;
};

const AssetTags = ({
	assetLibraryId,
	cmsGroupId,
	collapsable = true,
	getContent,
	hasUpdatePermission,
	inputSize,
	objectEntry,
	updateObjectEntry,
}: {
	assetLibraryId?: number | string | null | undefined;
	cmsGroupId: number | string;
	collapsable?: boolean;
	getContent?: (
		objectDefinitionExternalReferenceCode?: string
	) => Promise<string>;
	hasUpdatePermission?: boolean;
	inputSize?: CategorizationInputSize;
	objectEntry: IAssetObjectEntry | EntryCategorizationDTO;
	updateObjectEntry: (object: EntryCategorizationDTO) => void | Promise<void>;
}) => {
	const [canCreate, setCanCreate] = useState(false);
	const [value, setValue] = useState('');

	const scopeId = useMemo(
		() =>
			(objectEntry as IAssetObjectEntry).scopeId ||
			assetLibraryId ||
			cmsGroupId,
		[assetLibraryId, cmsGroupId, objectEntry]
	);

	const apiURL = useMemo(() => {
		const baseURL = `${Liferay.ThemeDisplay.getPortalURL()}/o/headless-admin-taxonomy/v1.0/sites`;

		if (scopeId > 0) {
			return `${baseURL}/${scopeId}/keywords`;
		}

		return `${baseURL}/${cmsGroupId}/keywords?filter=groupIds in ('${scopeId}')`;
	}, [cmsGroupId, scopeId]);

	useEffect(() => {
		const checkPermission = async () => {
			const {data} = await ApiHelper.get<{
				actions: {create: {href: string}};
			}>(apiURL);

			setCanCreate(!!data?.actions?.create);
		};

		checkPermission();
	}, [apiURL]);

	const selectedKeywords = useMemo(
		() => (objectEntry?.keywords || []).map((name) => ({id: name, name})),
		[objectEntry?.keywords]
	);

	const addKeyword = useCallback(
		async (keyword: TKeyword) => {
			const {keywords = []} = objectEntry;

			if (keywords.includes(keyword.name)) {
				return;
			}

			const updated = [...keywords, keyword.name];

			await updateObjectEntry({
				keywords: updated,
				keywordsToAdd: updated,
			} as EntryCategorizationDTO);
		},
		[objectEntry, updateObjectEntry]
	);

	const createAndAddKeyword = useCallback(async () => {
		const {data, error} = await TagService.createTag({
			assetLibraryId: scopeId,
			cmsGroupId,
			name: value,
		});

		if (data) {
			await addKeyword(data);

			setValue('');
		}
		else if (error) {
			console.error('Failed to create new keyword.', error);
		}
	}, [addKeyword, cmsGroupId, scopeId, value]);

	const removeKeyword = useCallback(
		async (keyword: string) => {
			const {keywords = []} = objectEntry;

			const index = keywords.indexOf(keyword);

			const keywordsToRemove = [];

			keywordsToRemove.push(keywords[index]);

			keywords.splice(index, 1);

			await updateObjectEntry({
				keywords,
				keywordsToAdd: keywords,
				keywordsToRemove,
			} as EntryCategorizationDTO);
		},
		[objectEntry, updateObjectEntry]
	);

	const handleGenerateTags = useCallback(async () => {
		Liferay.fire(CATEGORIZE_EVENT, {
			agent: GENERATE_TAGS_AGENT,
			cmsGroupId,
			content:
				(await getContent?.(
					(objectEntry as IAssetObjectEntry).systemProperties
						?.objectDefinitionBrief?.externalReferenceCode
				)) ||
				(objectEntry as IAssetObjectEntry).contentRawText ||
				'',
			scopeId,
		});
	}, [cmsGroupId, getContent, objectEntry, scopeId]);

	return (
		<ClayPanel
			collapsable={collapsable}
			collapseHeaderClassNames="text-secondary"
			defaultExpanded={true}
			displayTitle={Liferay.Language.get('tags')}
			displayType="unstyled"
			showCollapseIcon={collapsable}
		>
			<ClayPanel.Body>
				<div className="align-items-end d-flex">
					<div className="flex-grow-1">
						<ItemSelector<TKeyword>
							apiURL={apiURL}
							disabled={!hasUpdatePermission}
							items={selectedKeywords}
							locator={{
								id: 'id',
								label: 'name',
								value: 'name',
							}}
							onChange={setValue}
							onItemsChange={(newItems: TKeyword[]) => {
								if (newItems[0]) {
									addKeyword(newItems[0]);

									// The reason for this timeout is because of react's
									// batch rendering. Clay internals set the value of
									// the input, but we need to wait for the next 'tick' to set the value.

									setTimeout(() => setValue(''));
								}
							}}
							placeholder={Liferay.Language.get('add-tag')}
							primaryAction={
								canCreate &&
								!!value.length &&
								!(objectEntry?.keywords || []).includes(
									value
								) && {
									label: sub(
										Liferay.Language.get(
											'create-new-tag-x'
										),
										value
									),
									onClick: createAndAddKeyword,
								}
							}
							refetchOnActive
							sizing={inputSize}
							value={value}
						>
							{(item) => (
								<ItemSelector.Item
									key={item.name}
									textValue={item.name}
								>
									{item.name}
								</ItemSelector.Item>
							)}
						</ItemSelector>
					</div>

					{Liferay.FeatureFlags?.['LPD-62272'] &&
					hasUpdatePermission &&
					(getContent ||
						(objectEntry as IAssetObjectEntry).contentRawText) ? (
						<AIAssistantTriggerButton
							anchorId={AI_ASSISTANT_TOOLBAR_TRIGGER_ID}
							className="ai-assistant-chat__trigger--categorization ml-2"
							hideLabel
							instructionDefinitionScope="cms"
							label={Liferay.Language.get(
								'generate-tags-with-ai'
							)}
							onOpen={handleGenerateTags}
							presentation="dropdown"
						/>
					) : null}
				</div>

				<div className="asset-tags mt-3">
					{objectEntry?.keywords?.map((keyword, index) => {
						return (
							<Label
								className="mr-2 mt-2"
								closeButtonProps={{
									'aria-label': Liferay.Language.get('close'),
									'disabled': !hasUpdatePermission,
									'onClick': async (event) => {
										event.preventDefault();

										await removeKeyword(keyword);
									},
									'title': Liferay.Language.get('close'),
								}}
								displayType="secondary"
								inverse
								key={`${keyword}_${index}`}
								style={{textTransform: 'none'}}
							>
								{keyword}
							</Label>
						);
					})}
				</div>
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export default AssetTags;
