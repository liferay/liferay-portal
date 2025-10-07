/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../../../liferay/liferay';

export function formatAddress(address: BillingAddress) {
	if (!address || !Object.keys(address).length) {
		return '-';
	}

	const displayNames = new Intl.DisplayNames(
		[Liferay.ThemeDisplay.getBCP47LanguageId()],
		{type: 'region'}
	);

	return [
		address.street1,
		address.city,
		address.regionISOCode,
		address.zip,
		displayNames.of(address.countryISOCode as string),
	]
		.filter(Boolean)
		.join(', ');
}

export function formatDate(date: string | undefined) {
	if (!date) {
		return '-';
	}

	return new Date(date).toLocaleDateString('en-US', {
		day: 'numeric',
		hour: 'numeric',
		minute: '2-digit',
		month: 'short',
		year: 'numeric',
	});
}

export function textWrapper(content: string | number | undefined) {
	if (content === undefined || content === null || content === '') {
		return <p className="mb-2 mt-1">-</p>;
	}

	return <p className="mb-2 mt-1">{content}</p>;
}
