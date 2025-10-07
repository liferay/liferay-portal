/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import VerticalNavLayout from '../../../../src/main/resources/META-INF/resources/js/common/components/VerticalNavLayout';

const verticalNavItems = [
	{
		component: <h2>Apple Section</h2>,
		id: 'apple',
		label: 'Apple',
	},
	{
		component: <h2>Banana Section</h2>,
		id: 'banana',
		label: 'Banana',
	},
];

const renderComponent = () => {
	return render(<VerticalNavLayout items={verticalNavItems} />);
};

describe('VerticalNavLayout', () => {
	it('renders VerticalNavLayout', async () => {
		renderComponent();

		expect(
			screen.getByRole('heading', {name: 'Apple Section'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('heading', {name: 'Banana Section'})
		).not.toBeInTheDocument();

		await userEvent.click(screen.getByText('Banana'));

		expect(
			screen.queryByRole('heading', {name: 'Apple Section'})
		).not.toBeInTheDocument();

		expect(
			screen.getByRole('heading', {name: 'Banana Section'})
		).toBeInTheDocument();
	});
});
