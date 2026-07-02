/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import PieChartSlice from '../../../src/main/resources/META-INF/resources/js/pie_chart/components/PieChartSlice';

const DEFAULT_PROPS = {
	color: '#000000',
	d: 'M 0 0',
	datum: {label: 'Alpha', value: 1},
	index: 0,
	isActive: false,
	onBlur: () => {},
	onFocus: () => {},
	onHover: () => {},
	onHoverEnd: () => {},
	onKeyDown: () => {},
	percent: 100,
	sliceRef: () => {},
};

function renderSlice(props = {}) {
	return render(
		<svg>
			<PieChartSlice {...DEFAULT_PROPS} {...props} />
		</svg>
	);
}

describe('PieChartSlice', () => {
	it('renders a path using the given d', () => {
		renderSlice({d: 'M 1 1'});

		expect(screen.getByRole('img')).toHaveAttribute('d', 'M 1 1');
	});
});
