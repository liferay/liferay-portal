/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-nameentifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useMemo, useState} from 'react';

import TagService from '../../../common/services/TagService';
import {IAssetObjectEntry} from '../../../common/types/AssetType';
import {EntryCategorizationDTO} from '../services/ObjectEntryService';
import {CategorizationInputSize} from './AssetCategorization';

type TKeyword = {
	name: string;
};

const AssetTags = ({
	assetLibraryId,
	cmsGroupId,
	collapsable = true,
	hasUpdatePermission,
	inputSize,
	objectEntry,
	updateObjectEntry,
}: {
	assetLibraryId?: number | string | null | undefined;
	cmsGroupId: number | string;
	collapsable?: boolean;
	hasUpdatePermission?: boolean;
	inputSize?: CategorizationInputSize;
	objectEntry: IAssetObjectEntry | EntryCategorizationDTO;
	updateObjectEntry: (object: EntryCategorizationDTO) => void | Promise<void>;
}) => {
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

		if (scopeId >= 0) {
			return `${baseURL}/${scopeId}/keywords`;
		}

		return `${baseURL}/${cmsGroupId}/keywords?filter=groupIds in ('${scopeId}')`;
	}, [cmsGroupId, scopeId]);

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

			const newKeywords = keywords.filter((value) => value !== keyword);

			if (newKeywords.length < keywords.length) {
				await updateObjectEntry({
					keywords: newKeywords,
					keywordsToRemove: [keyword],
				} as EntryCategorizationDTO);
			}
		},
		[objectEntry, updateObjectEntry]
	);

	return (
		<ClayPanel
			collapsable={collapsable}
			defaultExpanded={true}
			displayTitle={
				<ClayPanel.Title className="panel-title text-secondary">
					{Liferay.Language.get('tags')}
				</ClayPanel.Title>
			}
			displayType="unstyled"
			showCollapseIcon={collapsable}
		>
			<ClayPanel.Body>
				<ItemSelector<TKeyword>
					apiURL={apiURL}
					disabled={!hasUpdatePermission}
					locator={{
						id: 'id',
						label: 'name',
						value: 'externalReferenceCode',
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
						!!value.length &&
						!(objectEntry?.keywords || []).includes(value) && {
							label: sub(
								Liferay.Language.get('create-new-tag-x'),
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
