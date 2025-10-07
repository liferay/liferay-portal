/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import Toolbar from '../../../../src/main/resources/META-INF/resources/js/common/components/Toolbar';

const renderComponent = ({
	backURL,
	children,
	title = 'New Page',
}: {
	backURL?: string;
	children?: React.ReactNode;
	title?: string;
} = {}) => {
	return render(
		<Toolbar backURL={backURL} title={title}>
			{children}
		</Toolbar>
	);
};

describe('Toolbar', () => {
	it('renders Toolbar with the title but not the Back button', async () => {
		renderComponent();

		expect(
			screen.getByRole('heading', {name: 'New Page'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'back'})
		).not.toBeInTheDocument();
	});

	it('renders Toolbar with children', async () => {
		const Button = () => <button>Save page</button>;

		renderComponent({children: <Button />});

		expect(
			screen.getByRole('button', {name: 'Save page'})
		).toBeInTheDocument();
	});

	it('renders Toolbar with backURL', async () => {
		renderComponent({backURL: 'url'});

		expect(screen.getByRole('button', {name: 'back'})).toBeInTheDocument();
	});
});
