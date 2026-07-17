/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export default function ChartEmptyState() {
	return (
		<div className="seo-studio-metrics-dashboard-chart-empty">
			<h4 className="seo-studio-metrics-dashboard-chart-empty-title">
				{Liferay.Language.get('no-data-available-yet')}
			</h4>

			<p className="seo-studio-metrics-dashboard-chart-empty-description text-secondary">
				{Liferay.Language.get(
					'no-data-is-available-yet.-results-will-appear-here-once-the-seo-health-scan-completes'
				)}
			</p>
		</div>
	);
}
