/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import CategorizationSpaces from '../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/components/CategorizationSpaces';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

const mockSpaces = [
	{
		assetLibraryKey: 'marketing-key',
		id: 1,
		name: 'Marketing Library',
		settings: {logoColor: 'outline-7'},
	},
	{
		assetLibraryKey: 'sales-key',
		id: 2,
		name: 'Sales Library',
		settings: {logoColor: 'outline-3'},
	},
	{
		assetLibraryKey: 'engineering-key',
		id: 3,
		name: 'Engineering Docs',
		settings: {logoColor: 'outline-4'},
	},
];

const defaultProps = {
	checkboxText: 'vocabulary',
	setSelectedSpaces: jest.fn(),
	setSpaceInputError: jest.fn(),
	spaceInputError: '',
};

describe('CategorizationSpaces', () => {
	beforeEach(() => {
		jest.spyOn(SpaceService, 'getSpaces').mockResolvedValue(
			mockSpaces as any
		);

		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('allows typing in the MultiSelect input to filter the dropdown options', async () => {
		const user = userEvent.setup();

		render(<CategorizationSpaces {...defaultProps} />);

		await waitFor(() => {
			expect(SpaceService.getSpaces).toHaveBeenCalled();
		});

		const checkbox = screen.getByRole('checkbox');

		expect(checkbox).toBeChecked();

		await user.click(checkbox);

		expect(checkbox).not.toBeChecked();

		const input = screen.getByRole('combobox');

		expect(input).not.toBeDisabled();

		await user.click(input);

		await waitFor(() => {
			expect(screen.getAllByRole('option').length).toBe(3);
		});

		await user.type(input, 'Library');

		expect(input).toHaveValue('Library');

		await waitFor(() => {
			const options = screen.getAllByRole('option');

			expect(options.length).toBe(2);
		});

		expect(screen.getByText('Marketing Library')).toBeInTheDocument();

		expect(screen.getByText('Sales Library')).toBeInTheDocument();

		expect(screen.queryByText('Engineering Docs')).not.toBeInTheDocument();

		await user.clear(input);

		await user.type(input, 'Engineering');

		expect(input).toHaveValue('Engineering');

		await waitFor(() => {
			const options = screen.getAllByRole('option');

			expect(options.length).toBe(1);
		});

		expect(screen.getByText('Engineering Docs')).toBeInTheDocument();
	}, 15000);

	it('disables the input and checkbox when disabled is true', async () => {
		render(<CategorizationSpaces {...defaultProps} disabled />);

		await waitFor(() => {
			expect(SpaceService.getSpaces).toHaveBeenCalled();
		});

		expect(screen.getByRole('checkbox')).toBeDisabled();

		expect(screen.getByRole('combobox')).toBeDisabled();
	});
});
