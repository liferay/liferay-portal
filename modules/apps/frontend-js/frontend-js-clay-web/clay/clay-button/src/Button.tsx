/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import LoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import React from 'react';
import warning from 'warning';

import Group from './Group';

type DisplayType =
	| null
	| 'ai'
	| 'primary'
	| 'secondary'
	| 'link'
	| 'success'
	| 'warning'
	| 'danger'
	| 'info'
	| 'beta'
	| 'beta-dark'
	| 'unstyled';

export interface IProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {

	/**
	 * Flag to indicate if button is used within an alert component.
	 */
	alert?: boolean;

	/**
	 * Renders the button as a block element.
	 */
	block?: boolean;

	/**
	 * Flag to indicate if the button should be borderless.
	 */
	borderless?: boolean;

	/**
	 * Flag to indicate if the button should use the clay-dark variant.
	 */
	dark?: boolean;

	/**
	 * Determines the button variant to use.
	 * The value `ai` renders the AI variant, meant for actions that trigger or
	 * involve AI-assisted workflows.
	 * The values `beta` and `beta-dark` are deprecated since v3.100.0 - use
	 * `translucent` and `dark` instead.
	 * The values `null` and `unstyled` are for internal use only.
	 */
	displayType?: DisplayType;

	/**
	 * Flag to display a loading indicator.
	 */
	loading?: boolean;

	/**
	 * Flag to indicate if button should be monospaced.
	 */
	monospaced?: boolean;

	/**
	 * Flag to indicate if the button should use the outline variant.
	 */
	outline?: boolean;

	/**
	 * Flag to indicate if the button should be shaped like a pill.
	 */
	rounded?: boolean;

	/**
	 * Determines the size of a button.
	 */
	size?: 'xs' | 'regular' | 'sm';

	/**
	 * Indicates button should be a small variant.
	 * @deprecated since v3.72.0 - use `size` instead.
	 */
	small?: boolean;

	/**
	 * Flag to indicate if the button should use the translucent variant.
	 */
	translucent?: boolean;
}

const ButtonComponent = React.forwardRef<HTMLButtonElement, IProps>(
	(
		{
			alert,
			block,
			borderless,
			children,
			className,
			dark,
			displayType = 'primary',
			loading,
			monospaced,
			outline,
			rounded,
			size = 'regular',
			small,
			translucent,
			type = 'button',
			...otherProps
		},
		ref
	) => {
		const childArray = React.Children.toArray(children);

		warning(
			!(
				childArray.length === 1 &&

				// @ts-ignore

				childArray[0].type?.displayName === 'ClayIcon' &&
				typeof otherProps['aria-label'] !== 'string' &&
				typeof otherProps['aria-labelledby'] !== 'string'
			),
			'Button Accessibility: Component has only the Icon declared. Define an `aria-label` or `aria-labelledby` attribute that labels the interactive button that screen readers can read. The `title` attribute is optional but consult your design team.'
		);

		if (displayType === 'beta') {
			displayType = 'info';
			translucent = true;
		}
		else if (displayType === 'beta-dark') {
			dark = true;
			displayType = 'info';
			translucent = true;
		}

		return (
			<button
				className={classNames(className, 'btn', {
					'alert-btn': alert,
					'btn-block': block,
					'btn-monospaced': monospaced,
					'btn-outline-borderless': borderless,
					'btn-sm': small && (!size || size === 'regular'),
					'btn-translucent': translucent,
					'clay-dark': dark,
					[`btn-${displayType}`]:
						displayType && !outline && !borderless,
					[`btn-outline-${displayType}`]:
						displayType && (outline || borderless),
					'rounded-pill': rounded,
					[`btn-${size}`]: size && size !== 'regular',
				})}
				ref={ref}
				type={type}
				{...otherProps}
			>
				{loading && (
					<span className="inline-item inline-item-before">
						<LoadingIndicator />
					</span>
				)}

				{children}
			</button>
		);
	}
);

ButtonComponent.displayName = 'ClayButton';

const Button = Object.assign(ButtonComponent, {
	Group,
});

export default Button;
