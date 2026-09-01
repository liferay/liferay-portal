/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {InternalDispatch, useControlledState, useId} from '@clayui/shared';
import React from 'react';

import Content from './Content';
import Item from './Item';
import ItemWithIcon from './ItemWithIcon';
import {List} from './List';
import TabPane from './TabPane';

export type DisplayType = null | 'basic' | 'underline';

export interface IProps extends React.HTMLAttributes<HTMLUListElement> {

	/**
	 * Flag to indicate the navigation behavior in the tab.
	 *
	 * - manual - it will just move the focus and tab activation is done just
	 * by pressing space or enter.
	 * - automatic - moves the focus to the tab and activates the tab.
	 */
	activation?: 'manual' | 'automatic';

	/**
	 * The current tab active (controlled).
	 */
	active?: number;

	/**
	 * Initial active tab when rendering component (uncontrolled).
	 */
	defaultActive?: number;

	/**
	 * Determines how tab is displayed.
	 * @deprecated since v3.89.0 with no replacement.
	 */
	displayType?: null | 'basic' | 'underline';

	/**
	 * Flag to indicate if `fade` classname that applies an fading animation
	 * should be applied.
	 */
	fade?: boolean;

	/**
	 * Justify the nav items according the tab content.
	 */
	justified?: boolean;

	/**
	 * Applies a modern style to the tab.
	 * @deprecated since v3.89.0 with no replacement.
	 */
	modern?: boolean;

	/**
	 * Callback is called when the active tab changes (controlled).
	 */
	onActiveChange?: InternalDispatch<number>;
}

function Tabs({
	activation = 'manual',
	active: externalActive,
	children,
	className,
	defaultActive = 0,
	displayType,
	fade = false,
	justified,
	modern = false,
	onActiveChange,
	...otherProps
}: IProps) {
	const [active, setActive, isUncontrolled] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: externalActive,
	});

	const [left, right] = React.Children.toArray(children);

	const tabsId = useId();

	// @ts-ignore

	if (left?.type?.displayName === 'ClayTabsList') {
		return (
			<>
				{React.cloneElement(left as React.ReactElement, {
					activation,
					active,
					displayType,
					justified,
					modern,
					onActiveChange: setActive,
					shouldUseActive: isUncontrolled,
					tabsId,
				})}

				{React.isValidElement(right) &&
					React.cloneElement(right as React.ReactElement, {
						active,
						fade,
						tabsId,
					})}
			</>
		);
	}

	return (
		<List
			{...otherProps}
			activation={activation}
			active={active}
			className={className}
			displayType={displayType}
			justified={justified}
			modern={modern}
			onActiveChange={setActive}
		>
			{children}
		</List>
	);
}

/**
 * @deprecated since v3.78.2 - Use new composition with Tabs.List and Tabs.Panels.
 */
Tabs.Content = Content;

Tabs.Panels = Content;
Tabs.Item = Item;
Tabs.ItemWithIcon = ItemWithIcon;
Tabs.List = List;
Tabs.TabPane = TabPane;
Tabs.TabPanel = TabPane;

export default Tabs;
