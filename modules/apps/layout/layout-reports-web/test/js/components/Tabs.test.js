/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import Tabs from '../../../src/main/resources/META-INF/resources/js/components/Tabs';

import '@testing-library/jest-dom';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/render_times/RenderTimes',
	() => jest.fn(() => <div />)
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/layout_reports/LayoutReports',
	() => jest.fn(() => <div />)
);

const mockTabs = [
	{
		id: 'page-speed-insights',
		name: 'Page Speed',
		url: 'url',
	},
	{
		id: 'performance',
		name: 'Performance',
		url: 'url',
	},
];

describe('Tabs', () => {
	it('renders tabs', async () => {
		render(<Tabs segments={{}} tabs={mockTabs} />);

		expect(screen.getByText('Performance')).toBeInTheDocument();
		expect(screen.getByText('Page Speed')).toBeInTheDocument();
	});

	it('sets correctly the active tab', () => {
		const setActiveTab = jest.fn(() => {});
		render(
			<Tabs
				activeTab={1}
				segments={{}}
				setActiveTab={setActiveTab}
				tabs={mockTabs}
			/>
		);

		const activeTab = screen.getByText('Performance');
		expect(activeTab.className).toContain('active');
	});
});
