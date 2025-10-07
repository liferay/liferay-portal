/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import UserGroupRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/UserGroupRenderer';

describe('UserGroupRenderer', () => {
	it('renders the user group name and member count correctly', () => {
		const mockItemData = {
			numberOfUserAccounts: 5,
		};
		const mockValue = 'My Users Group';

		render(<UserGroupRenderer itemData={mockItemData} value={mockValue} />);

		expect(screen.getByText(mockValue)).toBeInTheDocument();
		expect(screen.getByText('(5-members)')).toBeInTheDocument();
	});

	it('renders zero members when no numberOfUserAccounts is provided', () => {
		const mockItemData = {};
		const mockValue = 'My Users Group';

		render(<UserGroupRenderer itemData={mockItemData} value={mockValue} />);

		expect(screen.getByText(mockValue)).toBeInTheDocument();
		expect(screen.getByText('(0-members)')).toBeInTheDocument();
	});
});
