/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import RoomSettingsToolbar from '../../../../src/main/resources/META-INF/resources/js/main_view/rooms/RoomSettingsToolbar';

const renderComponent = (props = {}) =>
	render(
		<RoomSettingsToolbar
			backURL="/back"
			headerTitle="name1 Settings"
			{...props}
		/>
	);

describe('RoomSettingsToolbar', () => {
	afterEach(() => {
		cleanup();

		document.body.innerHTML = '';

		jest.clearAllMocks();
	});

	it('renders the header title', () => {
		renderComponent();

		expect(screen.getByText('name1 Settings')).toBeInTheDocument();
	});

	it('renders a back action pointing to the back URL', () => {
		renderComponent();

		expect(screen.getByLabelText('back')).toHaveAttribute('href', '/back');
	});

	it('renders a cancel link pointing to the back URL', () => {
		renderComponent();

		expect(screen.getByRole('link', {name: 'cancel'})).toHaveAttribute(
			'href',
			'/back'
		);
	});

	it('renders a save button that submits the form', () => {
		document.body.innerHTML =
			'<form class="lfr-main-form-container" id="roomForm"></form>';

		renderComponent();

		const saveButton = screen.getByRole('button', {name: 'save'});

		expect(saveButton).toHaveAttribute('type', 'submit');
		expect(saveButton).toHaveAttribute('form', 'roomForm');
	});
});
