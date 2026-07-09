/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '../../css/ChartTooltip.scss';

interface ChartTooltipProps {
	label: string;
	value: React.ReactNode;
}

/**
 * A corner-pinned label/value chip shown while a datum is active. It is an
 * absolutely positioned HTML overlay, so it auto-sizes to its text (no manual
 * width math) and is shared by the charts that pin a readout to a corner (Map,
 * and Line's `corner` tooltip). The nearest positioned ancestor is the anchor,
 * so the host must give the chip a `position: relative` container.
 */
export default function ChartTooltip({label, value}: ChartTooltipProps) {
	return (
		<div aria-hidden="true" className="charts-tooltip">
			<span className="charts-tooltip-label">{label}</span>

			<span className="charts-tooltip-value">{value}</span>
		</div>
	);
}
