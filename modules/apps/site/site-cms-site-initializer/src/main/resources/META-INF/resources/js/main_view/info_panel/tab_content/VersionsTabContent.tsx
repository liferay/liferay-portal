/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Button} from '@clayui/core';
import List from '@clayui/list';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import AssetVersionsListItem from '../components/AssetVersionsListItem';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';
import VersionService from '../services/VersionService';

const MAX_LIST_SIZE = 10;

const VersionsTabContent = () => {
	const {actions, asset}: IAssetTypeInfoPanelContext = useContext(
		AssetTypeInfoPanelContext
	);

	const [assetVersions, setAssetVersions] = useState<{
		count: number;
		items: IAssetObjectEntry[];
	}>({count: 0, items: []});

	const getAssetVersions = useCallback(async () => {
		setAssetVersions({count: 0, items: []});

		const href: string = actions?.versions?.href || '';

		try {
			const {data, error} = await VersionService.getObjectEntryVersions(
				href,
				{page: 1, pageSize: MAX_LIST_SIZE, sort: 'version:desc'}
			);

			if (error) {
				throw new Error(error);
			}

			if (data) {
				setAssetVersions({
					count: data.totalCount,
					items: data.items,
				});
			}
		}
		catch {
			displayErrorToast();
		}
	}, [actions]);

	useEffect(() => {
		getAssetVersions();
	}, [getAssetVersions]);

	return (
		<>
			{assetVersions.count > 0 && (
				<List>
					<AssetVersionsListItem
						{...assetVersions}
						file={asset?.file}
						getAssetVersions={getAssetVersions}
					/>
				</List>
			)}

			{assetVersions.count > MAX_LIST_SIZE && (
				<div className="d-flex justify-content-center">
					<Button displayType="secondary">
						{Liferay.Language.get('view-all')}
					</Button>
				</div>
			)}
		</>
	);
};

export default VersionsTabContent;
