/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LinkOrButton} from '@clayui/shared';
import classNames from 'classnames';
import React from 'react';

export interface IProps
	extends Omit<React.HTMLAttributes<HTMLLIElement>, 'onClick'> {

	/**
	 * Flag to indicate if the component is active or not.
	 *
	 * OBS: The `active` API in the new pattern has uncontrolled behavior,
	 * working just like `defaultActive` as in the prop declared in the
	 * root component.
	 */
	active?: boolean;

	/**
	 * Flag to indicate if the TabPane is disabled.
	 */
	disabled?: boolean;

	/**
	 * This value is used to be the target of the link.
	 */
	href?: string;

	/**
	 * Props to be added to the item element that can be an anchor or a button.
	 */
	innerProps?: React.HTMLAttributes<HTMLAnchorElement | HTMLButtonElement>;

	/**
	 * Callback to be used when clicking to a Tab Item.
	 */
	onClick?: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
}

const Item = React.forwardRef<any, IProps>(
	(
		{
			active = false,
			children,
			className,
			disabled = false,
			href,
			innerProps = {},
			onClick,
			...otherProps
		}: IProps,
		ref
	) => (
		<li
			{...otherProps}
			className={classNames('nav-item', className)}
			role="none"
		>
			<LinkOrButton
				{...innerProps}
				aria-selected={active}
				buttonDisplayType="unstyled"
				buttonType="button"
				className={classNames(
					'nav-link',
					{
						active,
						disabled,
					},
					innerProps.className
				)}
				data-testid="tabItem"
				disabled={disabled}
				href={href}
				onClick={onClick}
				ref={ref}
				role="tab"
				tabIndex={!active ? -1 : 0}
			>
				{children}
			</LinkOrButton>
		</li>
	)
);

Item.displayName = 'ClayTabsItem';

export default Item;
