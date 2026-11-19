/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import '../../../css/AppIcon.scss';
import {CategoryItem} from '../types';

type Props = Omit<CategoryItem, 'id'>;

const AppIcon = ({href, iconURL, label, leadingIcon}: Props) => {
	return (
		<a className="app-icon" href={href}>
			<span className="app-icon-tile">
				{iconURL ? (
					<img alt="" src={iconURL} />
				) : (
					<ClayIcon symbol={leadingIcon} />
				)}
			</span>

			<span className="app-icon-label">{label}</span>
		</a>
	);
};

export default AppIcon;
