/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum OrderCustomFields {
	ANALYTICS_GROUP_ID = 'analytics-group-id',
	CLOUD_PROVISIONING = 'cloud-provisioning',
	END_DATE = 'trial-end-date',
	PROJECT_NAME = 'project-name',
	START_DATE = 'trial-start-date',
	TRIAL_ERROR = 'trial-error',
	TRIAL_SETTINGS = 'trial-settings',
	VIRTUAL_HOST = 'trial-virtualhost',
}

export enum OrderStatus {
	APPROVED = 'approved',
	COMPLETED = 'completed',
	PENDING = 'pending',
	PROCESSING = 'processing',
}

export enum OrderTypes {
	ADDONS = 'ADDONS',
	CLIENT_EXTENSION = 'CLIENT_EXTENSION',
	CLOUDAPP = 'CLOUDAPP',
	COMPOSITE_APP = 'COMPOSITE_APP',
	DXPAPP = 'DXPAPP',
	LOW_CODE_CONFIGURATION = 'LOW_CODE_CONFIGURATION',
	OTHER = 'OTHER',
	SOLUTIONS7 = 'SOLUTIONS7',
	SOLUTIONS30 = 'SOLUTIONS30',
}

export enum OrderWorkflowStatusCode {
	CANCELLED = 8,
	COMPLETED = 0,
	IN_PROGRESS = 6,
	ON_HOLD = 20,
	PENDING = 1,
	PROCESSING = 10,
}

export enum PaymentStatus {
	PAID = 0,
	PENDING = 1,
	PAYMENT_PENDING = 2,
}

export const orderTypeLabel = {
	[OrderTypes.ADDONS]: 'Add-Ons',
	[OrderTypes.CLIENT_EXTENSION]: 'Client Extension',
	[OrderTypes.CLOUDAPP]: 'Cloud',
	[OrderTypes.COMPOSITE_APP]: 'Composite App',
	[OrderTypes.DXPAPP]: 'DXP',
	[OrderTypes.LOW_CODE_CONFIGURATION]: 'Low-Code Configuration',
	[OrderTypes.OTHER]: 'Other',
	[OrderTypes.SOLUTIONS7]: 'Solutions 7',
	[OrderTypes.SOLUTIONS30]: 'Solutions 30',
} as const;

export const OrderWorkflowDisplayType = {
	[OrderWorkflowStatusCode.COMPLETED]: 'success',
	[OrderWorkflowStatusCode.CANCELLED]: 'warning',
	[OrderWorkflowStatusCode.IN_PROGRESS]: 'info',
	[OrderWorkflowStatusCode.ON_HOLD]: 'secondary',
	[OrderWorkflowStatusCode.PENDING]: 'warning',
	[OrderWorkflowStatusCode.PROCESSING]: 'secondary',
} as const;

export const PaymentWorkflowDisplayType = {
	[PaymentStatus.PAID]: 'success',
	[PaymentStatus.PENDING]: 'secondary',
	[PaymentStatus.PAYMENT_PENDING]: 'warning',
} as const;
