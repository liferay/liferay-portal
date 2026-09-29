/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import moment from 'moment';
import React from 'react';

import '../../../../css/components/EngagementChartTooltip.scss';

interface IEngagementChartTooltipProps {
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

const formatTooltipDate = (date: string) => {
	const dateObj = new Date(date);

	return new Intl.DateTimeFormat(Liferay.ThemeDisplay.getBCP47LanguageId(), {
		day: '2-digit',
		month: 'short',
		year: 'numeric',
	}).format(dateObj);
};

export default function EngagementChartTooltip({
	active,
	payload,
}: IEngagementChartTooltipProps) {
	if (!active || !payload) {
		return null;
	}

	const {date, numberOfVisits, timeSpent} = payload[0].payload;

	return (
		<div className="chart-tooltip-container" data-qa-id="active-tooltip">
			<p className="chart-tooltip-label mb-1">
				{formatTooltipDate(date)}
			</p>

			<div>
				<span className="mb-1">
					{Liferay.Language.get('number-of-visits')}:
				</span>

				<span className="ml-1">{numberOfVisits}</span>
			</div>

			<div>
				<span>{Liferay.Language.get('time-spent')}:</span>

				<span className="ml-1">{formatTimeSpent(timeSpent)}</span>
			</div>
		</div>
	);
}
