/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SiteRenderer from '../../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/SiteRenderer';

describe('SiteRenderer', () => {
	it('renders the site name with its logo', () => {
		render(<SiteRenderer itemData={{logo: '/logo.png'}} value="My Site" />);

		expect(screen.getByText('My Site')).toBeInTheDocument();
		expect(screen.getByAltText('My Site')).toHaveAttribute(
			'src',
			'/logo.png'
		);
	});
});
