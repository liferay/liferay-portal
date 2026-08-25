/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React from 'react';

import {SideNavigationItem} from './types/SideNavigation';

function SideNavigationItemContent({item}: {item: SideNavigationItem}) {
	return (
		<>
			{item.leadingIcon && (
				<ClayIcon
					className="c-mr-2"
					key={item.leadingIcon}
					symbol={item.leadingIcon}
				/>
			)}

			{item.label}

			{item.parentLabel && (
				<span className="side-navigation-section-item-context">
					{sub(Liferay.Language.get('in-x'), item.parentLabel)}
				</span>
			)}
		</>
	);
}

export default SideNavigationItemContent;
