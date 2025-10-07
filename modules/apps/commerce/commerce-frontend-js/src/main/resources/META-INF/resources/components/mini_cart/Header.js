/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React, {useContext} from 'react';

import MiniCartContext from './MiniCartContext';
import {ORDER_IS_EMPTY, YOUR_ORDER} from './util/constants';

function Header() {
	const {
		cartState: {
			summary: {itemsCount = 0},
		},
		closeCart,
		labels,
		toggleable,
	} = useContext(MiniCartContext);

	return (
		<div className="mini-cart-header">
			<div className="mini-cart-header-block">
				<div className="mini-cart-header-title">
					<h3>
						{!itemsCount
							? labels[ORDER_IS_EMPTY]
							: labels[YOUR_ORDER]}
					</h3>
				</div>

				{toggleable && (
					<button className="mini-cart-close" onClick={closeCart}>
						<ClayIcon symbol="times" />
					</button>
				)}
			</div>
		</div>
	);
}

export default Header;
