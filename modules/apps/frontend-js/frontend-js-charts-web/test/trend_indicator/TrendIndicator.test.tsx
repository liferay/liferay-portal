/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {TrendIndicator} from '../../src/main/resources/META-INF/resources/js';

describe('TrendIndicator', () => {
	it('falls back to the localized default for the direction', () => {
		render(
			<TrendIndicator
				direction="up"
				label="versus previous period"
				value={22.5}
			/>
		);

		expect(screen.getByRole('img')).toHaveAccessibleName(
			'trending-up-by-22.5-percent'
		);
	});

	it('uses a consumer-provided accessible name when given', () => {
		render(
			<TrendIndicator
				ariaLabel="Alta de 22,5% em relação ao período anterior"
				direction="up"
				value={22.5}
			/>
		);

		expect(screen.getByRole('img')).toHaveAccessibleName(
			'Alta de 22,5% em relação ao período anterior'
		);
	});

	it('renders an arrow icon and the up colour for an upward trend', () => {
		const {container} = render(
			<TrendIndicator direction="up" value={22.5} />
		);

		expect(container.querySelector('svg')).toBeInTheDocument();
		expect(container.firstChild).toHaveClass('charts-trend-indicator--up');
	});

	it('renders an arrow icon and the down colour for a downward trend', () => {
		const {container} = render(
			<TrendIndicator direction="down" value={8} />
		);

		expect(container.querySelector('svg')).toBeInTheDocument();
		expect(container.firstChild).toHaveClass(
			'charts-trend-indicator--down'
		);
	});

	it('renders a flat icon and the neutral colour for a neutral trend', () => {
		const {container} = render(
			<TrendIndicator direction="neutral" value={0} />
		);

		expect(container.querySelector('svg')).toBeInTheDocument();
		expect(container.firstChild).toHaveClass(
			'charts-trend-indicator--neutral'
		);
	});

	it('renders the optional label', () => {
		render(
			<TrendIndicator
				direction="up"
				label="versus previous period"
				value={22.5}
			/>
		);

		expect(screen.getByText('versus previous period')).toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<TrendIndicator direction="up" value={22.5} />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
