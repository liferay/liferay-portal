/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AdditionalProps} from './AssetsFDSPropsTransformer';
import getDashboardAssetListFDSProps from './getDashboardAssetListFDSProps';

export default function HomeRecentAssetsFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: AdditionalProps;
	apiURL?: string;
	itemsActions?: any[];
	otherProps: any;
}) {
	return getDashboardAssetListFDSProps({
		...otherProps,
		additionalProps,
		apiURL: `${otherProps.apiURL}&pageSize=16`,
		itemsActions,
	});
}
