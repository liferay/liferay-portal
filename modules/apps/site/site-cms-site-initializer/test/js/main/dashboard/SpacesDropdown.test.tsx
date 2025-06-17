/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ViewDashboardContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/ViewDashboardContext';
import {SpacesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/SpacesDropdown';

const mockSpaces = (items: {id: string; name: string}[] = []) => {
	global.fetch = jest.fn().mockReturnValue({
		json: jest.fn().mockReturnValue({items}),
		ok: true,
	});
};

const WrappedComponent = ({constants}: any) => (
	<ViewDashboardContextProvider value={{constants}}>
		<SpacesDropdown />
	</ViewDashboardContextProvider>
);

describe('[CMS Dashboard] Components: SpacesDropdown', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly', async () => {
		mockSpaces();

		render(<WrappedComponent />);

		const spacesDropdownButton = screen.getByRole('button', {
			name: 'all-spaces',
		});

		expect(spacesDropdownButton).toBeInTheDocument();

		fireEvent.click(spacesDropdownButton);

		expect(screen.queryByText('filter-by-spaces')).toBeInTheDocument();

		expect(screen.queryByPlaceholderText('search')).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		expect(
			screen.queryByRole('menuitem', {name: 'all-spaces'})
		).toBeInTheDocument();
	});

	it('renders a space list', async () => {
		mockSpaces([
			{id: '01', name: 'space 01'},
			{id: '02', name: 'space 02'},
		]);

		render(<WrappedComponent />);

		const spacesDropdownButton = screen.getByRole('button', {
			name: 'all-spaces',
		});

		fireEvent.click(spacesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		expect(
			screen.queryByRole('menuitem', {name: 'all-spaces'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'space 01'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'space 02'})
		).toBeInTheDocument();
	});

	xit('search by a space and returns a filtered result', async () => {
		mockSpaces([
			{id: '01', name: 'space 01'},
			{id: '02', name: 'space 02'},
		]);

		render(<WrappedComponent />);

		const spacesDropdownButton = screen.getByRole('button', {
			name: 'all-spaces',
		});

		fireEvent.click(spacesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockSpaces([{id: '02', name: 'space 02'}]);

		await userEvent.type(screen.getByPlaceholderText('search'), 'space 02');

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-spaces'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'space 01'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'space 02'})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});

	xit('search by a space and returns a empty result', async () => {
		mockSpaces([
			{id: '01', name: 'space 01'},
			{id: '02', name: 'space 02'},
		]);

		render(<WrappedComponent />);

		const spacesDropdownButton = screen.getByRole('button', {
			name: 'all-spaces',
		});

		fireEvent.click(spacesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockSpaces();

		await userEvent.type(screen.getByPlaceholderText('search'), 'empty?');

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-spaces'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {
						name: 'no-filters-were-found',
					})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});

	it('selects a new space', async () => {
		mockSpaces([
			{id: '01', name: 'space 01'},
			{id: '02', name: 'space 02'},
		]);

		render(<WrappedComponent />);

		expect(screen.getByTestId('spaces')).toHaveTextContent('all-spaces');

		fireEvent.click(screen.getByTestId('spaces'));

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByRole('menuitem', {name: 'space 02'}));

		expect(screen.getByTestId('spaces')).toHaveTextContent('space 02');
	});
});
