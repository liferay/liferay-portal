/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const salesforceConfig = {
	salesforceConsumerKey: process.env.OBJECT_STORAGE_SALESFORCE_CONSUMER_KEY
		? process.env.OBJECT_STORAGE_SALESFORCE_CONSUMER_KEY
		: '',
	salesforceConsumerSecret: process.env
		.OBJECT_STORAGE_SALESFORCE_CONSUMER_SECRET
		? process.env.OBJECT_STORAGE_SALESFORCE_CONSUMER_SECRET
		: '',
	salesforceLoginURL: process.env.OBJECT_STORAGE_SALESFORCE_LOGIN_URL
		? process.env.OBJECT_STORAGE_SALESFORCE_LOGIN_URL
		: 'https://test.salesforce.com/',
	salesforcePassword: process.env.OBJECT_STORAGE_SALESFORCE_PASSWORD
		? process.env.OBJECT_STORAGE_SALESFORCE_PASSWORD
		: '',
	salesforceUsername: process.env.OBJECT_STORAGE_SALESFORCE_USERNAME
		? process.env.OBJECT_STORAGE_SALESFORCE_USERNAME
		: '',
};

export {salesforceConfig};
