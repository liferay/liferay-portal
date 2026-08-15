/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {
	TrendClassification,
	getPercentage,
	getStatsColor,
	getStatsIcon,
} from '@liferay/analytics-reports-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

type Trend = {
	classification: TrendClassification;
	percentage: number;
};

const Trend = ({classification, percentage}: Trend) => {
	const formattedPercentage = getPercentage(percentage);
	const statsIcon = getStatsIcon(percentage);

	return (
		<div>
			<Text color={getStatsColor(classification)} size={3}>
				{statsIcon && (
					<span className="mr-1">
						<ClayIcon symbol={statsIcon} />
					</span>
				)}

				<span>{formattedPercentage}%</span>
			</Text>

			<Text color="secondary" size={3}>
				<span
					className="text-lowercase"
					dangerouslySetInnerHTML={{
						__html: sub(
							Liferay.Language.get('x-vs-previous-period'),
							`<span class='hide'>${formattedPercentage}</span>`
						),
					}}
				/>
			</Text>
		</div>
	);
};

const MetricValue = ({
	textWeight = 'semi-bold',
	trend,
	value,
	valueClassName,
}: {
	textWeight?: React.ComponentProps<typeof Text>['weight'];
	trend?: Trend;
	value: React.ReactNode;
	valueClassName?: string;
}) => {
	return (
		<>
			<div className={valueClassName}>
				<Text size={9} weight={textWeight}>
					{value}
				</Text>
			</div>

			{trend ? <Trend {...trend} /> : null}
		</>
	);
};

export {MetricValue};
