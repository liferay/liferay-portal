/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import {render, screen} from '@testing-library/react';
import React from 'react';

import InteractiveCard from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/components/InteractiveCard';

const trend = {
	classification: TrendClassification.Positive,
	percentage: 22.5,
};

const renderComponent = (props: React.ComponentProps<typeof InteractiveCard>) =>
	render(<InteractiveCard {...props} />);

describe('InteractiveCard', () => {
	it('renders the title and value when not loading', () => {
		renderComponent({
			color: 'purple',
			icon: 'low-vision',
			title: 'Impressions',
			trend,
			value: '31.9k',
		});

		expect(screen.getByText('Impressions')).toBeInTheDocument();
		expect(screen.getByText('31.9k')).toBeInTheDocument();
	});

	it('hides the value while loading', () => {
		renderComponent({
			color: 'purple',
			icon: 'low-vision',
			loading: true,
			title: 'Impressions',
			trend,
			value: '31.9k',
		});

		expect(screen.getByText('Impressions')).toBeInTheDocument();
		expect(screen.queryByText('31.9k')).not.toBeInTheDocument();
	});

	it('marks the card as active', () => {
		renderComponent({
			active: true,
			color: 'dark',
			icon: 'view',
			title: 'Views',
			trend,
			value: '18.1k',
		});

		expect(screen.getByRole('button')).toHaveClass('active');
	});
});
