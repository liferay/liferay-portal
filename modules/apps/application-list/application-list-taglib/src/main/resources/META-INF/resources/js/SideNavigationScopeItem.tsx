/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

import {SideNavigationScope} from './types/SideNavigation';

interface Props {
	id: string;
	label: string;
	scope: SideNavigationScope;
}

function SideNavigationScopeItem({id, label, scope}: Props) {
	return (
		<li
			className={`side-navigation-scope-item side-navigation-scope-item-${scope}`}
			data-qa-id="sideNavigationScopeItem"
			role="none"
		>
			<ClayLabel
				className="text-uppercase"
				displayType="unstyled"
				id={id}
			>
				{label}
			</ClayLabel>
		</li>
	);
}

export default SideNavigationScopeItem;
