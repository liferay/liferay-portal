/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

// @ts-ignore

import {createCommerceCart} from '../../../utilities/createCommerceCart.js';

interface CreateOrderProps {
	addCommerceOrderURL: string;
	commerceChannelId: number;
	currencyCode: string;
	currentCommerceAccountId: number;
	hasAddCommerceOrderPermission: boolean;
	label: string;
}

const CreateOrder = ({
	addCommerceOrderURL: orderDetailURL,
	commerceChannelId,
	currencyCode,
	currentCommerceAccountId,
	hasAddCommerceOrderPermission,
	label,
}: CreateOrderProps) => {
	return (
		<ClayButton
			className="btn-create-order"
			disabled={
				!hasAddCommerceOrderPermission || !currentCommerceAccountId
			}
			onClick={(event) => {
				event.preventDefault();

				createCommerceCart({
					accountId: currentCommerceAccountId,
					commerceChannelId,
					currencyCode,
					orderDetailURL,
				});
			}}
		>
			{label}
		</ClayButton>
	);
};

export default CreateOrder;
