/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {
	ActionsDropdown,
	IActionsDropdown,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/ActionsDropdown';

describe('[CMS Dashboard] Components: ActionsDropdown', () => {
	const mockedOnChange = jest.fn();
	const mockedProps: IActionsDropdown = {
		items: [
			{label: 'item-1', value: 'item-1'},
			{label: 'item-2', value: 'item-2'},
		],
		onChange: mockedOnChange,
	};

	it('renders correctly with different options', () => {
		render(<ActionsDropdown {...mockedProps} />);

		const Dropdown = screen.getByRole('button');
		expect(Dropdown).toBeInTheDocument();

		fireEvent.click(Dropdown);

		const [PrimaryOption, SecondaryOption] =
			screen.getAllByRole('menuitem');

		expect(PrimaryOption).toBeInTheDocument();
		expect(PrimaryOption).toHaveTextContent('item-1');

		expect(SecondaryOption).toBeInTheDocument();
		expect(SecondaryOption).toHaveTextContent('item-2');
	});

	it('calls "onChange" function correctly after click on option', () => {
		render(<ActionsDropdown {...mockedProps} />);

		const Dropdown = screen.getByRole('button');
		expect(Dropdown).toBeInTheDocument();

		fireEvent.click(Dropdown);

		expect(mockedOnChange).not.toHaveBeenCalled();

		const PrimaryOption = screen.getByRole('menuitem', {
			name: 'item-1',
		});
		expect(PrimaryOption).toBeInTheDocument();

		fireEvent.click(PrimaryOption);

		expect(mockedOnChange).toHaveBeenCalledTimes(1);
	});
});
