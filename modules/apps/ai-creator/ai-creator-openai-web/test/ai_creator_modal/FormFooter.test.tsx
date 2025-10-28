/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import {FormFooter} from '../../src/main/resources/META-INF/resources/ai_creator_modal/FormFooter';

describe('FormFooter', () => {
	it('triggers onAdd callback', () => {
		const onAdd = jest.fn();

		render(
			<FormFooter
				onAdd={onAdd}
				onClose={() => {}}
				showAddButton
				showCreateButton
				showRetryButton
			/>
		);

		userEvent.click(screen.getByRole('button', {name: 'add'}));

		expect(onAdd).toHaveBeenCalledTimes(1);
	});

	it('triggers onClose callback', () => {
		const onClose = jest.fn();

		render(
			<FormFooter
				onAdd={() => {}}
				onClose={onClose}
				showAddButton
				showCreateButton
				showRetryButton
			/>
		);

		userEvent.click(screen.getByRole('button', {name: 'cancel'}));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('has a submit button', () => {
		render(
			<FormFooter
				onAdd={() => {}}
				onClose={() => {}}
				showAddButton
				showCreateButton
				showRetryButton
			/>
		);

		const createButton = screen.getByRole('button', {name: 'create'});
		const retryButton = screen.getByRole('button', {name: 'try-again'});

		expect(createButton.getAttribute('type')).toBe('submit');
		expect(retryButton.getAttribute('type')).toBe('submit');
	});
});
