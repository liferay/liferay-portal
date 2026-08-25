/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

type State = 'error' | 'complete';

type Context = {
	state?: State;
};

export const ItemContext = React.createContext<Context>({});

export interface IProps extends React.HTMLAttributes<HTMLLIElement> {

	/**
	 * Flag to indicate if `active` classname should be applied
	 */
	active?: boolean;

	/**
	 * Additional className to apply
	 */
	className?: string;

	/**
	 * Flag to indicate if `complete` classname should be applied
	 * @deprecated since v3.91.0 - use `state` instead.
	 */
	complete?: boolean;

	/**
	 * Flag to indicate if step should be disabled
	 */
	disabled?: boolean;

	/**
	 * Flag to indicate if progress line should expand out from step
	 */
	expand?: boolean;

	/**
	 * Defines the status of the step.
	 */
	state?: State;
}

function Item({
	active,
	children,
	className,
	complete,
	disabled,
	expand,
	state,
	...otherProps
}: IProps) {
	return (
		<li
			className={classNames('multi-step-item', className, {
				active,
				complete: complete ?? state === 'complete',
				disabled,
				error: state === 'error',
				['multi-step-item-expand']: expand,
			})}
			{...otherProps}
		>
			<ItemContext.Provider value={{state}}>
				{children}
			</ItemContext.Provider>
		</li>
	);
}

export default Item;
