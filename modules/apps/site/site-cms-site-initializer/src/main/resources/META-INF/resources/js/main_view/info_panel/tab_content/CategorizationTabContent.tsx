/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
} from 'react';

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import AssetCategories from '../components/AssetCategories';
import AssetTags from '../components/AssetTags';
import {AssetTypeInfoPanelContext} from '../context';
import ObjectEntryService from '../services/ObjectEntryService';

const CategorizationTabContent = () => {
	const {actions, asset, assetLibrary, cmsGroupId} = useContext(
		AssetTypeInfoPanelContext
	);

	const [currentAsset, setCurrentAsset] = useState<IAssetObjectEntry | null>(
		null
	);

	const getObjectEntry = useCallback(async () => {
		try {
			const {data, error} = await ObjectEntryService.getObjectEntry(
				actions.get.href
			);

			if (error) {
				throw new Error(error);
			}

			delete (data as Partial<IAssetObjectEntry>).actions;

			setCurrentAsset(data);
		}
		catch (error) {
			console.error(error);

			displayErrorToast(error as string);
		}
	}, [actions]);

	const hasUpdatePermission = useMemo(
		() => !!actions?.update?.href,
		[actions]
	);

	const updateObjectEntry = useCallback(
		async ({keywords, taxonomyCategoryIds}: IAssetObjectEntry) => {
			if (!currentAsset || !hasUpdatePermission) {
				return;
			}

			const {
				update: {href},
			} = actions;

			try {
				const {
					data: {...objectEntry},
					error,
				} = await ObjectEntryService.patchObjectEntry(
					{
						...currentAsset,
						keywords: keywords || currentAsset.keywords,
						taxonomyCategoryIds:
							taxonomyCategoryIds ||
							currentAsset.taxonomyCategoryIds,
					},
					href
				);

				if (error) {
					throw new Error();
				}

				delete (objectEntry as Partial<IAssetObjectEntry>).actions;

				setCurrentAsset(objectEntry as IAssetObjectEntry);
			}
			catch (_error) {
				displayErrorToast();
			}
		},
		[actions, currentAsset, hasUpdatePermission, setCurrentAsset]
	);

	useEffect(() => {
		if (asset) {
			getObjectEntry();
		}
	}, [asset, getObjectEntry]);

	if (!assetLibrary || !cmsGroupId || !actions?.get?.href) {
		return null;
	}

	return !currentAsset ? null : (
		<>
			<AssetCategories
				cmsGroupId={cmsGroupId}
				hasUpdatePermission={hasUpdatePermission}
				objectEntry={currentAsset}
				updateObjectEntry={updateObjectEntry}
			/>

			<AssetTags
				assetLibraryId={assetLibrary.groupId}
				cmsGroupId={cmsGroupId}
				hasUpdatePermission={hasUpdatePermission}
				objectEntry={currentAsset}
				updateObjectEntry={updateObjectEntry}
			/>
		</>
	);
};

export default CategorizationTabContent;
