/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	OrderCustomFields,
	OrderTypes,
	OrderWorkflowStatusCode,
} from '../enums/Order';

type CustomFields = {
	[key in keyof typeof OrderCustomFields]: string;
};

export default class MarketplaceDeliveryOrder {
	constructor(private order: PlacedOrder) {}

	get canDownload() {
		return [
			OrderTypes.CLIENT_EXTENSION,
			OrderTypes.COMPOSITE_APP,
			OrderTypes.DXPAPP,
			OrderTypes.LOW_CODE_CONFIGURATION,
			OrderTypes.OTHER,
		].includes(this.order.orderTypeExternalReferenceCode as OrderTypes);
	}

	get createDate() {
		return this.order.createDate;
	}

	get dxpProvisioningEnabled() {
		return (
			this.order.orderTypeExternalReferenceCode === OrderTypes.DXPAPP &&
			!this.isFreeApp
		);
	}
	get customFields() {
		const customFields = {} as CustomFields;

		for (const key in OrderCustomFields) {
			const keyValue = (OrderCustomFields as any)[key];

			(customFields as any)[key] = this.order?.customFields?.[keyValue];
		}

		return customFields;
	}

	get isCancelled() {
		return (
			this.order?.orderStatusInfo?.code ===
			OrderWorkflowStatusCode.CANCELLED
		);
	}

	get isFreeApp() {
		return this.order.placedOrderItems?.[0]?.price?.price === 0;
	}

	get isOrderCompleted() {
		return (
			this.order.orderStatusInfo?.code ===
			OrderWorkflowStatusCode.COMPLETED
		);
	}

	get placedOrderItems() {
		return this.order?.placedOrderItems ?? [];
	}

	get productThumbnail() {
		return this.placedOrderItems[0]?.thumbnail;
	}
}
