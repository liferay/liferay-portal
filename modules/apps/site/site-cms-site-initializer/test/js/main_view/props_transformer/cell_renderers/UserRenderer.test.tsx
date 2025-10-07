/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import UserRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/UserRenderer';

describe('UserRenderer', () => {
	it('renders the user name and image', () => {
		const mockItemData = {
			image: '/test/image.png',
			name: 'Test User',
		};
		const mockValue = 'Test User';

		render(<UserRenderer itemData={mockItemData} value={mockValue} />);

		expect(screen.getByText(mockValue)).toBeInTheDocument();
		expect(screen.getByAltText(mockItemData.name)).toHaveAttribute(
			'src',
			mockItemData.image
		);
	});

	it('renders the user name and indicates if the user is an owner', () => {
		const mockItemData = {
			roles: [{name: 'Asset Library Owner'}],
		};
		const mockValue = 'Owner User';

		render(<UserRenderer itemData={mockItemData} value={mockValue} />);
		expect(screen.getByText('(owner)')).toBeInTheDocument();
	});
});
