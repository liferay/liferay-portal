/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {slaStatusConstants} from '../../../../src/main/resources/META-INF/resources/js/components/filter/SLAStatusFilter.es';
import SummaryCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/process-items/SummaryCard.es';
import {MockRouter} from '../../../mock/MockRouter.es';

describe('The SummaryCard component should', () => {
	let container;
	let getByText;
	let renderResult;

	const props = {
		getTitle: () => 'Overdue',
		iconColor: 'danger',
		iconName: 'exclamation-circle',
		processId: 12345,
		slaStatusFilter: slaStatusConstants.overdue,
		total: false,
		totalValue: 858000,
		value: 156403,
	};

	beforeAll(async () => {
		cleanup();

		renderResult = render(
			<MockRouter>
				<SummaryCard {...props} />
			</MockRouter>
		);

		container = renderResult.container;
		getByText = renderResult.getByText;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Render correct icon and title', () => {
		const instanceIcon = container.querySelector(
			'.lexicon-icon-exclamation-circle'
		);
		const instanceTitle = getByText('Overdue');

		expect(instanceIcon).toBeTruthy();
		expect(instanceTitle).toBeTruthy();
	});

	it('Render formatted percentage', () => {
		const footer = container.querySelector('.footer');

		expect(footer).toHaveTextContent('18.23%');
	});

	it('Render formatted value for values with more than 3 digits', () => {
		const formattedValue = container.querySelector('.body');

		expect(formattedValue).toHaveTextContent('156.4K');
	});

	it('Show see items only when item is hovered', () => {
		const childLink = container.querySelector(
			'a.process-tabs-summary-card'
		);
		const footer = container.querySelector('.footer');

		fireEvent.mouseOver(childLink);

		expect(footer).toHaveTextContent('see-items');

		fireEvent.mouseOut(childLink);

		expect(footer).toHaveTextContent('18.23%');
	});
});

describe('The SummaryCard component should', () => {
	let container;
	let renderResult;

	const props = {
		completed: true,
		getTitle: () => 'Total',
		processId: 12345,
		timeRange: {
			dateEnd: '2019-12-09T00:00:00Z',
			dateStart: '2019-12-03T00:00:00Z',
			key: '7',
		},
		total: true,
		totalValue: 3500,
		value: 310,
	};

	beforeAll(() => {
		cleanup();

		renderResult = render(
			<MockRouter>
				<SummaryCard {...props} />
			</MockRouter>
		);

		container = renderResult.container;
	});

	test('Not render formatted percentage for total item', () => {
		const footer = container.querySelector('.footer');

		expect(footer).toHaveTextContent('');
	});

	test('Not render formatted value for values with 3 or less digits', () => {
		const formattedValue = container.querySelector('.body');

		expect(formattedValue).toHaveTextContent('310');
	});

	test('Render with correct link', () => {
		const childLink = container.querySelector(
			'a.process-tabs-summary-card'
		);

		const href = childLink.getAttribute('href');

		expect(href).toContain('/instance/12345/20/1');
		expect(href).toContain(
			'filters.statuses%5B0%5D=Completed&filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7'
		);
	});
});

describe('The SummaryCard component should', () => {
	let container;
	let renderResult;

	const props = {
		getTitle: () => 'On Time',
		iconColor: 'success',
		iconName: 'check-circle',
		processId: 12345,
		slaStatusFilter: slaStatusConstants.ontime,
		total: false,
		totalValue: 55,
		value: undefined,
	};

	beforeAll(() => {
		cleanup();

		renderResult = render(
			<MockRouter>
				<SummaryCard {...props} />
			</MockRouter>
		);

		container = renderResult.container;
	});

	test('Render component with disabled state', () => {
		const childLink = container.querySelector(
			'a.process-tabs-summary-card'
		);

		expect([childLink.classList][0]).toContain('disabled');
	});
});
