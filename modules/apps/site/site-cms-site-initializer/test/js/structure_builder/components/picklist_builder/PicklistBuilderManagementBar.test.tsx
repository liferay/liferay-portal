/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import PicklistService from '../../../../../src/main/resources/META-INF/resources/js/services/PicklistService';
import PicklistBuilderManagementBar from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/components/picklist_builder/PicklistBuilderManagementBar';
import {State} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/PicklistBuilderContext';
import {
	MockCacheProvider,
	broadcastRefMock,
} from '../../mocks/MockCacheProvider';
import {MockStateProvider} from '../../mocks/MockPicklistStateProvider';

jest.mock('@liferay/layout-js-components-web', () => {
	const actual = jest.requireActual('@liferay/layout-js-components-web');

	return {
		...actual,
		openConfirmModal: jest.fn(),
	};
});

const renderComponent = (state?: Partial<State>) => {
	return render(
		<MockStateProvider state={state}>
			<MockCacheProvider>
				<PicklistBuilderManagementBar />
			</MockCacheProvider>
		</MockStateProvider>
	);
};

const closeToast = async () => {
	await userEvent.click(screen.getByRole('button', {name: 'Close'}));
};

describe('PicklistBuilderManagementBar', () => {
	beforeAll(() => {
		PicklistService.createPicklist = jest.fn().mockResolvedValue({id: 1});
		PicklistService.updatePicklist = jest.fn();
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('creates a new picklist when no id exists', async () => {
		renderComponent({id: null});

		const saveButton = screen.getByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		expect(PicklistService.updatePicklist).not.toBeCalled();
		expect(PicklistService.createPicklist).toBeCalledWith({
			erc: 'picklistERC',
			name: {en_US: 'Picklist Name'},
			options: new Map([
				['option1ERC', {key: 'option1', name: {en_US: 'Option 1'}}],
			]),
		});
		expect(
			screen.getByText('Picklist Name-was-saved-successfully')
		).toBeInTheDocument();

		await closeToast();
	});

	it('sends a message via broadcast to update the cache state', async () => {
		renderComponent({id: null});

		const saveButton = screen.getByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		expect(broadcastRefMock.current.postMessage).toHaveBeenCalledWith({
			key: 'picklists',
			type: 'staleCache',
		});

		await closeToast();
	});

	it('updates the picklist when id exists', async () => {
		renderComponent({erc: 'newPicklistERC'});

		const saveButton = screen.getByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		expect(PicklistService.createPicklist).not.toBeCalled();
		expect(PicklistService.updatePicklist).toBeCalledWith(
			expect.objectContaining({
				erc: 'newPicklistERC',
			})
		);
		expect(
			screen.getByText('Picklist Name-was-saved-successfully')
		).toBeInTheDocument();

		await closeToast();
	});

	it('does not save anything when there is no ERC', async () => {
		renderComponent({erc: ''});

		const saveButton = screen.getByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		expect(PicklistService.createPicklist).not.toBeCalled();
		expect(PicklistService.updatePicklist).not.toBeCalled();
	});

	it('does not save anything when there is no name', async () => {
		renderComponent({name: {en_US: ''}});

		const saveButton = screen.getByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		expect(PicklistService.createPicklist).not.toBeCalled();
		expect(PicklistService.updatePicklist).not.toBeCalled();
	});

	it('shows an error toast when there has been an issue saving', async () => {
		renderComponent({id: null});

		PicklistService.createPicklist = jest.fn().mockImplementation(() => {
			throw new Error('Something was wrong');
		});

		const saveButton = screen.getByText('save');

		await userEvent.click(saveButton);

		expect(screen.getByText('Something was wrong')).toBeInTheDocument();

		await closeToast();
	});

	it('Shows warning modal when an option has been deleted', async () => {
		renderComponent({
			deletedOptions: true,
		});

		const saveButton = screen.getByText('save');

		await userEvent.click(saveButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'you-deleted-one-or-more-options-from-the-picklist',
				})
			);
			expect(PicklistService.updatePicklist).not.toBeCalled();
			expect(PicklistService.createPicklist).not.toBeCalled();
		});
	});
});
