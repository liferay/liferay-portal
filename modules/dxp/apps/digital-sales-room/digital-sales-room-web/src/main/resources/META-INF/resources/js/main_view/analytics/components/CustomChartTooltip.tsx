/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import moment from 'moment';
import React from 'react';

import '../../../../css/CustomChartTooltip.scss';

interface CustomTooltipProps {
	active?: boolean;
	label?: string;
	payload?: any[];
}

const formatTimeSpent = (totalSeconds: number) => {
	if (!totalSeconds) {
		return '0 min';
	}

	const duration = moment.duration(totalSeconds, 'seconds');

	const hours = Math.floor(duration.asHours());

	const minutes = duration.minutes();

	if (hours > 0) {
		return `${hours}h ${minutes} min`;
	}

	return `${minutes} min`;
};

const formatTooltipDate = (dateString: string) => {
	const dateObj = new Date(dateString);

	return new Intl.DateTimeFormat(Liferay.ThemeDisplay.getBCP47LanguageId(), {
		day: '2-digit',
		month: 'short',
		year: 'numeric',
	}).format(dateObj);
};

export default function CustomChartTooltip({
	active,
	payload,
}: CustomTooltipProps) {
	if (active && payload && payload.length) {
		const {date, numberOfVisits, timeSpent} = payload[0].payload;

		return (
			<div className="chart-tooltip-area" data-qa-id="active-tooltip">
				<p className="chart-tooltip-label mb-1">
					{formatTooltipDate(date)}
				</p>

				<p className="chart-tooltip-value mb-1">
					{Liferay.Language.get('number-of-visits')}:{' '}

					<strong>{numberOfVisits}</strong>
				</p>

				<p className="chart-tooltip-value mb-1">
					{Liferay.Language.get('time-spent')}:{' '}

					<strong>{formatTimeSpent(timeSpent)}</strong>
				</p>
			</div>
		);
	}

	return null;
}
