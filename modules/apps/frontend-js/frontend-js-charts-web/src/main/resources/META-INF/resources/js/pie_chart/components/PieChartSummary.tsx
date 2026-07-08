/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from '@clayui/shared';
import React from 'react';

import {toPercent} from '../../percent';
import {PieDatum} from '../types/PieDatum';

interface PieChartSummaryProps {
	data: PieDatum[];
	description: string | undefined;
	id: string;
	total: number;
}

export default function PieChartSummary({
	data,
	description,
	id,
	total,
}: PieChartSummaryProps) {
	return (
		<p className="chart-pie-summary sr-only" id={id}>
			{description ? `${description} ` : ''}

			{data.map((datum, index) => {
				const position = sub(Liferay.Language.get('x-of-x'), [
					index + 1,
					data.length,
				]);

				return `${position}, ${datum.label}: ${
					datum.value
				} (${toPercent(datum.value, total)}%). `;
			})}
		</p>
	);
}
