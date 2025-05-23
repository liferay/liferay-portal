/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import SpaceSticker from '../../components/SpaceSticker';

const SpaceRenderer = ({itemData, value}: {itemData: any; value: string}) => {
	return (
		<span className="align-items-center d-flex space-renderer-sticker">
			<SpaceSticker
				displayType={itemData.settings?.logoColor}
				name={value}
				size="sm"
			/>
		</span>
	);
};

export default SpaceRenderer;
