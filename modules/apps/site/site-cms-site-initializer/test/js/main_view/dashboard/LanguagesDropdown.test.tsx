/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {
	LanguagesDropdown,
	localizations,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/LanguagesDropdown';

describe('[CMS Dashboard] Components: LanguagesDropdown', () => {
	it('renders correctly', () => {
		render(<LanguagesDropdown />);

		expect(
			screen.getByRole('button', {name: 'all-languages'})
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'all-languages'}));

		expect(
			screen.getByRole('menuitem', {name: 'all-languages'})
		).toBeInTheDocument();

		const availableLanguages = Object.values(localizations);

		availableLanguages.forEach((translation) => {
			expect(
				screen.getByRole('menuitem', {name: translation})
			).toBeInTheDocument();
		});
	});
});
