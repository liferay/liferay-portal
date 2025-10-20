/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';

// @ts-ignore

import {CommerceFrontendUtils} from 'commerce-frontend-js';
import React from 'react';

const CreateOrderButton = ({
	addCommerceOrderURL: orderDetailURL,
	commerceChannelId,
	configuration,
	currencyCode,
	currentCommerceAccountId,
	hasAddCommerceOrderPermission,
}: TCreateOrderButtonProps) => {
	return (
		<ClayButton
			className="btn-create-order"
			disabled={
				!hasAddCommerceOrderPermission || !currentCommerceAccountId
			}
			onClick={(event) => {
				event.preventDefault();

				CommerceFrontendUtils.createCommerceCart({
					accountId: currentCommerceAccountId,
					commerceChannelId,
					currencyCode,
					orderDetailURL,
				});
			}}
		>
			{configuration.label}
		</ClayButton>
	);
};

export default CreateOrderButton;
