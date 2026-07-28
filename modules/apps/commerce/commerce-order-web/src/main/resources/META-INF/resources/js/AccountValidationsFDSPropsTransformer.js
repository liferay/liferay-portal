/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AccountValidationResultMessageDataRenderer from './AccountValidationResultMessageDataRenderer';

const AccountValidationsFDSPropsTransformer = (props) => ({
	...props,
	customDataRenderers: {
		accountValidationResultMessageDataRenderer: (componentProps) =>
			AccountValidationResultMessageDataRenderer({
				...componentProps,
				additionalProps: props.additionalProps,
			}),
	},
});

export default AccountValidationsFDSPropsTransformer;
