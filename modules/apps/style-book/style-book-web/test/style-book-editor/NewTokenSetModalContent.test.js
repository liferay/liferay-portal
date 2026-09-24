/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import NewTokenSetModalContent from '../../src/main/resources/META-INF/resources/js/style-book-editor/NewTokenSetModalContent';

const renderComponent = () =>
	render(
		<NewTokenSetModalContent
			closeModal={jest.fn()}
			existingTokenSets={[{label: 'Brand Colors', name: 'brandColors'}]}
			onSuccess={jest.fn()}
		/>
	);

describe('NewTokenSetModalContent', () => {
	it('disables the Create Token Set button until a label is filled', async () => {
		renderComponent();

		await waitFor(() =>
			expect(screen.getByText('create-token-set')).toBeDisabled()
		);

		await userEvent.type(
			screen.getByLabelText('name', {exact: false}),
			'Unique Label'
		);

		await waitFor(() =>
			expect(screen.getByText('create-token-set')).toBeEnabled()
		);
	});

	it('rejects a label matching an existing set label, ignoring case and extra spacing', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText('name', {exact: false});

		await userEvent.type(nameInput, '  brand colors  ');
		fireEvent.blur(nameInput);

		expect(
			await screen.findByText('a-token-set-with-that-name-already-exists')
		).toBeInTheDocument();
		await waitFor(() =>
			expect(screen.getByText('create-token-set')).toBeDisabled()
		);
	});

	it('rejects a label matching an existing set internal name', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText('name', {exact: false});

		await userEvent.type(nameInput, 'brandColors');
		fireEvent.blur(nameInput);

		expect(
			await screen.findByText('a-token-set-with-that-name-already-exists')
		).toBeInTheDocument();
	});
});
