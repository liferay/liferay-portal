/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {Option} from '../../../../../src/main/resources/META-INF/resources/js/common/types/Picklist';
import AddOptionModal from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/components/picklist_builder/AddOptionModal';
import * as PicklistContext from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/PicklistBuilderContext';
import {MockStateProvider} from '../../mocks/MockPicklistStateProvider';

const onCloseModal = jest.fn();

const renderComponent = async (
	option: Option | null = {
		erc: 'smallERC',
		key: 'smallSize',
		name: {en_US: 'Small'},
	}
) => {
	return render(
		<MockStateProvider>
			<AddOptionModal onCloseModal={onCloseModal} option={option} />
		</MockStateProvider>
	);
};

describe('AddOptionModal', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('Generates random values if no option exists', async () => {
		renderComponent(null);

		await waitFor(() => {
			expect(screen.getByText('add-option')).toBeInTheDocument();
			expect(screen.getByLabelText('picklist-name')).toHaveValue(
				'option'
			);

			const keyInput = screen.getByLabelText('key') as HTMLInputElement;

			expect(keyInput.value).toMatch(/^option\d{6}$/);
			expect(screen.getByLabelText('key')).not.toBeDisabled();

			const ercInput = screen.getByLabelText('erc') as HTMLInputElement;

			expect(ercInput.value).toMatch(
				/^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/i
			);
		});
	});

	it('fills the inputs with the values of the selected option', async () => {
		renderComponent();

		await waitFor(() => {
			expect(screen.getByLabelText('picklist-name')).toHaveValue('Small');
			expect(screen.getByLabelText('key')).toHaveValue('smallSize');
			expect(screen.getByLabelText('key')).toBeDisabled();
			expect(screen.getByLabelText('erc')).toHaveValue('smallERC');
		});
	});

	it('adds the option when the "Save" button is pressed and closes the modal', async () => {
		renderComponent(null);

		const mockAddOption = jest.fn();

		jest.spyOn(PicklistContext, 'useAddOption').mockImplementation(
			() => mockAddOption
		);

		await waitFor(() => {
			expect(screen.getByText('add-option')).toBeInTheDocument();
		});

		const nameInput = screen.getByLabelText('picklist-name');

		await userEvent.clear(nameInput);
		await userEvent.type(nameInput, 'Large');

		const keyInput = screen.getByLabelText('key');

		await userEvent.clear(keyInput);
		await userEvent.type(keyInput, 'largeSize');

		const ercInput = screen.getByLabelText('erc');

		await userEvent.clear(ercInput);
		await userEvent.type(ercInput, 'largeERC');

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => {
			expect(mockAddOption).toHaveBeenCalledWith({
				erc: 'largeERC',
				key: 'largeSize',
				name: {en_US: expect.stringContaining('Large')},
			});

			expect(onCloseModal).toBeCalled();
		});
	});

	it('adds options when the "Save and Add Another button" is pressed and keeps the modal open', async () => {
		const getInputValue = (label: string) =>
			(screen.getByLabelText(label) as HTMLInputElement).value;

		renderComponent(null);

		const mockAddOption = jest.fn();

		jest.spyOn(PicklistContext, 'useAddOption').mockImplementation(
			() => mockAddOption
		);

		await waitFor(() => {
			expect(screen.getByText('add-option')).toBeInTheDocument();
		});

		const erc = getInputValue('erc');
		const key = getInputValue('key');
		const name = getInputValue('picklist-name');

		await userEvent.click(screen.getByText('save-and-add-another'));

		await waitFor(() => {
			expect(onCloseModal).not.toBeCalled();

			expect(mockAddOption).toHaveBeenCalledWith({
				erc,
				key,
				name: {en_US: name},
			});
		});

		const nextErc = getInputValue('erc');
		const nextKey = getInputValue('key');
		const nextName = getInputValue('picklist-name');

		await waitFor(() => {
			expect(name).toBe(nextName);
			expect(key).not.toBe(nextKey);
			expect(erc).not.toBe(nextErc);
		});

		await userEvent.click(screen.getByText('save-and-add-another'));

		await waitFor(() => {
			expect(onCloseModal).not.toBeCalled();

			expect(mockAddOption).toHaveBeenCalledWith({
				erc: nextErc,
				key: nextKey,
				name: {en_US: nextName},
			});
		});
	});

	it('calls onCloseModal when the "Cancel" button is pressed', async () => {
		renderComponent();

		await waitFor(() => {
			expect(screen.getByText('add-option')).toBeInTheDocument();
		});

		await userEvent.click(screen.getByText('cancel'));

		await waitFor(() => {
			expect(onCloseModal).toBeCalled();
		});
	});
});
