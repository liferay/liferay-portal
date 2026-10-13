/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import {sub} from 'frontend-js-web';
import React from 'react';

export function PercentageOfTotal({
	count,
	template,
	total,
}: {
	count: number;
	template: string;
	total: number;
}) {
	const percentage = total ? (count / total) * 100 : 0;

	const languageId = Liferay.ThemeDisplay.getBCP47LanguageId();

	const label = sub(
		template,
		new Intl.NumberFormat(languageId, {
			maximumFractionDigits: 1,
			style: 'percent',
		}).format(percentage / 100),
		new Intl.NumberFormat(languageId).format(total)
	);

	return (
		<div className="mt-2">
			<Text color="secondary" size={3}>
				{label}
			</Text>

			<div aria-hidden="true" className="mt-2 progress">
				<div
					className="progress-bar"
					style={{width: `${percentage}%`}}
				/>
			</div>
		</div>
	);
}
