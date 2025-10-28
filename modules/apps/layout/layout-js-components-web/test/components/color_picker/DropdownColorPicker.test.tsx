/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	waitFor,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React, {ComponentProps} from 'react';

import {DropdownColorPicker} from '../../../src/main/resources/META-INF/resources/js/components/color_picker/DropdownColorPicker';

const COLORS = {
	'Category 1': {
		'TokenSet 1': [
			{
				label: 'Color 1',
				name: 'color1',
				value: '#0b5fff',
			},
		],
		'TokenSet 2': [
			{
				label: 'Color 2',
				name: 'color2',
				value: '#ffffff',
			},
		],
	},
	'Category 2': {
		'TokenSet 3': [
			{
				label: 'Color 3',
				name: 'color3',
				value: '#0b5fff',
			},
		],
	},
};

const renderDropdownColorPicker = ({
	active = false,
	label = 'default',
	onSetActive = () => {},
	onValueChange = () => {},
	showSelector = true,
	value = '#fff',
}: Partial<ComponentProps<typeof DropdownColorPicker>>) =>
	render(
		<DropdownColorPicker
			active={active}
			colors={COLORS}
			label={label}
			onSetActive={onSetActive}
			onValueChange={onValueChange}
			showSelector={showSelector}
			value={value}
		/>
	);

describe('DropdownColorPicker', () => {
	it('renders the DropdownColorPicker without label and sploch', () => {
		const {getByTitle} = renderDropdownColorPicker({showSelector: false});

		expect(getByTitle('value-from-stylebook')).toBeInTheDocument();
	});

	it('renders the DropdownColorPicker as selector with label and sploch', async () => {
		const label = 'Danger';
		const value = '#ffb46e';

		const {baseElement, getByLabelText} = renderDropdownColorPicker({
			label,
			value,
		});

		expect(getByLabelText(label)).toBeInTheDocument();
		expect(
			baseElement.querySelector(
				'.layout__dropdown-color-picker__selector-splotch'
			)
		).toHaveStyle(`background: ${value}`);
	});

	it('opens the DropdownColorPicker', async () => {
		const {getByText, getByTitle} = renderDropdownColorPicker({
			active: true,
		});

		const palette = [
			getByText('Category 1'),
			getByText('Category 2'),
			getByText('TokenSet 1'),
			getByText('TokenSet 2'),
			getByText('TokenSet 3'),
			getByTitle('Color 1'),
			getByTitle('Color 2'),
			getByTitle('Color 3'),
		];

		palette.forEach((item) => expect(item).toBeInTheDocument());
	});

	it('filters by category', async () => {
		const {getByLabelText, queryByText} = renderDropdownColorPicker({
			active: true,
		});
		const searchForm = getByLabelText('search-form');

		fireEvent.change(searchForm, {
			target: {value: 'Category 1'},
		});

		await waitForElementToBeRemoved(queryByText('Category 2'));

		expect(queryByText('Category 1')).toBeInTheDocument();
	});

	it('filters by tokenSet', async () => {
		const {getByLabelText, queryByText} = renderDropdownColorPicker({
			active: true,
		});
		const searchForm = getByLabelText('search-form');

		fireEvent.change(searchForm, {
			target: {value: 'TokenSet 1'},
		});

		await waitFor(() => {
			[queryByText('Category 1'), queryByText('TokenSet 1')].forEach(
				(item) => expect(item).toBeInTheDocument()
			);
			[queryByText('Category 2'), queryByText('TokenSet 2')].forEach(
				(item) => expect(item).not.toBeInTheDocument()
			);
		});
	});

	it('filters by color', async () => {
		const {getByLabelText, getByTitle, queryByText, queryByTitle} =
			renderDropdownColorPicker({active: true});
		const searchForm = getByLabelText('search-form');

		fireEvent.change(searchForm, {
			target: {value: 'Color 1'},
		});

		await waitFor(() => {
			[
				queryByText('Category 1'),
				queryByText('TokenSet 1'),
				getByTitle('Color 1'),
			].forEach((item) => expect(item).toBeInTheDocument());
			[
				queryByText('Category 2'),
				queryByTitle('Color 2'),
				queryByTitle('Color 3'),
			].forEach((item) => expect(item).not.toBeInTheDocument());
		});
	});

	it('shows empty results', async () => {
		const {findByText, getByLabelText} = renderDropdownColorPicker({
			active: true,
		});
		const searchForm = getByLabelText('search-form');

		fireEvent.change(searchForm, {
			target: {value: 'Color 123'},
		});

		const noResultsMessage = await findByText('no-results-found');

		expect(noResultsMessage).toBeInTheDocument();
	});
});
