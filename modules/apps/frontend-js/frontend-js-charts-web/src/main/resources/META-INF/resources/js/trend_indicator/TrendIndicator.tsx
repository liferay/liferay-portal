/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

import '../../css/TrendIndicator.scss';

export type TrendDirection = 'down' | 'neutral' | 'up';

export interface TrendIndicatorProps {

	/**
	 * Accessible name for the indicator. Trend phrasing and word order vary
	 * across languages, so the consumer may pass a fully localized string; when
	 * omitted, it falls back to a localized default for the direction with
	 * `value` interpolated in.
	 */
	ariaLabel?: string;

	/** Optional class name for the root element. */
	className?: string;

	/**
	 * The trend direction. Drives the arrow and color token — the sign of
	 * `value` does not determine it.
	 */
	direction: TrendDirection;

	/** Optional context shown after the value (e.g. "versus previous period"). */
	label?: string;

	/** The magnitude shown as a percentage. */
	value: number;
}

const DIRECTION_SYMBOLS: Record<TrendDirection, string> = {
	down: 'caret-bottom',
	neutral: 'hr',
	up: 'caret-top',
};

function getDefaultAriaLabel(direction: TrendDirection, value: number): string {
	const labels: Partial<Record<TrendDirection, string>> = {
		down: Liferay.Language.get('trending-down-by-x-percent'),
		up: Liferay.Language.get('trending-up-by-x-percent'),
	};

	const label = labels[direction];

	if (label) {
		return Liferay.Util.sub(label, [value]);
	}

	return Liferay.Language.get('no-change');
}

export default function TrendIndicator({
	ariaLabel,
	className,
	direction,
	label,
	value,
}: TrendIndicatorProps) {
	const symbol = DIRECTION_SYMBOLS[direction];

	const resolvedAriaLabel =
		ariaLabel ?? getDefaultAriaLabel(direction, value);

	return (
		<span
			aria-label={resolvedAriaLabel}
			className={classNames(
				'charts-trend-indicator',
				`charts-trend-indicator--${direction}`,
				className
			)}
			role="img"
		>
			<ClayIcon symbol={symbol} />

			<span className="charts-trend-indicator__value">{`${value}%`}</span>

			{label && (
				<span className="charts-trend-indicator__label">{label}</span>
			)}
		</span>
	);
}
