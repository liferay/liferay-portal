/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {MetricValue} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/MetricValue';

const renderComponent = (props: React.ComponentProps<typeof MetricValue>) =>
	render(<MetricValue {...props} />);

describe('MetricValue', () => {
	it('renders the value and the positive trend percentage', () => {
		renderComponent({
			trend: {
				classification: TrendClassification.Positive,
				percentage: 22.5,
			},
			value: '31.9k',
		});

		expect(screen.getByText('31.9k')).toBeInTheDocument();
		expect(screen.getByText('22.5%')).toBeInTheDocument();
	});

	it('renders the absolute percentage for a negative trend', () => {
		renderComponent({
			trend: {
				classification: TrendClassification.Negative,
				percentage: -5.6,
			},
			value: '1.2k',
		});

		expect(screen.getByText('5.6%')).toBeInTheDocument();
	});
});
