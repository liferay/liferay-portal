/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {
	BaseCard,
	IBaseCard,
} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/BaseCard';

describe('[CMS Dashboard] Components: BaseCard', () => {
	const mockedProps: IBaseCard = {
		Preferences: <button>preferences</button>,
		children: <h3>children</h3>,
		description: 'description',
		title: 'title',
	};

	it('renders correctly with given props', () => {
		render(<BaseCard {...mockedProps} />);

		expect(screen.getByText('TITLE')).toBeInTheDocument();
		expect(screen.getByText('description')).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'preferences'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('heading', {level: 3, name: 'children'})
		).toBeInTheDocument();
	});
});
