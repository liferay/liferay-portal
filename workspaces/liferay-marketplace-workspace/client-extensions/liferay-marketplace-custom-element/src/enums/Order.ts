/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum OrderCustomFields {
	ANALYTICS_GROUP_ID = 'analytics-group-id',
	CLOUD_PROVISIONING = 'cloud-provisioning',
	KORONEIKI_PROJECT = 'koroneiki-project',
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
	CLIENT_EXTENSION = 'CLIENT_EXTENSION',
	CLOUDAPP = 'CLOUDAPP',
	COMPOSITE_APP = 'COMPOSITE_APP',
	DXPAPP = 'DXPAPP',
	LOW_CODE_CONFIGURATION = 'LOW_CODE_CONFIGURATION',
	OTHER = 'OTHER',
	SOLUTIONS7 = 'SOLUTIONS7',
	SOLUTIONS30 = 'SOLUTIONS30',
	SSA_SAAS = 'SSA_SAAS',
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
	CANCELED = 8,
	FAILED = 4,
	PAID = 0,
	PAYMENT_PENDING = 2,
	PENDING = 1,
}

export const orderTypeLabel = {
	[OrderTypes.ADDONS]: 'Add-Ons',
	[OrderTypes.CLIENT_EXTENSION]: 'Client Extension',
	[OrderTypes.CLOUDAPP]: 'Cloud',
	[OrderTypes.COMPOSITE_APP]: 'Composite App',
	[OrderTypes.DXPAPP]: 'DXP',
	[OrderTypes.LOW_CODE_CONFIGURATION]: 'Low-Code Configuration',
	[OrderTypes.OTHER]: 'Other',
	[OrderTypes.SSA_SAAS]: 'SSA SaaS',
	[OrderTypes.SOLUTIONS7]: 'Solutions 7',
	[OrderTypes.SOLUTIONS30]: 'Solutions 30',
} as const;

export const orderWorkflowDisplayType = {
	[OrderWorkflowStatusCode.COMPLETED]: 'success',
	[OrderWorkflowStatusCode.CANCELLED]: 'warning',
	[OrderWorkflowStatusCode.IN_PROGRESS]: 'info',
	[OrderWorkflowStatusCode.ON_HOLD]: 'secondary',
	[OrderWorkflowStatusCode.PENDING]: 'warning',
	[OrderWorkflowStatusCode.PROCESSING]: 'secondary',
} as const;

export const orderWorkflowStatusCodeLabels = {
	[OrderWorkflowStatusCode.CANCELLED]: 'Canceled',
	[OrderWorkflowStatusCode.COMPLETED]: 'Completed',
	[OrderWorkflowStatusCode.IN_PROGRESS]: 'In Progress',
	[OrderWorkflowStatusCode.ON_HOLD]: 'On Hold',
	[OrderWorkflowStatusCode.PENDING]: 'Pending',
	[OrderWorkflowStatusCode.PROCESSING]: 'Processing',
} as const;

export const paymentWorkflowDisplayType = {
	[PaymentStatus.PAID]: 'success',
	[PaymentStatus.PENDING]: 'secondary',
	[PaymentStatus.PAYMENT_PENDING]: 'warning',
} as const;
