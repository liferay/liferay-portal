/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CopyFragmentModal from '../../../src/main/resources/META-INF/resources/js/components/modals/CopyFragmentModal';

const renderComponent = ({fragmentCollections = []} = {}) => {
	const user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});

	render(<CopyFragmentModal fragmentCollections={fragmentCollections} />);

	return user;
};

describe('CopyFragmentModal', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders fragment collection form with message when no there is no fragment collections', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(
			screen.getByText(
				'a-fragment-set-must-first-be-created-before-you-can-copy-it'
			)
		).toBeInTheDocument();
	});

	it('renders fragment collections selector when there are fragment collections', () => {
		renderComponent({
			fragmentCollections: [
				{fragmentCollectionId: 1, name: 'fragment-collection'},
			],
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.getByLabelText('fragment-sets')).toBeInTheDocument();
	});

	it('renders fragment collections from without message when clicking on the save in new set button', async () => {
		const user = renderComponent({
			fragmentCollections: [
				{fragmentCollectionId: 1, name: 'fragment-collection'},
			],
		});

		act(() => {
			jest.runAllTimers();
		});

		const button = screen.getByText('save-in-new-set');

		await user.click(button);

		expect(screen.getByLabelText('name')).toBeInTheDocument();

		expect(
			screen.queryByText(
				'a-fragment-set-must-first-be-created-before-you-can-copy-it'
			)
		).not.toBeInTheDocument();
	});

	it('show required validation when no name is introduced', async () => {
		const user = renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.change(screen.getByLabelText('name'), {
			target: {value: ''},
		});

		await user.click(screen.getByText('save'));

		expect(screen.getByText('x-field-is-required')).toBeInTheDocument();
	});

	it('show required validation when no fragment collection is introduced', async () => {
		const user = renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.change(screen.getByLabelText('name'), {
			target: {value: ''},
		});

		await user.click(screen.getByText('save'));

		expect(screen.getByText('x-field-is-required')).toBeInTheDocument();
	});
});
