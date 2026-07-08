/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import TasksQuickFilters from '../../js/components/task/TasksQuickFilters';

const mockSetTasksFDSState = jest.fn();

let mockTasksFDSState: any = {
	filters: [
		{active: false, id: 'cmpState', selectedData: {selectedItems: []}},
		{active: false, id: 'cmpDueDate', selectedData: {}},
	],
};

jest.mock('@liferay/frontend-js-state-web/react', () => ({
	useLiferayState: () => [mockTasksFDSState, mockSetTasksFDSState],
}));

describe('TasksQuickFilters', () => {
	beforeEach(() => {
		mockSetTasksFDSState.mockClear();

		mockTasksFDSState = {
			filters: [
				{
					active: false,
					id: 'cmpState',
					selectedData: {selectedItems: []},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		(fetch as jest.Mock).mockResolvedValue({
			json: () =>
				Promise.resolve({
					blockedCount: 1,
					inProgressCount: 2,
					overdueCount: 3,
					totalCount: 4,
				}),
			ok: true,
		});
	});

	afterEach(() => {
		cleanup();
		(fetch as jest.Mock).mockClear();
	});

	it('clears the quick filter button when the selection changes within the same filter', async () => {
		const {rerender} = render(<TasksQuickFilters />);

		await screen.findByText('blocked');

		await userEvent.click(
			screen.getByText('blocked').closest('button') as HTMLButtonElement
		);

		expect(screen.getByText('blocked').closest('button')).toHaveClass(
			'active'
		);

		// Simulate FDS applying the quick filter state (consumes isQuickFilterChangeRef)

		mockTasksFDSState = {
			...mockTasksFDSState,
			filters: [
				{
					active: true,
					id: 'cmpState',
					selectedData: {
						exclude: false,
						selectedItems: [{label: 'blocked', value: 'blocked'}],
					},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		await act(async () => {
			rerender(<TasksQuickFilters />);
		});

		// Simulate the State dropdown adding "done" to the selection while cmpState stays active

		mockTasksFDSState = {
			...mockTasksFDSState,
			filters: [
				{
					active: true,
					id: 'cmpState',
					selectedData: {
						exclude: false,
						selectedItems: [
							{label: 'blocked', value: 'blocked'},
							{label: 'done', value: 'done'},
						],
					},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		await act(async () => {
			rerender(<TasksQuickFilters />);
		});

		expect(screen.getByText('blocked').closest('button')).not.toHaveClass(
			'active'
		);
	});

	it('keeps the quick filter button active when an unrelated filter changes', async () => {
		const {rerender} = render(<TasksQuickFilters />);

		await screen.findByText('blocked');

		await userEvent.click(
			screen.getByText('blocked').closest('button') as HTMLButtonElement
		);

		expect(screen.getByText('blocked').closest('button')).toHaveClass(
			'active'
		);

		// Simulate FDS applying the quick filter state (consumes isQuickFilterChangeRef)

		mockTasksFDSState = {
			...mockTasksFDSState,
			filters: [
				{
					active: true,
					id: 'cmpState',
					selectedData: {
						exclude: false,
						selectedItems: [{label: 'blocked', value: 'blocked'}],
					},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		await act(async () => {
			rerender(<TasksQuickFilters />);
		});

		// Simulate a search that triggers a new filters reference but leaves cmpState active

		mockTasksFDSState = {
			...mockTasksFDSState,
			filters: [
				{
					active: true,
					id: 'cmpState',
					selectedData: {
						exclude: false,
						selectedItems: [{label: 'blocked', value: 'blocked'}],
					},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		await act(async () => {
			rerender(<TasksQuickFilters />);
		});

		expect(screen.getByText('blocked').closest('button')).toHaveClass(
			'active'
		);
	});

	it('keeps the total tasks button active when an unrelated filter changes', async () => {
		const {rerender} = render(<TasksQuickFilters />);

		await screen.findByText('total-tasks');

		await userEvent.click(
			screen
				.getByText('total-tasks')
				.closest('button') as HTMLButtonElement
		);

		expect(screen.getByText('total-tasks').closest('button')).toHaveClass(
			'active'
		);

		// Simulate FDS applying the quick filter state (consumes isQuickFilterChangeRef)

		mockTasksFDSState = {
			...mockTasksFDSState,
			filters: [
				{
					active: false,
					id: 'cmpState',
					selectedData: {exclude: false, selectedItems: []},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		await act(async () => {
			rerender(<TasksQuickFilters />);
		});

		// Simulate a search that adds a new filter reference but leaves cmpState and cmpDueDate inactive

		mockTasksFDSState = {
			...mockTasksFDSState,
			filters: [
				{
					active: false,
					id: 'cmpState',
					selectedData: {exclude: false, selectedItems: []},
				},
				{active: false, id: 'cmpDueDate', selectedData: {}},
			],
		};

		await act(async () => {
			rerender(<TasksQuickFilters />);
		});

		expect(screen.getByText('total-tasks').closest('button')).toHaveClass(
			'active'
		);
	});

	it('renders the appropriate counts from multiple API calls', async () => {
		await act(async () => {
			render(<TasksQuickFilters />);
		});

		expect(fetch).toHaveBeenCalledWith(
			'/o/headless-cmp/v1.0/task-statistics/',
			{method: 'GET'}
		);

		expect(screen.getByText('blocked').previousSibling).toHaveTextContent(
			'1'
		);
		expect(
			screen.getByText('in-progress').previousSibling
		).toHaveTextContent('2');
		expect(screen.getByText('overdue').previousSibling).toHaveTextContent(
			'3'
		);
		expect(
			screen.getByText('total-tasks').previousSibling
		).toHaveTextContent('4');
	});

	it('renders the appropriate counts when a projectId is provided', async () => {
		await act(async () => {
			render(<TasksQuickFilters projectId="123" />);
		});

		expect(fetch).toHaveBeenCalledWith(
			'/o/headless-cmp/v1.0/projects/123/task-statistics/',
			{
				method: 'GET',
			}
		);

		expect(screen.getByText('blocked').previousSibling).toHaveTextContent(
			'1'
		);
		expect(
			screen.getByText('in-progress').previousSibling
		).toHaveTextContent('2');
		expect(screen.getByText('overdue').previousSibling).toHaveTextContent(
			'3'
		);
		expect(
			screen.getByText('total-tasks').previousSibling
		).toHaveTextContent('4');
	});
});
