/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum OrderCustomFields {
	ANALYTICS_GROUP_ID = 'analytics-group-id',
	CLOUD_PROVISIONING = 'cloud-provisioning',
	KORONEIKI_PROJECT = 'koroneiki-project',
	ORDER_METADATA = 'order-metadata',
	PROJECT_NAME = 'project-name',
	TRIAL_END_DATE = 'trial-end-date',
	TRIAL_ERROR = 'trial-error',
	TRIAL_SETTINGS = 'trial-settings',
	TRIAL_START_DATE = 'trial-start-date',
	TRIAL_VIRTUAL_HOST = 'trial-virtual-host',
}

export enum OrderStatus {
	APPROVED = 'approved',
	CANCELLED = 'cancelled',
	COMPLETED = 'completed',
	IN_PROGRESS = 'in-progress',
	ON_HOLD = 'on-hold',
	PENDING = 'pending',
	PROCESSING = 'processing',
}

export enum OrderTypes {
	ADDONS = 'ADDONS',
	AI_HUB = 'AI_HUB',
	AI_HUB_TOKEN = 'AI_HUB_TOKEN',
	CLIENT_EXTENSION = 'CLIENT_EXTENSION',
	CLOUD_APP = 'CLOUD_APP',
	CMP = 'CMP',
	CMP_BETA = 'CMP_BETA',
	COMPOSITE_APP = 'COMPOSITE_APP',
	DSR = 'DSR',
	DXP = 'DXP',
	DXP_APP = 'DXP_APP',
	LOW_CODE_CONFIGURATION = 'LOW_CODE_CONFIGURATION',
	OTHER = 'OTHER',
	SOLUTIONS30 = 'SOLUTIONS30',
	SOLUTIONS7 = 'SOLUTIONS7',
	SSA_SAAS = 'SSA_SAAS',
}

export enum OrderWorkflowStatusCode {
	CANCELLED = 8,
	COMPLETED = 0,
	IN_PROGRESS = 6,
	ON_HOLD = 20,
	PENDING = 1,
	PROCESSING = 10,
	PENDING_PAYMENT = 99,
}

export enum PaymentStatus {
	CANCELED = 8,
	FAILED = 4,
	PAID = 0,
	PAYMENT_PENDING = 2,
	PENDING = 1,
}

export const APP_ORDER_TYPES: readonly OrderTypes[] = [
	OrderTypes.CLIENT_EXTENSION,
	OrderTypes.CLOUD_APP,
	OrderTypes.COMPOSITE_APP,
	OrderTypes.DXP_APP,
	OrderTypes.LOW_CODE_CONFIGURATION,
	OrderTypes.OTHER,
];

export const CMP_ORDER_TYPES: readonly OrderTypes[] = [
	OrderTypes.CMP,
	OrderTypes.CMP_BETA,
];

export const LIFERAY_PRODUCT_ORDER_TYPES: readonly OrderTypes[] = [
	OrderTypes.ADDONS,
	OrderTypes.AI_HUB,
	OrderTypes.CMP,
	OrderTypes.CMP_BETA,
	OrderTypes.DXP,
];

export const orderTypeDocumentationURL: Partial<Record<OrderTypes, string>> = {
	[OrderTypes.CMP]: 'https://learn.liferay.com/content-marketing-platform',
	[OrderTypes.CMP_BETA]:
		'https://learn.liferay.com/content-marketing-platform',
	[OrderTypes.DSR]: 'https://learn.liferay.com/w/digital-sales-room/index',
	[OrderTypes.DXP]:
		'https://learn.liferay.com/w/dxp/self-hosted-installation-and-upgrades/setting-up-liferay/activating-liferay-dxp',
};

export const orderTypeLabel = {
	[OrderTypes.ADDONS]: 'Add-Ons',
	[OrderTypes.AI_HUB]: 'AI Hub',
	[OrderTypes.AI_HUB_TOKEN]: 'AI Hub Token',
	[OrderTypes.CLIENT_EXTENSION]: 'Client Extension',
	[OrderTypes.CLOUD_APP]: 'Cloud',
	[OrderTypes.CMP]: 'Content Marketing Platform',
	[OrderTypes.CMP_BETA]: 'Content Marketing Platform',
	[OrderTypes.COMPOSITE_APP]: 'Composite App',
	[OrderTypes.DSR]: 'Digital Sales Room',
	[OrderTypes.DXP_APP]: 'DXP',
	[OrderTypes.DXP]: 'DXP Free',
	[OrderTypes.LOW_CODE_CONFIGURATION]: 'Low-Code Configuration',
	[OrderTypes.OTHER]: 'Other',
	[OrderTypes.SOLUTIONS7]: 'Solutions 7',
	[OrderTypes.SOLUTIONS30]: 'Solutions 30',
	[OrderTypes.SSA_SAAS]: 'SSA SaaS',
} as const;

export const orderWorkflowDisplayType = {
	[OrderWorkflowStatusCode.CANCELLED]: 'warning',
	[OrderWorkflowStatusCode.COMPLETED]: 'success',
	[OrderWorkflowStatusCode.IN_PROGRESS]: 'info',
	[OrderWorkflowStatusCode.ON_HOLD]: 'secondary',
	[OrderWorkflowStatusCode.PENDING]: 'warning',
	[OrderWorkflowStatusCode.PROCESSING]: 'secondary',
} as const;

export const orderWorkflowStatusCodeLabels = {
	[OrderWorkflowStatusCode.CANCELLED]: 'Cancelled',
	[OrderWorkflowStatusCode.COMPLETED]: 'Completed',
	[OrderWorkflowStatusCode.IN_PROGRESS]: 'In Progress',
	[OrderWorkflowStatusCode.ON_HOLD]: 'On Hold',
	[OrderWorkflowStatusCode.PENDING]: 'Pending',
	[OrderWorkflowStatusCode.PENDING_PAYMENT]: 'Pending Payment',
	[OrderWorkflowStatusCode.PROCESSING]: 'Processing',
} as const;

export const paymentWorkflowDisplayType = {
	[PaymentStatus.PAID]: 'success',
	[PaymentStatus.PENDING]: 'secondary',
	[PaymentStatus.PAYMENT_PENDING]: 'warning',
} as const;

export function getOrderStatusLabel(order: PlacedOrder) {
	if (
		[
			OrderTypes.ADDONS,
			OrderTypes.CMP,
			OrderTypes.CMP_BETA,
			OrderTypes.DXP,
			OrderTypes.DSR,
		].includes(order.orderTypeExternalReferenceCode as OrderTypes)
	) {
		return (
			{
				[OrderWorkflowStatusCode.CANCELLED]: 'Expired',
				[OrderWorkflowStatusCode.COMPLETED]: 'Active',
				[OrderWorkflowStatusCode.IN_PROGRESS]: 'Active',
				[OrderWorkflowStatusCode.ON_HOLD]: 'Pending',
				[OrderWorkflowStatusCode.PENDING]: 'Pending',
				[OrderWorkflowStatusCode.PROCESSING]: 'Pending',
			}[order.orderStatusInfo.code] || order.orderStatusInfo.label
		);
	}

	if (order.orderTypeExternalReferenceCode === OrderTypes.AI_HUB) {
		if (order.orderStatusInfo.code !== OrderWorkflowStatusCode.COMPLETED) {
			return 'Pending';
		}

		return 'Active';
	}

	return order.orderStatusInfo.label;
}
