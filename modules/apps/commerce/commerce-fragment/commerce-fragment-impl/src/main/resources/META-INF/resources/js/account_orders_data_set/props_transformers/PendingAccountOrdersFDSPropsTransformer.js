/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PendingOrderIdDataRenderer, {
	wipeCurrencyAndNavigate,
} from '../data_renderers/PendingOrderIdDataRenderer';

const PendingOrdersFDSPropsTransformer = (props) => ({
	...props,
	customDataRenderers: {
		pendingOrderIdDataRenderer: (itemProps) =>
			PendingOrderIdDataRenderer({
				...itemProps,
				setCurrentOrderURL: props.additionalProps.setCurrentOrderURL,
			}),
	},
	onActionDropdownItemClick: ({
		action: {
			data: {id: actionId},
		},
		itemData: cart,
	}) => {
		if (actionId === 'view') {
			wipeCurrencyAndNavigate({
				cart,
				setCurrentOrderURL: props.additionalProps.setCurrentOrderURL,
			});
		}
	},
});

export default PendingOrdersFDSPropsTransformer;
