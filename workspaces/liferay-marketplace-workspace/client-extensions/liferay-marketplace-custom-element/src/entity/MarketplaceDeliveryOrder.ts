/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {OrderTypes, OrderWorkflowStatusCode} from '../enums/Order';
import {safeJSONParse} from '../utils/util';

export default class MarketplaceDeliveryOrder {
	constructor(private order: PlacedOrder) {}

	get createDate() {
		return this.order.createDate;
	}

	get isDownloadable() {
		return [
			OrderTypes.CLIENT_EXTENSION,
			OrderTypes.COMPOSITE_APP,
			OrderTypes.DXPAPP,
			OrderTypes.OTHER,
		].includes(this.order.orderTypeExternalReferenceCode as OrderTypes);
	}

	get isFreeApp() {
		const orderOptions = safeJSONParse<
			Array<{key: string; value: string[]}>
		>(this.order.placedOrderItems?.[0]?.options, []);

		return (
			this.order.placedOrderItems?.[0]?.price?.price === 0 &&
			!orderOptions.some(({value}) => value.includes('trial'))
		);
	}

	get isOrderStatusCompleted() {
		return (
			this.order.orderStatusInfo?.code ===
			OrderWorkflowStatusCode.COMPLETED
		);
	}
}
