/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PieDatum} from './PieDatum';

export interface PieChartLegendBaseProps {
	activeIndex: number | null;
	colors: string[];
	data: PieDatum[];
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	total: number;
}
