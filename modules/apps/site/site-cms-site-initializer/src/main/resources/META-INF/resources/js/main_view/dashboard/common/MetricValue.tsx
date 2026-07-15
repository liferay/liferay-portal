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

const MetricValue = ({
	textWeight = 'semi-bold',
	trend,
	value,
	valueClassName,
}: {
	textWeight?: React.ComponentProps<typeof Text>['weight'];
	trend: {
		classification: TrendClassification;
		percentage: number;
	};
	value: React.ReactNode;
	valueClassName?: string;
}) => {
	const percentage = getPercentage(trend.percentage);
	const statsColor = getStatsColor(trend.classification);
	const statsIcon = getStatsIcon(trend.percentage);

	return (
		<>
			<div className={valueClassName}>
				<Text size={9} weight={textWeight}>
					{value}
				</Text>
			</div>

			<div>
				<Text color={statsColor} size={3}>
					{statsIcon && (
						<span className="mr-1">
							<ClayIcon
								aria-label={statsIcon}
								symbol={statsIcon}
							/>
						</span>
					)}

					<span>{percentage}%</span>
				</Text>

				<Text color="secondary" size={3}>
					<span
						className="text-lowercase"
						dangerouslySetInnerHTML={{
							__html: sub(
								Liferay.Language.get('x-vs-previous-period'),
								`<span class='hide'>${percentage}</span>`
							),
						}}
					/>
				</Text>
			</div>
		</>
	);
};

export {MetricValue};
