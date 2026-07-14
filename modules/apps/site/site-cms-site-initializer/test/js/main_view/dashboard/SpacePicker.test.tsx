/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React, {useState} from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {
	SpaceOption,
	SpacePicker,
	initialSpace,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/SpacePicker';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

const mockedSpaceService = SpaceService as jest.Mocked<typeof SpaceService>;

const WrappedComponent = () => {
	const [space, setSpace] = useState<SpaceOption>(initialSpace);

	return <SpacePicker onSelectSpace={setSpace} selectedSpace={space} />;
};

describe('[CMS Dashboard] Components: SpacePicker', () => {
	const mockSpaces = [
		{externalReferenceCode: 'ERC_01', id: '01', name: 'space 01'},
		{externalReferenceCode: 'ERC_02', id: '02', name: 'space 02'},
	];

	beforeEach(() => {
		jest.clearAllMocks();

		mockedSpaceService.getSpaces.mockResolvedValue(mockSpaces as any);
	});

	it('fetches the spaces', async () => {
		render(<WrappedComponent />);

		await waitFor(() =>
			expect(mockedSpaceService.getSpaces).toHaveBeenCalled()
		);
	});

	it('renders the selected space in the trigger', () => {
		render(<WrappedComponent />);

		expect(
			screen.getByRole('combobox', {name: 'filter-by-spaces'})
		).toHaveTextContent('all-spaces');
	});

	it('renders the space list', async () => {
		render(<WrappedComponent />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-spaces'})
		);

		const listbox = await screen.findByRole('listbox');

		expect(
			await within(listbox).findByRole('option', {name: 'space 01'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'space 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'all-spaces'})
		).toBeInTheDocument();

		expect(within(listbox).getAllByRole('option')).toHaveLength(3);
	});

	it('filters the list when searching', async () => {
		render(<WrappedComponent />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-spaces'})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'space 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'space 02'},
		});

		await waitFor(() =>
			expect(within(listbox).getAllByRole('option')).toHaveLength(1)
		);

		expect(
			within(listbox).getByRole('option', {name: 'space 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'space 01'})
		).not.toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'all-spaces'})
		).not.toBeInTheDocument();
	});

	it('shows a message when no space matches the search', async () => {
		render(<WrappedComponent />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-spaces'})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'space 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'no-match'},
		});

		await waitFor(() =>
			expect(
				within(listbox).queryByRole('option')
			).not.toBeInTheDocument()
		);

		expect(
			within(listbox).getByText('no-results-were-found')
		).toBeInTheDocument();
	});

	it('selects a new space', async () => {
		render(<WrappedComponent />);

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-spaces',
		});

		fireEvent.click(trigger);

		const listbox = await screen.findByRole('listbox');

		fireEvent.click(
			await within(listbox).findByRole('option', {name: 'space 02'})
		);

		await waitFor(() => expect(trigger).toHaveTextContent('space 02'));
	});
});
