/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {SpacesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/SpacesDropdown';

describe('[CMS Dashboard] Components: SpacesDropdown', () => {
	it('renders correctly', () => {
		render(<SpacesDropdown />);

		expect(
			screen.getByRole('button', {name: 'all-spaces'})
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'all-spaces'}));

		expect(screen.getAllByRole('menuitem').length).toBeGreaterThanOrEqual(
			1
		);
		expect(
			screen.getByRole('menuitem', {name: 'all-spaces'})
		).toBeInTheDocument();
	});
});
