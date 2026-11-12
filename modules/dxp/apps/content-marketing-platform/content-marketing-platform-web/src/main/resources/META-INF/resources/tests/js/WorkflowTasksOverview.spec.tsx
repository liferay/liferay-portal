/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import WorkflowTasksOverview from '../../js/components/task/WorkflowTasksOverview';
import {mockFetch} from '../js/__mocks__/frontend-js-web';

const mockSetWorkflowTasksFDSState = jest.fn();
const mockUseLiferayState = jest.fn();

jest.mock('@liferay/frontend-js-state-web/react', () => ({
	useLiferayState: (...args: any[]) => mockUseLiferayState(...args),
}));

function mockWorkflowTasksResponse(totalCount: number) {
	return {
		json: async () => ({totalCount}),
		ok: true,
	} as Response;
}

describe('WorkflowTasksOverview', () => {
	const eventHandlers: Record<string, Function[]> = {};

	beforeEach(() => {
		jest.clearAllMocks();

		Object.keys(eventHandlers).forEach((key) => {
			delete eventHandlers[key];
		});

		(global as any).Liferay.on = jest.fn(
			(event: string, handler: Function) => {
				if (!eventHandlers[event]) {
					eventHandlers[event] = [];
				}

				eventHandlers[event].push(handler);
			}
		);

		(global as any).Liferay.detach = jest.fn(
			(event: string, handler: Function) => {
				if (eventHandlers[event]) {
					eventHandlers[event] = eventHandlers[event].filter(
						(h) => h !== handler
					);
				}
			}
		);

		(global as any).Liferay.fire = jest.fn(
			(event: string, data?: unknown) => {
				(eventHandlers[event] || []).forEach((fn) => fn(data));
			}
		);

		mockUseLiferayState.mockReturnValue([
			{
				filters: [
					{
						active: false,
						id: 'completed',
						selectedData: {
							exclude: false,
							selectedItems: [],
						},
					},
					{
						active: true,
						id: 'assetType',
						selectedData: {
							exclude: false,
							selectedItems: [
								{
									label: 'Blog',
									value: 'blog',
								},
							],
						},
					},
				],
				search: {
					query: '',
				},
			},
			mockSetWorkflowTasksFDSState,
		]);
	});

	it('clicking quick filters updates the completed filter in the data set state', async () => {
		mockFetch
			.mockResolvedValueOnce(mockWorkflowTasksResponse(20))
			.mockResolvedValueOnce(mockWorkflowTasksResponse(30));

		const {getByText} = render(
			<WorkflowTasksOverview filterURL="/o/search/v1.0/search?filter=test" />
		);

		await waitFor(() => getByText('pending'));

		fireEvent.click(getByText('pending').closest('button') as HTMLElement);

		expect(mockSetWorkflowTasksFDSState).toHaveBeenCalledWith(
			expect.objectContaining({
				filters: [
					expect.objectContaining({
						active: true,
						id: 'completed',
						selectedData: {
							exclude: false,
							selectedItems: [
								{
									label: 'pending',
									value: 'false',
								},
							],
						},
					}),
					expect.objectContaining({
						active: false,
						id: 'assetType',
						selectedData: {
							exclude: false,
							selectedItems: [],
						},
					}),
				],
			})
		);

		mockSetWorkflowTasksFDSState.mockClear();

		fireEvent.click(
			getByText('completed').closest('button') as HTMLElement
		);

		expect(mockSetWorkflowTasksFDSState).toHaveBeenCalledWith(
			expect.objectContaining({
				filters: [
					expect.objectContaining({
						active: true,
						id: 'completed',
						selectedData: {
							exclude: false,
							selectedItems: [
								{
									label: 'completed',
									value: 'true',
								},
							],
						},
					}),
					expect.objectContaining({
						active: false,
						id: 'assetType',
						selectedData: {
							exclude: false,
							selectedItems: [],
						},
					}),
				],
			})
		);
	});

	it('refetches counts when the data set display is updated', async () => {
		mockFetch
			.mockResolvedValueOnce(mockWorkflowTasksResponse(20))
			.mockResolvedValueOnce(mockWorkflowTasksResponse(30))
			.mockResolvedValueOnce(mockWorkflowTasksResponse(21))
			.mockResolvedValueOnce(mockWorkflowTasksResponse(31));

		const {getByText} = render(
			<WorkflowTasksOverview filterURL="/o/search/v1.0/search?filter=test" />
		);

		await waitFor(() => getByText('30'));

		(global as any).Liferay.fire(FDS_EVENT.DISPLAY_UPDATED, {});

		await waitFor(() => {
			expect(getByText('31')).toBeInTheDocument();
		});
	});

	it('renders nothing and skips the fetch when filterURL is missing', async () => {
		const {container} = render(<WorkflowTasksOverview />);

		await waitFor(() => {
			expect(container.firstChild).toBeNull();
		});

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('renders pending and completed counts', async () => {
		mockFetch
			.mockResolvedValueOnce(mockWorkflowTasksResponse(20))
			.mockResolvedValueOnce(mockWorkflowTasksResponse(30));

		const {getByText} = render(
			<WorkflowTasksOverview filterURL="/o/search/v1.0/search?filter=test" />
		);

		await waitFor(() => {
			expect(getByText('30')).toBeInTheDocument();
			expect(getByText('pending')).toBeInTheDocument();
			expect(getByText('20')).toBeInTheDocument();
			expect(getByText('completed')).toBeInTheDocument();
		});

		expect(mockFetch).toHaveBeenCalledWith(
			'/o/search/v1.0/search?filter=test&filter=completed eq true&pageSize=1'
		);
		expect(mockFetch).toHaveBeenCalledWith(
			'/o/search/v1.0/search?filter=test&filter=completed eq false&pageSize=1'
		);
	});

	it('returns null when API fetch fails', async () => {
		mockFetch.mockRejectedValueOnce(new Error('network error'));

		const {container} = render(
			<WorkflowTasksOverview filterURL="/o/search/v1.0/search?filter=test" />
		);

		await waitFor(() => {
			expect(container.firstChild).toBeNull();
		});
	});

	it('returns null when total count is zero', async () => {
		mockFetch
			.mockResolvedValueOnce(mockWorkflowTasksResponse(0))
			.mockResolvedValueOnce(mockWorkflowTasksResponse(0));

		const {container} = render(
			<WorkflowTasksOverview filterURL="/o/search/v1.0/search?filter=test" />
		);

		await waitFor(() => {
			expect(container.firstChild).toBeNull();
		});
	});

	it('shows loading indicator before fetch completes', () => {
		mockFetch.mockImplementation(() => new Promise(() => {}));

		const {container} = render(
			<WorkflowTasksOverview filterURL="/o/search/v1.0/search?filter=test" />
		);

		expect(
			container.querySelector('.loading-animation')
		).toBeInTheDocument();
	});
});
