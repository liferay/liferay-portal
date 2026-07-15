/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

import ClayButton from './Button';

interface IProps
	extends Omit<React.ComponentProps<typeof ClayButton>, 'displayType'> {

	/**
	 * Flag to render the "calling" gradient fill instead of the default solid
	 * fill. Only meaningful for the filled variant.
	 */
	gradient?: boolean;

	/**
	 * The text of the button, defaults to `Chat with AI`. When `monospaced` is
	 * set the button renders icon-only and this becomes the `aria-label`.
	 */
	label?: string;

	/**
	 * Flag to render the button as a borderless link (underline on hover).
	 * Implies `borderless`.
	 */
	link?: boolean;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * The id of the AI icon in the spritemap.
	 */
	symbol?: string;
}

export type Props = IProps;

const ClayAIButton = React.forwardRef<HTMLButtonElement, IProps>(
	(
		{
			'aria-label': ariaLabel,
			borderless,
			children,
			className,
			gradient,
			label = 'Chat with AI',
			link,
			monospaced,
			rounded = true,
			spritemap,
			symbol = 'stars',
			...otherProps
		}: IProps,
		ref
	) => (
		<ClayButton
			{...otherProps}
			aria-label={
				monospaced
					? ariaLabel ??
						(typeof children === 'string' ? children : label)
					: ariaLabel
			}
			borderless={borderless || link}
			className={classNames(className, {
				'btn-ai-gradient': gradient,
				'btn-ai-link': link,
			})}
			displayType="ai"
			monospaced={monospaced}
			ref={ref}
			rounded={rounded}
		>
			{!monospaced && (children ?? label)}

			<ClayIcon
				className={monospaced ? undefined : 'ml-2'}
				spritemap={spritemap}
				symbol={symbol}
			/>
		</ClayButton>
	)
);

ClayAIButton.displayName = 'ClayAIButton';

export default ClayAIButton;
