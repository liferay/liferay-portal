/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import PicklistOptions from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/components/picklist_builder/PicklistOptions';
import * as PicklistContext from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/PicklistBuilderContext';
import {
	DEFAULT_STATE,
	MockStateProvider,
} from '../../mocks/MockPicklistStateProvider';

const renderComponent = (state?: Partial<PicklistContext.State>) => {
	return render(
		<MockStateProvider state={state}>
			<PicklistOptions />
		</MockStateProvider>
	);
};

describe('PicklistOptions', () => {
	it('shows an empty state when there are no picklists', () => {
		renderComponent({options: new Map()});

		expect(
			screen.getByText('there-are-no-options-yet')
		).toBeInTheDocument();
	});

	it('opens the add option modal when the Add New button is pressed', async () => {
		renderComponent({options: new Map()});

		await userEvent.click(screen.getByText('add-new'));

		await waitFor(() => {
			expect(screen.getByText('add-option')).toBeInTheDocument();
		});
	});

	it('opens the add option modal when the Add New button from the management toolbar is pressed', async () => {
		renderComponent({options: new Map()});

		await userEvent.click(screen.getByTitle('add-new'));

		await waitFor(() => {
			expect(screen.getByText('add-option')).toBeInTheDocument();
		});
	});

	it('shows the list of options', async () => {
		const options = new Map([...DEFAULT_STATE.options]).set('option2ERC', {
			key: 'option2',
			name: {en_US: 'Option 2'},
		});

		renderComponent({options});

		[
			'Option 1',
			'option1',
			'option1ERC',
			'Option 2',
			'option2',
			'option2ERC',
		].forEach((name) =>
			expect(screen.getByRole('cell', {name})).toBeInTheDocument()
		);

		expect(
			screen.queryByText('there-are-no-options-yet')
		).not.toBeInTheDocument();
	});

	it('edits an option', async () => {
		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'actions'}));

		await userEvent.click(screen.getByText('edit'));

		await waitFor(() => {
			expect(screen.getByLabelText('picklist-name')).toHaveValue(
				'Option 1'
			);
			expect(screen.getByLabelText('key')).toHaveValue('option1');
			expect(screen.getByLabelText('erc')).toHaveValue('option1ERC');
		});
	});

	it('deletes an option', async () => {
		const mockRemoveOption = jest.fn();

		jest.spyOn(PicklistContext, 'useRemoveOptions').mockImplementation(
			() => mockRemoveOption
		);

		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'actions'}));

		await userEvent.click(screen.getByText('delete'));

		await waitFor(() => {
			expect(mockRemoveOption).toHaveBeenCalledWith(['option1ERC']);
		});
	});

	it('deletes several options', async () => {
		const mockRemoveOption = jest.fn();

		jest.spyOn(PicklistContext, 'useRemoveOptions').mockImplementation(
			() => mockRemoveOption
		);

		const options = new Map([...DEFAULT_STATE.options]).set('option2ERC', {
			key: 'option2',
			name: 'Option 2' as any,
		});

		renderComponent({options});

		const selecItemCheckboxs = screen.queryAllByTitle('select-item');

		selecItemCheckboxs.forEach(async (selecItemCheckbox) => {
			await userEvent.click(selecItemCheckbox);
		});

		await waitFor(() => {
			expect(
				screen.getByText(`all-selected-x-of-x-items`)
			).toBeInTheDocument();
		});

		await userEvent.click(screen.getByTitle('actions'));

		await userEvent.click(screen.getByRole('menuitem', {name: 'delete'}));

		await waitFor(() => {
			expect(mockRemoveOption).toHaveBeenCalledWith([
				'option1ERC',
				'option2ERC',
			]);
		});
	});
});
