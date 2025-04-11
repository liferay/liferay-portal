/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProductOptionsDataRenderer} from 'commerce-frontend-js';

export default function PlacedOrderItemClassicFDSPropsTransformer(props) {
	return {
		...props,
		customDataRenderers: {
			productOptionsDataRenderer: (componentProps) =>
				ProductOptionsDataRenderer({
					...componentProps,
					additionalProps: props.additionalProps,
				}),
		},
	};
}
