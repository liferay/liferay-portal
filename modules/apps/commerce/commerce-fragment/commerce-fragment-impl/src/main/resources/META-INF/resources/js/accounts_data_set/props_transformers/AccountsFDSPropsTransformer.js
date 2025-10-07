/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AccountNameDataRenderer, {
	changeAccount,
} from '../data_renderers/AccountNameDataRenderer';

const PendingOrdersFDSPropsTransformer = (props) => ({
	...props,
	customDataRenderers: {
		accountNameDataRenderer: (itemProps) =>
			AccountNameDataRenderer({
				...itemProps,
				setCurrentAccountURL:
					props.additionalProps.setCurrentAccountURL,
			}),
	},
	onActionDropdownItemClick: ({
		action: {
			data: {id: actionId},
		},
		itemData: {id: accountId},
	}) => {
		if (actionId === 'view') {
			changeAccount(
				accountId,
				props.additionalProps.setCurrentAccountURL
			);
		}
	},
});

export default PendingOrdersFDSPropsTransformer;
