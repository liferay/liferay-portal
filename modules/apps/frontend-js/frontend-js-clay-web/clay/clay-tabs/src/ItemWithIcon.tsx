/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import React from 'react';

import Item from './Item';

export interface IProps
	extends Omit<React.ComponentProps<typeof Item>, 'children'> {

	/**
	 * The name of the tab. It is not painted beside the icon: it names the
	 * item for assistive technologies and is shown on hover.
	 */
	label: string;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * The id of the icon in the spritemap.
	 */
	symbol: string;
}

const ItemWithIcon = React.forwardRef<any, IProps>(
	(
		{innerProps = {}, label, spritemap, symbol, ...otherProps}: IProps,
		ref
	) => (
		<Item
			{...otherProps}
			innerProps={{title: label, ...innerProps}}
			ref={ref}
		>
			<Icon
				className="mt-0 text-6"
				spritemap={spritemap}
				style={{padding: '2px'}}
				symbol={symbol}
			/>

			<span className="sr-only">{label}</span>
		</Item>
	)
);

ItemWithIcon.displayName = 'ClayTabsItemWithIcon';

export default ItemWithIcon;
