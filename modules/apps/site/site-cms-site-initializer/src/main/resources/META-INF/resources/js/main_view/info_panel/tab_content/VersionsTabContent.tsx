/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Button} from '@clayui/core';
import List from '@clayui/list';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import AssetVersionsListItem from '../components/AssetVersionsListItem';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';

const MAX_LIST_SIZE = 10;

const VersionsTabContent = () => {
	const {objectEntries = []}: IAssetTypeInfoPanelContext = useContext(
		AssetTypeInfoPanelContext
	);

	const [objectEntryVersions, setObjectEntryVersions] = useState<{
		count: number;
		items: IAssetObjectEntry[];
	}>({count: 0, items: []});

	const getObjectEntriesVersions = useCallback(async () => {
		const [
			{
				actions: {
					versions: {href = ''},
				},
			},
		] = objectEntries;

		try {
			const response = await fetch(
				`${href}?page=1&pageSize=${MAX_LIST_SIZE}`
			);

			if (response.ok) {
				const {items, totalCount} = await response.json();

				setObjectEntryVersions({count: totalCount, items});
			}
		}
		catch {
			Liferay.Util.openToast({
				message: Liferay.Language.get(
					'an-unexpected-system-error-occurred'
				),
				type: 'danger',
			});
		}
	}, [objectEntries, setObjectEntryVersions]);

	useEffect(() => {
		getObjectEntriesVersions();
	}, [getObjectEntriesVersions]);

	return (
		<>
			{objectEntryVersions.count > 0 && (
				<List>
					<AssetVersionsListItem {...objectEntryVersions} />
				</List>
			)}

			{objectEntryVersions.count > MAX_LIST_SIZE && (
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
