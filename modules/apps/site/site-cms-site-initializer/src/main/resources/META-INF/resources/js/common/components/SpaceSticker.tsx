/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import cx from 'classnames';
import React from 'react';

import {LogoColor} from '../types/Space';

export const logoColors: Record<LogoColor, string> = {
	'outline-0': Liferay.Language.get('gray'),
	'outline-1': Liferay.Language.get('purple'),
	'outline-2': Liferay.Language.get('yellow'),
	'outline-3': Liferay.Language.get('green'),
	'outline-4': Liferay.Language.get('red'),
	'outline-5': Liferay.Language.get('orange'),
	'outline-6': Liferay.Language.get('teal'),
	'outline-7': Liferay.Language.get('blue'),
	'outline-8': Liferay.Language.get('pink'),
	'outline-9': Liferay.Language.get('white'),
};

function getDisplayType(char: string): LogoColor {
	const displayTypes = Object.keys(logoColors);

	return displayTypes[char.charCodeAt(0) % displayTypes.length] as LogoColor;
}

export default function SpaceSticker({
	displayType,
	hideName,
	href,
	name,
	size,
	...otherProps
}: {
	hideName?: boolean;
	href?: string;
	name: string;
} & Pick<
	React.ComponentProps<typeof ClaySticker>,
	'className' | 'displayType' | 'id' | 'size'
>) {
	const gap = size === 'lg' ? 'c-gap-3' : 'c-gap-2';
	const wrapperClasses = cx('align-items-center d-flex', gap);

	return (
		<div className={wrapperClasses}>
			<ClaySticker
				displayType={displayType || getDisplayType(name)}
				size={size}
				{...otherProps}
			>
				{name.charAt(0).toUpperCase()}
			</ClaySticker>

			{!hideName &&
				(href ? (
					<ClayLink href={href}>{name}</ClayLink>
				) : (
					<span>{name}</span>
				))}
		</div>
	);
}
