/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render, screen} from '@testing-library/react';
import {fetch} from 'frontend-js-web';
import React from 'react';

import TasksOverview from '../../js/components/task/TasksOverview';

describe('TasksOverview', () => {
	afterEach(cleanup);

	it('renders the appropriate counts', async () => {
		(fetch as jest.Mock)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({completionRate: 50}),
				ok: true,
			})
			.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({
						blockedCount: 1,
						inProgressCount: 2,
						overdueCount: 3,
						totalCount: 4,
					}),
				ok: true,
			});

		await act(async () => {
			render(
				<TasksOverview
					hasAddTaskPermission
					projectId="123"
					redirect="/redirect-url"
				/>
			);
		});

		expect(fetch).toHaveBeenCalledWith('/o/cmp/projects/123');
		expect(fetch).toHaveBeenCalledWith(
			'/o/headless-cmp/v1.0/projects/123/task-statistics/'
		);

		expect(screen.getByText('1')).toBeInTheDocument();
		expect(screen.getByText('2')).toBeInTheDocument();
		expect(screen.getByText('3')).toBeInTheDocument();
		expect(screen.getByText('4')).toBeInTheDocument();
		expect(screen.getByText('50%')).toBeInTheDocument();
	});

	it('renders empty state when totalCount is 0', async () => {
		(fetch as jest.Mock)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({completionRate: 0}),
				ok: true,
			})
			.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({
						blockedCount: 0,
						inProgressCount: 0,
						overdueCount: 0,
						totalCount: 0,
					}),
				ok: true,
			});

		await act(async () => {
			render(
				<TasksOverview
					hasAddTaskPermission
					projectId="123"
					redirect="/redirect-url"
				/>
			);
		});

		expect(fetch).toHaveBeenCalledWith('/o/cmp/projects/123');
		expect(fetch).toHaveBeenCalledWith(
			'/o/headless-cmp/v1.0/projects/123/task-statistics/'
		);

		expect(screen.getByText('no-tasks')).toBeInTheDocument();

		expect(screen.getByText('no-tasks')).toBeInTheDocument();
		expect(
			screen.getByText('add-a-tasks-to-start-tracking-work')
		).toBeInTheDocument();
		expect(screen.getByText('new-task')).toBeInTheDocument();

		expect(screen.queryByText('tasks-overview')).not.toBeInTheDocument();
		expect(screen.queryByText('total-tasks')).not.toBeInTheDocument();
	});

	it('hides the new task button on the empty state when the user lacks add task permission', async () => {
		(fetch as jest.Mock)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({completionRate: 0}),
				ok: true,
			})
			.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({
						blockedCount: 0,
						inProgressCount: 0,
						overdueCount: 0,
						totalCount: 0,
					}),
				ok: true,
			});

		await act(async () => {
			render(
				<TasksOverview
					hasAddTaskPermission={false}
					projectId="123"
					redirect="/redirect-url"
				/>
			);
		});

		expect(screen.getByText('no-tasks')).toBeInTheDocument();
		expect(screen.queryByText('new-task')).not.toBeInTheDocument();
	});
});
