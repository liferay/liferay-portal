/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PieDatum} from '../types/PieDatum';

interface PieChartCenterLabelProps {
	activeDatum?: PieDatum;
	activePercent?: string;
	total: number;
}

export default function PieChartCenterLabel({
	activeDatum,
	activePercent,
	total,
}: PieChartCenterLabelProps) {
	return (
		<div aria-hidden="true" className="chart-pie-center-label">
			{activeDatum ? (
				<>
					<span className="chart-pie-center-label-title">
						{activeDatum.label}
					</span>

					<span className="chart-pie-center-label-percent">
						{activePercent}%
					</span>

					<span className="chart-pie-center-label-value">
						{activeDatum.value.toLocaleString()}
					</span>
				</>
			) : (
				<>
					<span className="chart-pie-center-label-title">
						{Liferay.Language.get('total')}
					</span>

					<span className="chart-pie-center-label-total">
						{total.toLocaleString()}
					</span>
				</>
			)}
		</div>
	);
}
