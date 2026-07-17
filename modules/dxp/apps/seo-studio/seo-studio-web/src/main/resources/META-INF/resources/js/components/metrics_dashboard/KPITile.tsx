/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

export default function KPITile({
	description,
	empty,
	icon,
	title,
	value,
}: {
	description: string;
	empty?: boolean;
	icon: string;
	title: string;
	value: number | string;
}) {
	const themeImagesPath = Liferay.ThemeDisplay.getPathThemeImages?.() ?? '';

	const spritemap = `${themeImagesPath}/clay/icons.svg`;

	return (
		<div className="seo-studio-metrics-dashboard-tile">
			<div className="seo-studio-metrics-dashboard-tile-header">
				<span className="seo-studio-metrics-dashboard-tile-title">
					{title}
				</span>

				<span className="seo-studio-metrics-dashboard-tile-icon">
					<ClayIcon spritemap={spritemap} symbol={icon} />
				</span>
			</div>

			<p className="seo-studio-metrics-dashboard-tile-description">
				{description}
			</p>

			<span
				className={classNames(
					'seo-studio-metrics-dashboard-tile-value',
					{
						'seo-studio-metrics-dashboard-tile-value-empty': empty,
					}
				)}
			>
				{value}
			</span>
		</div>
	);
}
