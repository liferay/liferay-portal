/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import classNames from 'classnames';
import React from 'react';

import Context, {IContext} from './Context';

export type CardType = 'navigation' | 'template' | null;

interface IProps
	extends Omit<IContext, 'interactive'>,
		React.BaseHTMLAttributes<HTMLAnchorElement | HTMLDivElement> {

	/**
	 * Determines whether `card-type-template` is emitted. `template` keeps that
	 * styling, `navigation` and `null` opt out of it.
	 */
	cardType?: CardType;
}

export function ClayCardNavigation({
	cardType = 'template',
	children,
	className,
	horizontal,
	href,
	onClick,
	...otherProps
}: IProps) {
	const Container = href ? ClayLink : 'div';

	return (
		<Context.Provider value={{horizontal, interactive: true}}>
			<Container
				className={classNames(
					className,
					'card card-interactive card-interactive-primary',
					{
						'card-type-template': cardType === 'template',
						'template-card': !horizontal,
						'template-card-horizontal': horizontal,
					}
				)}
				href={href}
				onClick={onClick}
				role={onClick ? 'button' : undefined}
				{...otherProps}
			>
				{children}
			</Container>
		</Context.Provider>
	);
}
