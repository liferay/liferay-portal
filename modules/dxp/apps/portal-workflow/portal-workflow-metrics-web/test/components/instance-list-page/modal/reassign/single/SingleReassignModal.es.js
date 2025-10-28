/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {InstanceListContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
import {ModalContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import SingleReassignModal from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/reassign/single/SingleReassignModal.es';
import ToasterProvider from '../../../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../../../../mock/MockRouter.es';
import FetchMock, {fetchMockResponse} from '../../../../../mock/fetch.es';

import '@testing-library/jest-dom';

const ContainerMock = ({children}) => {
	const selectedInstance = {
		assetTitle: 'Blog1',
		assetType: 'Blogs Entry',
		assignees: [{id: 2, name: 'Test Test'}],
		id: 1,
		status: 'In Progress',
		taskNames: ['Review'],
	};

	return (
		<InstanceListContext.Provider
			value={{
				selectedInstance,
			}}
		>
			<ModalContext.Provider value={{visibleModal: 'singleReassign'}}>
				<ToasterProvider>{children}</ToasterProvider>
			</ModalContext.Provider>
		</InstanceListContext.Provider>
	);
};

describe('The SingleReassignModal component should', () => {
	let getByText;

	const items = [
		{
			id: 1,
			name: '0test test0',
		},
	];

	const fetchMock = new FetchMock({
		GET: {
			default: [
				fetchMockResponse({}, false),
				fetchMockResponse({
					items: [
						{
							assigneePerson: {id: 2, name: 'Test Test'},
							id: 1,
							label: 'Review',
							objectReviewed: {
								assetTitle: 'Blog1',
								assetType: 'Blogs Entry',
							},
							status: 'In Progress',
							workflowInstanceId: 1,
						},
					],
					totalCount: items.length,
				}),
				fetchMockResponse({items}),
			],
		},
		POST: {
			default: [
				fetchMockResponse(new Error('Request failed'), false),
				fetchMockResponse({items: []}),
			],
		},
	});

	beforeAll(async () => {
		const renderResult = render(
			<MockRouter>
				<SingleReassignModal />
			</MockRouter>,
			{
				wrapper: ContainerMock,
			}
		);

		getByText = renderResult.getByText;

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

	it('Render modal with error message and retry', async () => {
		const alertError = getByText('your-request-has-failed');
		const emptyStateMessage = getByText('unable-to-retrieve-data');
		const retryBtn = getByText('retry');

		expect(alertError).toBeTruthy();
		expect(emptyStateMessage).toBeTruthy();

		fireEvent.click(retryBtn);

		await act(async () => {
			jest.advanceTimersByTime(100);
		});
	});

	it('Render modal with items', async () => {
		const cancelBtn = getByText('cancel');
		const reassignBtn = getByText('reassign');
		const table = document.querySelector('.table');

		const item = table.children[1].children[0];

		expect(cancelBtn).not.toHaveAttribute('disabled');
		expect(reassignBtn).toHaveAttribute('disabled');
		expect(item.children[0]).toHaveTextContent('1');
		expect(item.children[1]).toHaveTextContent('Blogs Entry: Blog1');
		expect(item.children[2]).toHaveTextContent('Review');
		expect(item.children[3]).toHaveTextContent('Test Test');

		const autocompleteInput = document.querySelector('input.form-control');

		fireEvent.change(autocompleteInput, {target: {value: 'test'}});

		fireEvent.mouseDown(document.querySelector('.dropdown-item'));

		expect(reassignBtn).not.toHaveAttribute('disabled');

		fireEvent.click(reassignBtn);

		await act(async () => {
			jest.advanceTimersByTime(100);
		});
	});

	it('Render modal reassign error and retry', async () => {
		const alertError = getByText('your-request-has-failed');
		const reassignBtn = getByText('reassign');

		expect(alertError).toBeTruthy();
		expect(reassignBtn).not.toHaveAttribute('disabled');

		fireEvent.click(reassignBtn);

		await act(async () => {
			jest.advanceTimersByTime(100);
		});
	});

	it('Render alert with success message and close modal', async () => {
		const alertToast = document.querySelector('.alert-dismissible');

		const alertClose = alertToast.children[1];

		expect(alertToast).toHaveTextContent('this-task-has-been-reassigned');

		fireEvent.click(alertClose);

		const alertContainer = document.querySelector('.alert-container');
		expect(alertContainer.children[0].children.length).toBe(0);
	});
});
