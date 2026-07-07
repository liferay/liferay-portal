/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface Props {
	axis: {x1: number; x2: number; y1: number; y2: number};
}

export default function LineChartAxis({axis}: Props) {
	return (
		<line
			className="charts-line-chart__axis"
			x1={axis.x1}
			x2={axis.x2}
			y1={axis.y1}
			y2={axis.y2}
		/>
	);
}
