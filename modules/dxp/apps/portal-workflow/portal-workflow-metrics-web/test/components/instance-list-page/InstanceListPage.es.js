/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React from 'react';

import InstanceListPage from '../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPage.es';
import ToasterProvider from '../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const items = [
	{
		assetTitle: 'New Post 1',
		assetType: 'Blog',
		assignees: [{id: -1, name: 'Unassigned', reviewer: true}],
		dateCreated: new Date('2019-01-01'),
		id: 1,
		taskNames: [],
	},
	{
		assetTitle: 'New Post 2',
		assetType: 'Blog',
		assignees: [{id: -1, name: 'Unassigned', reviewer: true}],
		creator: {
			name: 'User 1',
		},
		dateCreated: new Date('2019-01-03'),
		id: 2,
		taskNames: ['Update'],
	},
];

const routeParams = {
	page: 1,
	pageSize: 2,
	query: '',
	sort: 'overdueInstanceCount%3Adesc',
};

fetch.mockImplementation(async () => ({
	json: async () => ({items, totalCount: items.length + 1}),
	ok: true,
	text: async () => ({items, totalCount: items.length + 1}),
}));

describe('The instance list card should', () => {
	let container;
	let findByText;
	let getByText;

	beforeAll(async () => {
		const renderResult = render(
			<MockRouter>
				<InstanceListPage routeParams={routeParams} />
			</MockRouter>,
			{wrapper: ToasterProvider}
		);

		container = renderResult.container;
		findByText = renderResult.findByText;
		getByText = renderResult.getByText;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with "sla-status", "process-status", "completion-period", "process-step" and "assignee" filters', () => {
		const filters = container.querySelectorAll('.dropdown-toggle');

		expect(filters[0]).toHaveTextContent('sla-status');
		expect(filters[1]).toHaveTextContent('process-status');
		expect(filters[2]).toHaveTextContent('completion-period');
		expect(filters[3]).toHaveTextContent('process-step');
		expect(filters[4]).toHaveTextContent('assignee');
	});

	it('Select all page by clicking on check all button', async () => {
		const checkAllButton = container.querySelectorAll(
			'input.custom-control-input'
		)[0];
		const firstTableElements = container.querySelectorAll(
			'.table-first-element-group'
		);

		const instanceCheckbox1 = firstTableElements[0]?.querySelector(
			'input.custom-control-input'
		);
		const instanceCheckbox2 = firstTableElements[1]?.querySelector(
			'input.custom-control-input'
		);

		expect(checkAllButton.checked).toEqual(false);
		expect(instanceCheckbox1.checked).toEqual(false);
		expect(instanceCheckbox2.checked).toEqual(false);

		fireEvent.click(checkAllButton);

		await act(async () => {
			jest.runAllTimers();
		});

		const label = getByText('x-of-x-selected');

		expect(checkAllButton.checked).toEqual(true);
		expect(label).toBeTruthy();
		expect(instanceCheckbox1.checked).toEqual(true);
		expect(instanceCheckbox2.checked).toEqual(true);

		fireEvent.click(checkAllButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(checkAllButton.checked).toEqual(false);
		expect(instanceCheckbox1.checked).toEqual(false);
		expect(instanceCheckbox2.checked).toEqual(false);
	});

	it('Select all instances by clicking on select all button', async () => {
		const checkAllButton = container.querySelectorAll(
			'input.custom-control-input'
		)[0];
		const firstTableElements = container.querySelectorAll(
			'.table-first-element-group'
		);

		const instanceCheckbox1 = firstTableElements[0]?.querySelector(
			'input.custom-control-input'
		);
		const instanceCheckbox2 = firstTableElements[1]?.querySelector(
			'input.custom-control-input'
		);

		expect(checkAllButton.checked).toEqual(false);
		expect(instanceCheckbox1.checked).toEqual(false);
		expect(instanceCheckbox2.checked).toEqual(false);

		fireEvent.click(instanceCheckbox1);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(checkAllButton.checked).toEqual(false);
		expect(instanceCheckbox1.checked).toEqual(true);
		expect(instanceCheckbox2.checked).toEqual(false);

		const clearButton = getByText('clear');

		fireEvent.click(clearButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(checkAllButton.checked).toEqual(false);
		expect(instanceCheckbox1.checked).toEqual(false);
		expect(instanceCheckbox2.checked).toEqual(false);

		fireEvent.click(checkAllButton);

		let label = getByText('x-of-x-selected');

		expect(checkAllButton.checked).toEqual(true);
		expect(label).toBeTruthy();
		expect(instanceCheckbox1.checked).toEqual(true);
		expect(instanceCheckbox2.checked).toEqual(true);

		const selectAllButton = getByText('select-all');

		fireEvent.click(selectAllButton);

		await act(async () => {
			jest.runAllTimers();
		});

		label = getByText('all-selected');

		expect(label).toBeTruthy();
	});

	xit('Show last metrics calculated info', async () => {
		const metricsCalculated = await findByText('metrics-calculated');

		expect(metricsCalculated).toBeTruthy();
	});
});
