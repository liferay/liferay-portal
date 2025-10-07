/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SiteRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SiteRenderer';

describe('SiteRenderer', () => {
	it('renders the site name and logo correctly', () => {
		const mockItemData = {
			logo: '/some/path/to/site-logo.png',
		};
		const mockValue = 'My Test Site';

		render(<SiteRenderer itemData={mockItemData} value={mockValue} />);

		expect(screen.getByText(mockValue)).toBeInTheDocument();

		const siteLogo = screen.getByAltText(mockValue);
		expect(siteLogo).toBeInTheDocument();
		expect(siteLogo).toHaveAttribute('src', mockItemData.logo);
	});
});
