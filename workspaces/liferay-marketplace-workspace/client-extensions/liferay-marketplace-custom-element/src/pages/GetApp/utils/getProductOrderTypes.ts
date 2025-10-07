/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProductType} from '../../../enums/Product';

const productTypeERC = {
	[ProductType.CLIENT_EXTENSION]: 'CLIENT_EXTENSION',
	[ProductType.CLOUD]: 'CLOUDAPP',
	[ProductType.COMPOSITE_APP]: 'COMPOSITE_APP',
	[ProductType.DXP]: 'DXPAPP',
	[ProductType.LOW_CODE_CONFIGURATION]: 'LOW_CODE_CONFIGURATION',
	[ProductType.OTHER]: 'OTHER',
	[ProductType.SSA_SAAS]: 'SSA_SAAS',
} as const;

export function getProductOrderTypes(productSpecificationValue: any) {
	const productSpecification = productSpecificationValue.toLowerCase();

	return {
		externalReferenceCode:
			productTypeERC[productSpecification as ProductType] || 'NOTYPE',
	} as OrderType;
}
