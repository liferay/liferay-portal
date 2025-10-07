/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
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

	it('saves the option when the save button is pressed', async () => {
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
		});
	});

	it('calls onCloseModal when the cancel button is pressed', async () => {
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
