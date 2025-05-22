/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SpaceLogoColorDropdown from '../../../../src/main/resources/META-INF/resources/js/main/components/SpaceLogoColorDropdown';

describe('SpaceLogoColorDropdown', () => {
	it('renders outline-0 as default selected option', () => {
		render(<SpaceLogoColorDropdown />);

		const dropdownButton = screen.getByRole('button', {
			name: 'space-color',
		});
		expect(dropdownButton).toBeInTheDocument();
		expect(
			dropdownButton.getElementsByClassName('sticker-outline-0')
		).toHaveLength(1);
	});

	it('renders all space colors', async () => {
		render(<SpaceLogoColorDropdown />);

		const dropdownButton = screen.getByRole('button', {
			name: 'space-color',
		});

		await userEvent.click(dropdownButton);

		const allColorElements = screen.getAllByRole('menuitem');
		expect(allColorElements).toHaveLength(10);

		allColorElements.forEach((element, index) => {
			expect(
				element.getElementsByClassName(`sticker-outline-${index}`)
			).toHaveLength(1);
		});
	});

	it('calls callback when selecting an option', async () => {
		const onColorSelect = jest.fn();

		render(<SpaceLogoColorDropdown onChange={onColorSelect} />);

		await userEvent.click(
			screen.getByRole('button', {
				name: 'space-color',
			})
		);

		const colorIndex = 3;

		expect(onColorSelect).not.toHaveBeenCalled();

		await userEvent.click(screen.getAllByRole('menuitem')[colorIndex]);

		expect(onColorSelect).toHaveBeenCalledTimes(1);
		expect(onColorSelect).toHaveBeenCalledWith(`outline-${colorIndex}`);
	});
});
