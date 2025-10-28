/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent} from '@testing-library/react';

// import React, {useState} from 'react';

// import {InstanceListContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
// import {ModalContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
// import SingleUpdateDueDateModal from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/update-due-date/SingleUpdateDueDateModal.es';
// import ToasterProvider from '../../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
// import {MockRouter} from '../../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

// const ContainerMock = ({children}) => {
// 	const selectedInstance = {
// 		assetTitle: 'Blog1',
// 		assetType: 'Blogs Entry',
// 		assignees: [{id: 2, name: 'Test Test'}],
// 		id: 1,
// 		status: 'In Progress',
// 		taskNames: ['Review'],
// 	};
// 	const [updateDueDate, setUpdateDueDate] = useState({
// 		comment: undefined,
// 		dueDate: undefined,
// 	});

// 	return (
// 		<InstanceListContext.Provider
// 			value={{
// 				selectedInstance,
// 			}}
// 		>
// 			<ModalContext.Provider
// 				value={{
// 					setUpdateDueDate,
// 					updateDueDate,
// 					visibleModal: 'updateDueDate',
// 				}}
// 			>
// 				<ToasterProvider>{children}</ToasterProvider>
// 			</ModalContext.Provider>
// 		</InstanceListContext.Provider>
// 	);
// };

describe('The SingleUpdateDueDateModal component should', () => {
	let getByPlaceholderText;
	let getByText;

	// const items = [{dateDue: '2020-02-01T10:00:00', id: 1}];

	// const clientMock = {
	// 	get: jest
	// 		.fn()
	// 		.mockRejectedValueOnce(new Error('Request failed'))
	// 		.mockResolvedValue({data: {items}}),
	// 	post: jest
	// 		.fn()
	// 		.mockRejectedValueOnce(new Error('Request failed'))
	// 		.mockResolvedValue({data: {items: []}}),
	// };

	// beforeAll(async () => {
	// 	const renderResult = render(
	// 		<MockRouter client={clientMock} isAmPm>
	// 			<SingleUpdateDueDateModal />
	// 		</MockRouter>,
	// 		{
	// 			wrapper: ContainerMock,
	// 		}
	// 	);

	// 	getByPlaceholderText = renderResult.getByPlaceholderText;
	// 	getByText = renderResult.getByText;

	// 	await act(async () => {
	// 		jest.runAllTimers();
	// 	});
	// });

	xit('Render modal with error message and retry', async () => {
		const alertError = getByText('your-request-has-failed');
		const emptyStateMessage = getByText('unable-to-retrieve-data');
		const retryBtn = getByText('retry');

		expect(alertError).toBeTruthy();
		expect(emptyStateMessage).toBeTruthy();

		fireEvent.click(retryBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render modal with form inputs with defaultValues', async () => {
		const cancelBtn = getByText('cancel');
		const commentInput = getByPlaceholderText('write-a-note');
		const dateInput = getByPlaceholderText('MM/DD/YYYY');
		const doneBtn = getByText('done');
		const timeInput = getByPlaceholderText('HH:mm am/pm');

		expect(dateInput.value).toBe('02/01/2020');
		expect(timeInput.value).toBe('10:00 AM');
		expect(commentInput.value).toBe('');
		expect(cancelBtn).not.toHaveAttribute('disabled');
		expect(doneBtn).not.toHaveAttribute('disabled');

		const newDate = '01/01';
		const newTime = '12:00';

		fireEvent.change(dateInput, {target: {value: newDate}});
		fireEvent.change(timeInput, {target: {value: newTime}});
		fireEvent.change(commentInput, {target: {value: 'test'}});

		expect(dateInput.parentNode).toHaveClass('has-error');
		expect(timeInput.parentNode).toHaveClass('has-error');
		expect(doneBtn).toHaveAttribute('disabled');

		fireEvent.change(dateInput, {target: {value: `${newDate}/2020`}});
		fireEvent.change(timeInput, {target: {value: '10:00 PM'}});

		expect(dateInput.parentNode).not.toHaveClass('has-error');
		expect(timeInput.parentNode).not.toHaveClass('has-error');
		expect(doneBtn).not.toHaveAttribute('disabled');

		fireEvent.click(doneBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render modal reassign error and retry', async () => {
		const alertError = getByText(
			'your-request-has-failed select-done-to-retry'
		);
		const doneBtn = getByText('done');

		expect(alertError).toBeTruthy();
		expect(doneBtn).not.toHaveAttribute('disabled');

		fireEvent.click(doneBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render alert with success message and close modal', () => {
		const alertToast = document.querySelector('.alert-dismissible');
		const alertClose = alertToast.children[1];

		expect(alertToast).toHaveTextContent(
			'the-due-date-for-this-task-has-been-updated'
		);

		fireEvent.click(alertClose);

		const alertContainer = document.querySelector('.alert-container');
		expect(alertContainer.children[0].children.length).toBe(0);
	});
});
