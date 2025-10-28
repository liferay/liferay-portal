/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '../../../i18n';

export const AccountType = [
	{
		key: 'business',
		name: i18n.translate('business'),
		text: i18n.translate(
			'for-businesses-with-a-vat-tax-number-this-account-type-support-multiple-users'
		),
	},
	{
		key: 'person',
		name: i18n.translate('personal'),
		text: i18n.translate(
			'for-individuals-without-a-vat-tax-number-this-account-support-single-user-only'
		),
	},
	{
		key: 'existing-business',
		name: i18n.translate('join-existing-business-account'),
		text: i18n.translate(
			'join-an-existing-business-account-e-g-your-company-and-gain-full-access'
		),
	},
];
