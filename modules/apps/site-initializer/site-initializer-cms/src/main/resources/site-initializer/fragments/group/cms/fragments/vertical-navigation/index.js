/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

import {VerticalNav} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import React from 'react';
import {createRoot} from 'react-dom/client';

const props = JSON.parse(
	fragmentElement.querySelector('.vertical-navigation-props').innerHTML
);

const root = createRoot(
	fragmentElement.querySelector('.vertical-navigation-fragment')
);

root.render(
	React.createElement(
		VerticalNav,
		{
			active: props.active,
			['aria-label']: configuration.ariaLabel,
			defaultExpandedKeys: new Set(props.expandedKeys),
			displayType: 'primary',
			items: props.navItems,
			spritemap: Liferay.Icons.spritemap,
		},
		(navItem) =>
			React.createElement(
				VerticalNav.Item,
				{
					href: navItem.href,
					items: navItem.items,
					key: navItem.id,
					textValue: navItem.label,
				},
				React.createElement(
					'div',
					{
						className: 'autofit-row',
					},
					navItem.icon &&
						React.createElement(
							'div',
							{
								className: 'autofit-col',
							},
							React.createElement(ClayIcon, {
								spritemap: Liferay.Icons.spritemap,
								symbol: navItem.icon,
							})
						),
					React.createElement(
						'div',
						{
							className: 'autofit-col autofit-col-expand',
						},
						navItem.label
					)
				)
			)
	)
);
