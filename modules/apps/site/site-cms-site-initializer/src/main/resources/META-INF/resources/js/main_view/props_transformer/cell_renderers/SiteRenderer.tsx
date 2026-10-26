/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';
import React from 'react';

import {
	DESIGN_LIBRARY_TYPE,
	SITE_TEMPLATE_TYPE,
} from '../../../common/utils/constants';

const SiteRenderer = ({itemData, value}: {itemData: any; value: string}) => {
	let label = value;

	if (itemData.type === DESIGN_LIBRARY_TYPE) {
		label = `${value} (${Liferay.Language.get('design-library')})`;
	}
	else if (itemData.type === SITE_TEMPLATE_TYPE) {
		label = `${value} (${Liferay.Language.get('site-template')})`;
	}
	else if (itemData.stagingType === 'STAGING') {
		label = `${value} (${Liferay.Language.get('staging')})`;
	}

	return (
		<span className="align-items-center d-flex">
			<ClaySticker
				className="c-mr-2"
				displayType="secondary"
				shape="circle"
				size="lg"
			>
				<ClaySticker.Image alt={label} src={itemData.logo} />
			</ClaySticker>

			{label}
		</span>
	);
};

export default SiteRenderer;
