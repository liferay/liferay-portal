/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent} from '@testing-library/react';

// import React from 'react';

import '@testing-library/jest-dom';

// import {
// 	ALL_INDEXES_KEY,
// 	METRIC_INDEXES_KEY,
// 	SLA_INDEXES_KEY,
// } from '../../../../src/main/resources/META-INF/resources/js/components/settings/indexes-page/IndexesConstants.es';
// import IndexesPage from '../../../../src/main/resources/META-INF/resources/js/components/settings/indexes-page/IndexesPage.es';
// import ToasterProvider from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
// import {MockRouter} from '../../../mock/MockRouter.es';

xdescribe('The IndexesPage component should', () => {
	xdescribe('Render and dispatch actions', () => {

		// jest.runAllTimers();

		let container;
		let indexesItems;
		let getAllByRole;
		let getAllByText;
		let getByText;

		// const items = [
		// 	{
		// 		group: SLA_INDEXES_KEY,
		// 		key: 'sla-results',
		// 		label: 'sla-results',
		// 	},
		// 	{
		// 		group: METRIC_INDEXES_KEY,
		// 		key: 'metrics-instances',
		// 		label: 'metrics-instances',
		// 	},
		// ];

		// const clientMock = {
		// 	get: jest
		// 		.fn()
		// 		.mockResolvedValueOnce({data: {items}})
		// 		.mockResolvedValue({data: {items: []}}),
		// 	patch: jest.fn().mockRejectedValueOnce().mockResolvedValue(),
		// };

		// beforeAll(async () => {
		// 	jest.useFakeTimers();

		// 	const renderResult = render(
		// 		<MockRouter client={clientMock}>
		// 			<ToasterProvider>
		// 				<IndexesPage />
		// 			</ToasterProvider>
		// 		</MockRouter>
		// 	);

		// 	container = renderResult.container;
		// 	getAllByRole = renderResult.getAllByRole;
		// 	getAllByText = renderResult.getAllByText;
		// 	getByText = renderResult.getByText;

		// 	window.location.hash = '/settings/indexes';

		// 	await act(async () => {
		// 		jest.runAllTimers();
		// 	});
		// });

		xit('Render information correctly ', async () => {
			const pageTitle = getByText('workflow-index-actions');
			const reindexAllLabel = getByText('workflow-indexes');
			const reindexAllBtn = getAllByText('reindex-all')[0];

			expect(pageTitle).toBeTruthy();
			expect(reindexAllLabel).toBeTruthy();
			expect(reindexAllBtn).toBeTruthy();

			fireEvent.click(reindexAllBtn);

			await act(async () => {
				jest.runAllTimers();
			});
		});

		xit('Render error toast when dispatch any reindex action with error', () => {
			const alertToast = getByText('your-request-has-failed');
			const alertClose = container.querySelector('button.close');

			expect(alertToast).toBeTruthy();

			fireEvent.click(alertClose);
		});

		xit('Render with items correctly', () => {
			indexesItems = getAllByRole('listitem');

			expect(indexesItems[1]).toHaveTextContent('metrics');
			expect(indexesItems[2]).toHaveTextContent('reindex-all');
			expect(indexesItems[3]).toHaveTextContent('reindex');
			expect(indexesItems[2]).toHaveTextContent(
				'workflow-metrics-indexes'
			);
			expect(indexesItems[3]).toHaveTextContent('metrics-instances');
			expect(indexesItems[4]).toHaveTextContent('slas');
			expect(indexesItems[5]).toHaveTextContent('reindex-all');
			expect(indexesItems[6]).toHaveTextContent('reindex');
			expect(indexesItems[5]).toHaveTextContent('workflow-sla-indexes');
			expect(indexesItems[6]).toHaveTextContent('sla-results');
			expect(indexesItems[2].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[3].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[5].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[6].children[1].children[0]).not.toBeDisabled();
		});

		xit('Render progress status when dispatch any reindex action', async () => {
			const reindexAllBtn = getAllByText('reindex-all')[0];

			fireEvent.click(reindexAllBtn);

			await act(async () => {
				jest.runAllTimers();
			});

			const reindexAllStatus =
				container.querySelectorAll('.progress-group')[0];

			expect(indexesItems[2].children[1].children[0]).toBeDisabled();
			expect(indexesItems[3].children[1].children[0]).toBeDisabled();
			expect(indexesItems[5].children[1].children[0]).toBeDisabled();
			expect(indexesItems[6].children[1].children[0]).toBeDisabled();

			expect(reindexAllStatus.children[1]).toHaveTextContent('0%');

			await act(async () => {
				jest.runOnlyPendingTimers();
			});

			let alertToast = getByText('all-x-have-reindexed-successfully');

			expect(alertToast).toBeTruthy();
			expect(indexesItems[2].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[3].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[5].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[6].children[1].children[0]).not.toBeDisabled();

			await act(async () => {
				jest.runOnlyPendingTimers();
			});

			const alertContainer = document.querySelector('.alert-container');
			expect(alertContainer.children[0].children.length).toBe(0);

			await fireEvent.click(indexesItems[2].children[1].children[0]);

			expect(indexesItems[3].children[1].children[0]).toBeDisabled();
			expect(indexesItems[5].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[6].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[2]).toHaveTextContent('0%');

			await act(async () => {
				jest.runOnlyPendingTimers();
			});

			alertToast = getByText('all-x-have-reindexed-successfully');

			expect(alertToast).toBeTruthy();
			expect(indexesItems[2].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[3].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[5].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[6].children[1].children[0]).not.toBeDisabled();

			await fireEvent.click(indexesItems[5].children[1].children[0]);

			expect(indexesItems[6].children[1].children[0]).toBeDisabled();
			expect(indexesItems[2].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[3].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[5]).toHaveTextContent('0%');

			await act(async () => {
				jest.runOnlyPendingTimers();
			});

			alertToast = getByText('all-x-have-reindexed-successfully');

			expect(alertToast).toBeTruthy();
			expect(indexesItems[2].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[3].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[5].children[1].children[0]).not.toBeDisabled();
			expect(indexesItems[6].children[1].children[0]).not.toBeDisabled();
		});
	});

	xdescribe('Render loading reindexes', () => {

		// jest.runAllTimers();

		let container;
		let getAllByRole;

		// const items = [
		// 	{
		// 		group: SLA_INDEXES_KEY,
		// 		key: 'sla-results',
		// 		label: 'sla-results',
		// 	},
		// 	{
		// 		group: METRIC_INDEXES_KEY,
		// 		key: 'metrics-instances',
		// 		label: 'metrics-instances',
		// 	},
		// ];

		// const clientMock = {
		// 	get: jest.fn().mockResolvedValue({
		// 		data: {items},
		// 	}),
		// };

		// const mockStatuses = [
		// 	{
		// 		completionPercentage: 50,
		// 		key: ALL_INDEXES_KEY,
		// 	},
		// 	{
		// 		completionPercentage: 20,
		// 		key: METRIC_INDEXES_KEY,
		// 	},
		// 	{
		// 		completionPercentage: 40,
		// 		key: 'metrics-instances',
		// 	},
		// 	{
		// 		completionPercentage: 60,
		// 		key: SLA_INDEXES_KEY,
		// 	},
		// 	{
		// 		completionPercentage: 80,
		// 		key: 'sla-results',
		// 	},
		// ];

		// beforeAll(async () => {
		// 	cleanup();

		// 	const renderResult = render(
		// 		<MockRouter
		// 			client={clientMock}
		// 			initialReindexStatuses={mockStatuses}
		// 		>
		// 			<ToasterProvider>
		// 				<IndexesPage />
		// 			</ToasterProvider>
		// 		</MockRouter>
		// 	);

		// 	container = renderResult.container;
		// 	getAllByRole = renderResult.getAllByRole;

		// 	await act(async () => {
		// 		jest.runAllTimers();
		// 	});
		// });

		xit('Render all indexes on progress', () => {
			const indexActions = getAllByRole('listitem');

			const reindexAllStatus =
				container.querySelectorAll('.progress-group')[0];

			expect(reindexAllStatus.children[1]).toHaveTextContent('50%');

			expect(indexActions[2]).toHaveTextContent('0%');
			expect(indexActions[2]).toHaveTextContent('20%');
			expect(indexActions[3]).toHaveTextContent('40%');
			expect(indexActions[5]).toHaveTextContent('60%');
			expect(indexActions[6]).toHaveTextContent('80%');
		});
	});
});
