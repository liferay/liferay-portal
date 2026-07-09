/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from '@clayui/shared';
import React from 'react';

import {toPercent} from '../percent';

interface ChartSummaryItem {
	label: string;
	value: number;
}

interface ChartSummaryProps {

	/** Optional accessible description rendered before the item list. */
	description?: string;

	/** Id for `aria-describedby` wiring from the chart's `<figure>`. */
	id: string;

	/** One `{label, value}` entry per datum, in reading order. */
	items: ChartSummaryItem[];

	/** Prefix each entry with its "x of n" position (default `false`). */
	showPosition?: boolean;

	/** Sum of the item values, used to compute each share. */
	total: number;
}

/**
 * A visually hidden, screen-reader-only sentence listing each datum as
 * `label: value (share%)`. Shared across the charts so the spoken summary
 * reads consistently; charts map their data to `{label, value}` items.
 */
export default function ChartSummary({
	description,
	id,
	items,
	showPosition = false,
	total,
}: ChartSummaryProps) {
	return (
		<p className="charts-summary sr-only" id={id}>
			{description ? `${description} ` : ''}

			{items.map((item, index) => {
				const position = showPosition
					? `${sub(Liferay.Language.get('x-of-x'), [
							index + 1,
							items.length,
						])}, `
					: '';

				return `${position}${item.label}: ${item.value} (${toPercent(
					item.value,
					total
				)}%). `;
			})}
		</p>
	);
}
