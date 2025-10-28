/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import App from '../../src/main/resources/META-INF/resources/js/components/App.es';
import FetchMock, {fetchMockResponse} from '../mock/fetch.es';

const processItems = [
	{
		instancesCount: 5,
		process: {
			id: 1234,
			title: 'Single Approver',
		},
	},
];

const pending = {
	instanceCount: 0,
	onTimeInstanceCount: 0,
	overdueInstanceCount: 0,
	process: {
		id: 1234,
		title: 'Single Approver',
	},
	untrackedInstanceCount: 0,
};

const fetchMock = new FetchMock({
	GET: {
		'/o/portal-workflow-metrics/v1.0/processes/metrics': fetchMockResponse({
			items: processItems,
			totalCount: processItems.length,
		}),

		// eslint-disable-next-line sort-keys
		'/o/portal-workflow-metrics/v1.0/indexes': fetchMockResponse({
			items: [],
			totalCount: 0,
		}),
		'/o/portal-workflow-metrics/v1.0/processes/1234/metrics':
			fetchMockResponse(pending),
		'default': fetchMockResponse({items: [], totalCount: 0}),
	},
});

const mockProps = {
	companyId: 12345,
	defaultDelta: 20,
	deltaValues: [5, 10, 20, 30, 50, 75],
	isAmPm: false,
	maxPages: 15,
	portletNamespace: '_workflow_',
	reindexStatuses: [],
};

describe('The App component should', () => {
	let container;
	let findByText;
	let getByText;

	beforeAll(async () => {
		const header = document.createElement('div');

		header.id = '_workflow_controlMenu';
		header.innerHTML = `<div class="sites-control-group"><ul class="control-menu-nav"></ul></div><div class="user-control-group"><ul class="control-menu-nav"><li></li></ul></div>`;

		document.body.appendChild(header);

		const renderResult = render(<App {...mockProps} />);

		container = renderResult.container;
		getByText = renderResult.getByText;
		findByText = renderResult.findByText;

		await act(async () => {
			jest.advanceTimersByTime(100);
		});
	});

	beforeEach(() => {
		fetchMock.mock();
	});

	afterEach(() => {
		fetchMock.reset();
	});

	it('Navigate to settings indexes page', async () => {
		const kebabButton =
			document.getElementById('headerKebab').children[0].children[0]
				.children[0];

		fireEvent.click(kebabButton);

		const dropDownItems = document.querySelectorAll('.dropdown-item');

		expect(dropDownItems[0]).toHaveTextContent('settings');

		fireEvent.click(dropDownItems[0]);

		expect(window.location.hash).toContain('#/settings/indexes');

		fireEvent.click(document.getElementById('backButton').children[0]);

		await act(async () => {
			jest.advanceTimersByTime(100);
		});
	});

	it('Return to process list page', async () => {
		const processName = container.querySelectorAll('.table-title');

		const processNameLink = processName[0].children[0];

		expect(processNameLink).toHaveTextContent('Single Approver');
		expect(window.location.hash).toContain('#/processes');

		fireEvent.click(processNameLink);

		await act(async () => {
			jest.advanceTimersByTime(100);
		});
	});

	xit('Render the process metrics page on dashboard tab', async () => {
		expect(window.location.hash).toContain(
			'#/metrics/1234/dashboard/20/1/overdueInstanceCount%3Aasc'
		);

		const tabs = container.querySelectorAll('a.nav-link');
		const metricsCalculated = await findByText('SLA Metrics calculated');

		expect(tabs[0]).toHaveTextContent('dashboard');
		expect(tabs[0].className.includes('active')).toBe(true);
		expect(tabs[1]).toHaveTextContent('performance');

		expect(window.location.hash).toContain(
			'#/metrics/1234/dashboard/20/1/overdueInstanceCount%3Aasc'
		);

		expect(metricsCalculated).toBeTruthy();

		fireEvent.click(tabs[1]);
	});

	xit('Render the process metrics page on performance tab and back to dashboard', async () => {
		const metricsCalculated = await findByText('SLA Metrics calculated');
		const tabs = container.querySelectorAll('a.nav-link');

		expect(tabs[0]).toHaveTextContent('dashboard');
		expect(tabs[1]).toHaveTextContent('performance');
		expect(tabs[1].className.includes('active')).toBe(true);

		expect(window.location.hash).toContain('#/metrics/1234/performance');

		expect(metricsCalculated).toBeTruthy();

		fireEvent.click(tabs[0]);

		expect(tabs[0].className.includes('active')).toBe(true);
		expect(window.location.hash).toContain('#/metrics/1234/dashboard');
	});

	it('Navigate to new SLA page', async () => {
		const slaInfoLink = getByText('add-a-new-sla');

		fireEvent.click(slaInfoLink);

		expect(window.location.hash).toContain('#/sla/1234/new');
	});
});
