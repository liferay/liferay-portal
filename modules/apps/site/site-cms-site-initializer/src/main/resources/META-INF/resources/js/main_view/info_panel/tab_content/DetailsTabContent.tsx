/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import React, {useContext} from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import AssetMetadata from '../components/AssetMetadata';
import {AssetTypeInfoPanelContext} from '../context';
import {ASSET_TYPE} from '../util/constants';

const DetailsTabContent = () => {
	const {objectEntries = [], type} = useContext(AssetTypeInfoPanelContext);

	const [{embedded: objectEntry}]: ISearchAssetObjectEntry[] = objectEntries;

	return (
		<>
			{type === ASSET_TYPE.FILES && objectEntry.file?.thumbnailURL && (
				<ClayCard>
					<img
						alt="thumbnail"
						className="card-image"
						src={objectEntry.file?.thumbnailURL}
					/>
				</ClayCard>
			)}
			<AssetMetadata />
		</>
	);
};

export default DetailsTabContent;
