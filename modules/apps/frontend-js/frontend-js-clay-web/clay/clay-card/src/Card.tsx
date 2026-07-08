/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import AspectRatio from './AspectRatio';
import Body from './Body';
import Caption from './Caption';
import {ClayCardHorizontal} from './CardHorizontal';
import {ClayCardNavigation} from './CardNavigation';
import Context, {IContext} from './Context';
import Description from './Description';
import Group from './Group';
import Row from './Row';

interface ICardProps extends IContext {

	/**
	 * Flag that indicates if `active` class is applied
	 */
	active?: boolean;

	/**
	 * Flag that indicates if `disabled` class is applied
	 */
	disabled?: boolean;

	/**
	 * Determines the style of the card
	 */
	displayType?: 'file' | 'image' | 'user';

	/**
	 * Flag that indicates if the card can be selectable.
	 */
	selectable?: boolean;
}

interface IProps
	extends ICardProps,
		React.BaseHTMLAttributes<
			HTMLAnchorElement | HTMLSpanElement | HTMLDivElement
		> {}

function CardBase({
	active,
	children,
	className,
	disabled = false,
	displayType,
	selectable = false,
	...otherProps
}: IProps) {
	const isCardType = {
		file: displayType === 'file',
		image: displayType === 'image',
		user: displayType === 'user',
	};

	return (
		<Context.Provider value={{horizontal: false, interactive: false}}>
			<div
				aria-disabled={disabled || undefined}
				className={classNames(
					className,
					{
						active,
						'card': !selectable,
						disabled,
						'file-card': isCardType.file,
						'form-check-card form-check form-check-top-left':
							selectable,
						'image-card': isCardType.image,
						'user-card': isCardType.user,
					},
					'card-type-asset'
				)}
				{...otherProps}
			>
				{selectable ? <div className="card">{children}</div> : children}
			</div>
		</Context.Provider>
	);
}

function Card({children, horizontal, interactive, ...otherProps}: IProps) {
	const Container = interactive
		? ClayCardNavigation
		: horizontal
			? ClayCardHorizontal
			: CardBase;

	return (
		<Container horizontal={horizontal} {...otherProps}>
			{children}
		</Container>
	);
}

Card.displayName = 'ClayCard';

Card.AspectRatio = AspectRatio;
Card.Body = Body;
Card.Caption = Caption;
Card.Description = Description;
Card.Group = Group;
Card.Row = Row;

export default Card;
