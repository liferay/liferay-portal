/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const defaultSiteInitializer = 'com.liferay.site.initializer.welcome';

export const EXTEND_TYPES = {
	ADMIN_REQUEST: 'admin-request',
	AUTO_EXTEND: 'auto-extend',
};

export const EXTEND_OPTIONS = [
	{
		actionText: 'Submit',
		actionUrl: '',
		alertText: `The trial can be extended automatically once, with immediate effect. After that, any further extension will require admin approval.`,
		alertType: 'info',
		extendType: EXTEND_TYPES.AUTO_EXTEND,
	},
	{
		actionText: 'Submit Request',
		actionUrl: '',
		alertText: `You've already extended your trial once. To extend it again, you’ll need to submit a request to your admin.`,
		alertType: 'warning',
		extendType: EXTEND_TYPES.ADMIN_REQUEST,
	},
] as const;

export const EXTEND_TRIAL_STATUS_LABEL = {
	'Approved': 'Approved',
	'AutoApproved': 'Auto Approved',
	'Pending': 'Request Pending',
	'Rejected': 'Rejected',
	'extension-expired': 'Extension Expired',
	'not-requested': 'Not Requested',
};

export const TRIAL_STATUS_LABEL = {
	'approved': 'Expired',
	'cancelled': 'Cancelled',
	'completed': 'Expired',
	'in-progress': 'Active',
	'on-hold': 'On Hold',
	'pending': 'Not Processed',
	'processing': 'Processing',
};

export const trialObjectives = [
	{
		days: 1,
		key: 'quick-demo',
		name: 'Demo',
	},
	{
		days: 3,
		key: 'feature-showcase',
		name: 'Showcase',
	},
	{
		days: 7,
		key: 'proof-of-concept',
		name: 'Proof of Concept',
	},
	{
		days: 30,
		key: 'pilot-project',
		name: 'Pilot Project',
	},
	{
		days: 90,
		key: 'extended-evaluation',
		name: 'Extended Evaluation',
	},
];

export const siteInitializers = [
	{
		key: 'blank-site-initializer',
		name: 'Blank Site',
	},
	{
		key: 'com.liferay.site.initializer.masterclass',
		name: 'Masterclass',
	},
	{
		key: 'com.liferay.site.initializer.welcome',
		name: 'Welcome',
	},
	{
		key: 'minium-initializer',
		name: 'Minium',
	},
	{
		key: 'speedwell-initializer',
		name: 'Speedwell',
	},
];
