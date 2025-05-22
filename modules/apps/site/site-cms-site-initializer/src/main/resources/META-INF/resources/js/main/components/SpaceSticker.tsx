/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';
import React from 'react';

export type LogoColor = React.ComponentProps<typeof ClaySticker>['displayType'];

export const logoColors: LogoColor[] = [
	'outline-0',
	'outline-1',
	'outline-2',
	'outline-3',
	'outline-4',
	'outline-5',
	'outline-6',
	'outline-7',
	'outline-8',
	'outline-9',
];

function getDisplayType(char: string): LogoColor {
	return logoColors[char.charCodeAt(0) % logoColors.length];
}

export default function SpaceSticker({
	displayType,
	hiddenName,
	name,
	size,
	...otherProps
}: {
	hiddenName?: boolean;
	name: string;
} & Pick<
	React.ComponentProps<typeof ClaySticker>,
	'className' | 'displayType' | 'id' | 'size'
>) {
	return (
		<>
			<ClaySticker
				displayType={displayType || getDisplayType(name)}
				size={size}
				{...otherProps}
			>
				{name.charAt(0).toUpperCase()}
			</ClaySticker>

			{!hiddenName && <span className="ml-2">{name}</span>}
		</>
	);
}
