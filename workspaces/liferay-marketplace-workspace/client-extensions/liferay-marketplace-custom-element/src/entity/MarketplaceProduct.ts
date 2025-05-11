/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MarketplaceDeliveryProduct} from './MarketplaceDeliveryProduct';

export class MarketplaceProduct extends MarketplaceDeliveryProduct {
	constructor(product: Product) {
		super(product as unknown as DeliveryProduct);
	}

	get specificationValues() {
		const specificationValues = super.specificationValues;

		for (const key in specificationValues) {
			(specificationValues as any)[key] = (specificationValues as any)[
				key
			].en_US;
		}

		return specificationValues;
	}
}
