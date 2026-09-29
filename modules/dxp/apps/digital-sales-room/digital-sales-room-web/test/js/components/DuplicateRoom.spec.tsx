/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import RoomService from '../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import DuplicateRoom from '../../../src/main/resources/META-INF/resources/js/components/DuplicateRoom';

jest.mock('@clayui/modal', () => {
	const React = require('react');

	return {
		__esModule: true,
		default: {
			Body: ({children}: any) =>
				React.createElement('div', null, children),
			Footer: ({last}: any) => React.createElement('div', null, last),
			Header: ({children}: any) =>
				React.createElement('div', null, children),
		},
	};
});

jest.mock('@liferay/frontend-data-set-web', () => {
	const React = require('react');

	return {
		FrontendDataSet: ({onSelectedItemsChange}: any) =>
			React.createElement(
				'button',
				{
					onClick: () =>
						onSelectedItemsChange([{id: 101}, {id: 102}]),
					type: 'button',
				},
				'select-documents'
			),
	};
});

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {
			duplicateRoom: jest.fn(),
			getDocumentsFolderId: jest.fn(),
			getRoom: jest.fn(),
		},
	})
);

const mockDisplayErrorToast = jest.fn();
const mockDisplaySuccessToast = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/utils/toastUtil',
	() => ({
		displayErrorToast: (...args: unknown[]) =>
			mockDisplayErrorToast(...args),
		displaySuccessToast: (...args: unknown[]) =>
			mockDisplaySuccessToast(...args),
	})
);

describe('DuplicateRoom', () => {
	const closeModal = jest.fn();
	const loadData = jest.fn();

	const renderComponent = () =>
		render(
			<DuplicateRoom
				closeModal={closeModal}
				loadData={loadData}
				name="Room A"
				roomId={10}
				siteId={20}
			/>
		);

	beforeEach(() => {
		jest.clearAllMocks();

		(RoomService.getDocumentsFolderId as jest.Mock).mockResolvedValue(555);

		(RoomService.duplicateRoom as jest.Mock).mockResolvedValue({id: 99});

		(RoomService.getRoom as jest.Mock).mockResolvedValue({
			id: 99,
			initialized: true,
		});
	});

	afterEach(() => {
		cleanup();
		jest.restoreAllMocks();
	});

	it('resolves the DSR_DOCUMENTS folder for the source room site', async () => {
		renderComponent();

		await waitFor(() => {
			expect(RoomService.getDocumentsFolderId).toHaveBeenCalledWith(
				20,
				'DSR_DOCUMENTS'
			);
		});
	});

	it('duplicates the room with the selected documents and a copy name', async () => {
		renderComponent();

		await waitFor(() => {
			expect(screen.getByText('select-documents')).toBeInTheDocument();
		});

		await userEvent.click(screen.getByText('select-documents'));
		await userEvent.click(screen.getByText('duplicate'));

		await waitFor(() => {
			expect(RoomService.duplicateRoom).toHaveBeenCalledWith(10, {
				fileEntryIds: [101, 102],
				name: 'Room A (Copy)',
			});
		});

		await waitFor(
			() => {
				expect(mockDisplaySuccessToast).toHaveBeenCalled();
				expect(closeModal).toHaveBeenCalled();
				expect(loadData).toHaveBeenCalled();
			},
			{timeout: 5000}
		);
	}, 10000);

	it('shows an error toast when the duplication fails', async () => {
		(RoomService.duplicateRoom as jest.Mock).mockRejectedValue(
			new Error('Duplication failed')
		);

		renderComponent();

		await waitFor(() => {
			expect(screen.getByText('select-documents')).toBeInTheDocument();
		});

		await userEvent.click(screen.getByText('duplicate'));

		await waitFor(() => {
			expect(mockDisplayErrorToast).toHaveBeenCalledWith(
				'Duplication failed'
			);
		});
	});
});
