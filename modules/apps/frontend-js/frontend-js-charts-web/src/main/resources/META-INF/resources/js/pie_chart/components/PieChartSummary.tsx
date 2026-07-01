/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PieDatum} from '../types/PieDatum';
import {toPercent} from '../utils/percent';

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

			{data.map(
				(datum, index) =>
					`${index + 1} of ${data.length}, ${datum.label}: ${
						datum.value
					} (${toPercent(datum.value, total)}%). `
			)}
		</p>
	);
}
