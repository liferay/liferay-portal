/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {CurrencyUtils, formatActionUrl} from 'commerce-frontend-js';
import {navigate} from 'frontend-js-web';
import React from 'react';

export function wipeCurrencyAndNavigate({cart, setCurrentOrderURL}) {
	CurrencyUtils.resetCommerceCurrency();

	navigate(
		formatActionUrl(
			setCurrentOrderURL,
			cart,
			Liferay.FeatureFlags['LPD-20379'] ? {skipRedirect: true} : {}
		)
	);
}

const PendingOrderIdDataRenderer = ({setCurrentOrderURL, ...props}) => {
	return (
		<div className="table-list-title">
			<ClayLink
				data-senna-off
				href="#"
				onClick={(event) => {
					event.preventDefault();

					wipeCurrencyAndNavigate({
						cart: props.itemData,
						setCurrentOrderURL,
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
