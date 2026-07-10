/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import FragmentSetModal from '../../../src/main/resources/META-INF/resources/js/components/modals/FragmentSetModal';

const renderComponent = ({
	allowCustomName,
	fragmentCollections = [],
	onSubmitFragmentCollection,
} = {}) => {
	const user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});

	render(
		<FragmentSetModal
			allowCustomName={allowCustomName}
			fragmentCollections={fragmentCollections}
			onSubmitFragmentCollection={onSubmitFragmentCollection}
		/>
	);

	return user;
};

describe('FragmentSetModal', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders fragment collection form when there are no fragment collections', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(
			screen.getByText('add-a-fragment-set-to-save-your-fragment')
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
		expect(
			screen.getByDisplayValue('-- not-selected --')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'select-an-existing-set-or-create-a-new-one-to-save-your-fragment'
			)
		).toBeInTheDocument();
	});

	it('renders fragment collections form when clicking on the save in new set button', async () => {
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
			screen.queryByText('add-a-fragment-set-to-save-your-fragment')
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
		const user = renderComponent({
			fragmentCollections: [
				{fragmentCollectionId: 1, name: 'fragment-collection'},
			],
		});

		act(() => {
			jest.runAllTimers();
		});

		await user.click(screen.getByText('save'));

		expect(screen.getByText('x-field-is-required')).toBeInTheDocument();
	});

	it('submits the selected fragment collection', async () => {
		const onSubmitFragmentCollection = jest.fn();
		const user = renderComponent({
			fragmentCollections: [
				{fragmentCollectionId: 1, name: 'fragment-collection'},
			],
			onSubmitFragmentCollection,
		});

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.change(screen.getByLabelText('fragment-sets'), {
			target: {value: '1'},
		});

		await user.click(screen.getByText('save'));

		expect(onSubmitFragmentCollection).toHaveBeenCalledWith(1, '');
	});

	it('submits the selected fragment collection with the fragment name when allowCustomName is set', async () => {
		const onSubmitFragmentCollection = jest.fn();
		const user = renderComponent({
			allowCustomName: true,
			fragmentCollections: [
				{fragmentCollectionId: 1, name: 'fragment-collection'},
			],
			onSubmitFragmentCollection,
		});

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.change(screen.getByLabelText('name'), {
			target: {value: 'my-fragment'},
		});

		fireEvent.change(screen.getByLabelText('fragment-sets'), {
			target: {value: '1'},
		});

		await user.click(screen.getByText('save'));

		expect(onSubmitFragmentCollection).toHaveBeenCalledWith(
			1,
			'my-fragment'
		);
	});

	it('shows required validation when the fragment name is empty and allowCustomName is set', async () => {
		const onSubmitFragmentCollection = jest.fn();
		const user = renderComponent({
			allowCustomName: true,
			fragmentCollections: [
				{fragmentCollectionId: 1, name: 'fragment-collection'},
			],
			onSubmitFragmentCollection,
		});

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.change(screen.getByLabelText('fragment-sets'), {
			target: {value: '1'},
		});

		await user.click(screen.getByText('save'));

		expect(onSubmitFragmentCollection).not.toHaveBeenCalled();
		expect(screen.getByText('x-field-is-required')).toBeInTheDocument();
	});

	it('does not create the fragment set when the fragment name is empty and allowCustomName is set', async () => {
		const onSubmitFragmentCollection = jest.fn();
		const user = renderComponent({
			allowCustomName: true,
			onSubmitFragmentCollection,
		});

		act(() => {
			jest.runAllTimers();
		});

		await user.click(screen.getByText('save'));

		expect(fetch).not.toHaveBeenCalled();
		expect(onSubmitFragmentCollection).not.toHaveBeenCalled();
		expect(screen.getByText('x-field-is-required')).toBeInTheDocument();
	});
});
