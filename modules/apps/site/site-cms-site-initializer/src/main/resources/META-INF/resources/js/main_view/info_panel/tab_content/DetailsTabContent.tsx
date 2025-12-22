/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import React, {useContext} from 'react';

import AssetMetadata from '../components/AssetMetadata';
import {AssetTypeInfoPanelContext} from '../context';
import {ASSET_TYPE} from '../util/constants';

const DetailsTabContent = () => {
	const {asset, type} = useContext(AssetTypeInfoPanelContext);

	return (
		<>
			{type === ASSET_TYPE.FILES && asset?.file?.thumbnailURL && (
				<ClayCard>
					<img
						alt="thumbnail"
						className="card-image w-100"
						src={asset.file.thumbnailURL}
					/>
				</ClayCard>
			)}

			<AssetMetadata />
		</>
	);
};

export default DetailsTabContent;
