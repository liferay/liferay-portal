/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IconSelector} from '@clayui/core';
import {ClayIconSpriteContext} from '@clayui/icon';
import React, {useState} from 'react';

export default function NavigationMenuIconSelector({
	portletNamespace,
	selectedIcon,
	spritemap,
}) {
	const [icon, setIcon] = useState(selectedIcon);

	return (
		<div className="mb-3">
			<input
				name={
					portletNamespace + 'TypeSettingsProperties--displayIcon--'
				}
				type="hidden"
				value={icon}
			/>

			<ClayIconSpriteContext.Provider value={spritemap}>
				<IconSelector
					onIconChange={setIcon}
					selectedIcon={icon}
					spritemap={spritemap}
				/>
			</ClayIconSpriteContext.Provider>
		</div>
	);
}
