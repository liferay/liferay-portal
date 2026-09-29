/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';
import React from 'react';

import {getInitial} from '../utils/getInitial';
import {getRandomColor} from '../utils/getRandomColors';

function AccountSticker({
	className,
	initialColor,
	logoURL,
	name,
	shape,
	size,
}: {
	className?: string;
	initialColor?: string;
	logoURL?: string;
	name: string;
	shape?: 'circle' | 'user-icon';
	size?: 'lg' | 'sm' | 'xl' | 'xxl' | 'xs';
}) {
	return (
		<ClaySticker
			className={className}
			shape={shape}
			size={size}
			style={
				logoURL
					? undefined
					: ((): any => {
							const color = getRandomColor(name);

							return {
								backgroundColor: getRandomColor(name, 0.2),
								border: `1px solid ${color}`,
								color: initialColor ?? color,
							};
						})()
			}
		>
			{logoURL ? (
				<ClaySticker.Image alt={name} src={logoURL} />
			) : (
				getInitial(name)
			)}
		</ClaySticker>
	);
}

export default AccountSticker;
