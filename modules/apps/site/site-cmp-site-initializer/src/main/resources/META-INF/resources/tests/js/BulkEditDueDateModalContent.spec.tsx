/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import BulkEditDueDateModalContent from '../../js/components/modal/BulkEditDueDateModalContent';

jest.mock('../../js/components/DateField', () => ({
	...(jest.requireActual('../../js/components/DateField') as object),
	__esModule: true,
	default: ({
		errorMessage,
		id,
		onChange,
	}: {
		errorMessage?: string;
		id: string;
		onChange: (value: string) => Promise<void> | void;
	}) => (
		<>
			<input
				data-testid="mock-date-field"
				id={id}
				onChange={(event) => onChange(event.target.value)}
				type="text"
			/>

			{errorMessage ? <span>{errorMessage}</span> : null}
		</>
	),
}));

const mockTriggerAssetBulkAction = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	triggerAssetBulkAction: (...args: any[]) =>
		mockTriggerAssetBulkAction(...args),
}));

const mockDisplayErrorToast = jest.fn();

jest.mock('../../js/utils/toastUtil', () => ({
	displayErrorToast: (...args: any[]) => mockDisplayErrorToast(...args),
}));

const mockCloseModal = jest.fn();
const mockSelectedData = {
	items: [{embedded: {id: 1}}, {embedded: {id: 2}}],
};

const renderModal = () =>
	render(
		<BulkEditDueDateModalContent
			closeModal={mockCloseModal}
			dataSetId="dataSetId"
			selectedData={mockSelectedData as any}
		/>
	);

describe('BulkEditDueDateModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('rejects an unparseable date without calling the server', async () => {
		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '13/45/2026'},
		});

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(getByText('please-enter-a-valid-date')).toBeInTheDocument();
		});

		expect(mockTriggerAssetBulkAction).not.toHaveBeenCalled();
	});

	it('requires a date instead of silently ignoring the submit', async () => {
		const {getByText} = renderModal();

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(getByText('this-field-is-required')).toBeInTheDocument();
		});

		expect(mockTriggerAssetBulkAction).not.toHaveBeenCalled();
	});

	it('sends the selected date in the server format', async () => {
		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '08/20/2026'},
		});

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(mockTriggerAssetBulkAction).toHaveBeenCalledWith(
				expect.objectContaining({
					keyValues: {dueDate: '2026-08-20'},
				})
			);
		});
	});

	it('shows the server error in the error toast', async () => {
		mockTriggerAssetBulkAction.mockImplementationOnce(({onCreateError}) =>
			onCreateError({error: 'You do not have permission'})
		);

		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '08/20/2026'},
		});

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(mockDisplayErrorToast).toHaveBeenCalledWith(
				'You do not have permission'
			);
		});

		expect(mockCloseModal).not.toHaveBeenCalled();
	});
});
