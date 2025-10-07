/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {CurrencyUtils} from 'commerce-frontend-js';
import {navigate} from 'frontend-js-web';
import React from 'react';

export function wipeCurrencyAndNavigate({cartId, orderDetailURL}) {
	CurrencyUtils.resetCommerceCurrency();

	navigate(`${orderDetailURL}${cartId}`);
}

const PendingOrderIdDataRenderer = ({orderDetailURL, ...props}) => {
	return (
		<div className="table-list-title">
			<ClayLink
				data-senna-off
				href="#"
				onClick={(event) => {
					event.preventDefault();

					wipeCurrencyAndNavigate({
						cartId: props.itemId,
						orderDetailURL,
					});
				}}
				role="button"
			>
				{props.itemId}
			</ClayLink>
		</div>
	);
};

export default PendingOrderIdDataRenderer;
