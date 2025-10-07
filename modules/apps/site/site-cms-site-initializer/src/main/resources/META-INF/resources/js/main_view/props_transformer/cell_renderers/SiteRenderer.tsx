/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';
import React from 'react';

const SiteRenderer = ({itemData, value}: {itemData: any; value: string}) => {
	return (
		<span className="align-items-center d-flex">
			<ClaySticker
				className="c-mr-2"
				displayType="secondary"
				shape="circle"
				size="lg"
			>
				<ClaySticker.Image alt={value} src={itemData.logo} />
			</ClaySticker>

			{value}
		</span>
	);
};

export default SiteRenderer;
