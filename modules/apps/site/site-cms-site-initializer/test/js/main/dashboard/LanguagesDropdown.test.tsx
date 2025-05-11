/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {LanguagesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/LanguagesDropdown';

describe('[CMS Dashboard] Components: LanguagesDropdown', () => {
	it('renders correctly', () => {
		render(<LanguagesDropdown />);

		expect(
			screen.getByRole('button', {name: 'all-languages'})
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'all-languages'}));

		expect(screen.getAllByRole('menuitem').length).toBeGreaterThanOrEqual(
			1
		);
		expect(
			screen.getByRole('menuitem', {name: 'all-languages'})
		).toBeInTheDocument();
	});
});
