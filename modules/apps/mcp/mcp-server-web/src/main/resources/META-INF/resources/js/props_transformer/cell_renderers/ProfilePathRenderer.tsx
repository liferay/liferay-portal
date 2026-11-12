/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import React from 'react';

import {Profile} from '../../types/Profile';
import copyProfileURL from '../../utils/copyProfileURL';

export default function ProfilePathRenderer({itemData}: {itemData: Profile}) {
	const profilePath = itemData.profilePath ?? '';

	return (
		<span className="align-items-center d-flex text-nowrap">
			{`/${profilePath}`}

			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('copy-url')}
				borderless
				className="ml-1"
				displayType="secondary"
				onClick={() => copyProfileURL(profilePath)}
				size="xs"
				symbol="copy"
				title={Liferay.Language.get('copy-url')}
			/>
		</span>
	);
}
