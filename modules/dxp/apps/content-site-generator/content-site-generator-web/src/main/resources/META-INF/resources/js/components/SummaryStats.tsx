/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import type {SummaryStat} from '../types/ContentModel';

interface IProps {
	stats: SummaryStat[];
}

export default function SummaryStats({stats}: IProps) {
	return (
		<div className="content-site-generator__stats">
			{stats.map((stat) => (
				<div
					className="card content-site-generator__stat"
					key={stat.label}
				>
					<div className="card-body">
						<div className="content-site-generator__stat-label small text-secondary">
							<ClayIcon
								className="mr-2"
								spritemap={Liferay.Icons.spritemap}
								symbol={stat.icon}
							/>

							{stat.label}
						</div>

						<div className="content-site-generator__stat-value font-weight-semi-bold">
							{stat.value}
						</div>
					</div>
				</div>
			))}
		</div>
	);
}
