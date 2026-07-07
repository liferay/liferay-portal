/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import type {ChartLegendItem} from './types';

interface Props {
	items: ChartLegendItem[];
	onActivate: (id: number) => void;
	onDeactivate: (id: number) => void;
	onSelect: (id: number) => void;
}

export default function ChartLegendList({
	items,
	onActivate,
	onDeactivate,
	onSelect,
}: Props) {
	return (
		<ul aria-hidden="true" className="charts-legend">
			{items.map((item) => (
				<li
					className={classNames('charts-legend__item', {
						'is-active': item.active,
					})}
					key={item.id}
					onClick={() => onSelect(item.id)}
					onMouseEnter={() => onActivate(item.id)}
					onMouseLeave={() => onDeactivate(item.id)}
				>
					{item.visual}

					<span className="charts-legend__label">{item.label}</span>

					{item.listValue !== undefined && (
						<span className="charts-legend__value">
							{item.listValue}
						</span>
					)}
				</li>
			))}
		</ul>
	);
}
