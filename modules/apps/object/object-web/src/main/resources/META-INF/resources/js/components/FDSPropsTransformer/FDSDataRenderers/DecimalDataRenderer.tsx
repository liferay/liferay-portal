/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export default function DecimalDataRenderer({value}: {value: number}) {
	if (value !== undefined) {
		const formatedValue = new Intl.NumberFormat(
			Liferay.ThemeDisplay.getBCP47LanguageId(),
			{
				maximumFractionDigits: 20,
				style: 'decimal',
				useGrouping: false,
			}
		).format(value);

		return <span>{formatedValue}</span>;
	}

	return <span>0</span>;
}
