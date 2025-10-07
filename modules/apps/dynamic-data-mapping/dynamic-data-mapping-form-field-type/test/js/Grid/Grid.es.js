/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import Grid from '../../../src/main/resources/META-INF/resources/js/Grid/Grid.es';

const spritemap = 'icons.svg';

const GridWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<Grid {...props} />
	</PageProvider>
);

describe('Grid', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;
	});

	afterEach(cleanup);

	beforeAll(() => {

		// eslint-disable-next-line no-console
		console.warn = (...args) => {
			if (/DataProvider: Trying/.test(args[0])) {
				return;
			}
			originalWarn.call(console, ...args);
		};
	});

	beforeEach(() => {
		fetch.mockResponseOnce(JSON.stringify({}));
	});

	it('emits a fieldBlurred event when blurring the radio input', () => {
		const handleFieldBlurred = jest.fn();

		const {container} = render(
			<GridWithProvider
				columns={[
					{
						label: 'col1',
						value: 'colFieldId1',
					},
					{
						label: 'col2',
						value: 'colFieldId2',
					},
				]}
				name="name"
				onBlur={handleFieldBlurred}
				readOnly={false}
				rows={[
					{
						label: 'row1',
						value: 'rowFieldId1',
					},
					{
						label: 'row2',
						value: 'rowFieldId2',
					},
				]}
				spritemap={spritemap}
			/>
		);

		const radioInputElement = container.querySelector(
			'input[value][type="radio"][name="name_rowFieldId1"]:not([value="colFieldId2"])'
		);

		fireEvent.blur(radioInputElement);

		expect(handleFieldBlurred).toHaveBeenCalled();
	});

	it('emits a fieldEdited event when changing the state of radio input', () => {
		const handleFieldEdited = jest.fn();

		const {container} = render(
			<GridWithProvider
				columns={[
					{
						label: 'col1',
						value: 'colFieldId1',
					},
					{
						label: 'col2',
						value: 'colFieldId2',
					},
				]}
				name="name"
				onChange={handleFieldEdited}
				readOnly={false}
				rows={[
					{
						label: 'row1',
						value: 'rowFieldId1',
					},
					{
						label: 'row2',
						value: 'rowFieldId2',
					},
				]}
				spritemap={spritemap}
			/>
		);

		const radioInputElement = container.querySelector(
			'input[value][type="radio"][name="name_rowFieldId1"]:not([value="colFieldId2"])'
		);

		userEvent.click(radioInputElement);

		expect(handleFieldEdited).toHaveBeenCalled();
	});

	it('emits a fieldFocused event when focusing a radio input', () => {
		const handleFieldFocused = jest.fn();

		const {container} = render(
			<GridWithProvider
				columns={[
					{
						label: 'col1',
						value: 'colFieldId1',
					},
					{
						label: 'col2',
						value: 'colFieldId2',
					},
				]}
				name="name"
				onFocus={handleFieldFocused}
				readOnly={false}
				rows={[
					{
						label: 'row1',
						value: 'rowFieldId1',
					},
					{
						label: 'row2',
						value: 'rowFieldId2',
					},
				]}
				spritemap={spritemap}
			/>
		);

		const radioInputElement = container.querySelector(
			'input[value][type="radio"][name="name_rowFieldId1"]:not([value="colFieldId2"])'
		);

		fireEvent.focus(radioInputElement);

		expect(handleFieldFocused).toHaveBeenCalled();
	});

	it('has a label', () => {
		const {container} = render(
			<GridWithProvider label="label" spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('has a tip', () => {
		const {container} = render(
			<GridWithProvider spritemap={spritemap} tip="Type something" />
		);

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(
			<GridWithProvider id="Id" spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('is not editable', () => {
		const {container} = render(
			<GridWithProvider readOnly={true} spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<GridWithProvider required={false} spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders columns', () => {
		const {container} = render(
			<GridWithProvider
				columns={[
					{
						label: 'col1',
						value: 'fieldId',
					},
					{
						label: 'col2',
						value: 'fieldId',
					},
				]}
				spritemap={spritemap}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders data-option-reference for rows and columns', () => {
		const columns = [
			{reference: 'col1OptionReference'},
			{reference: 'col2OptionReference'},
		];
		const rows = [
			{reference: 'row1OptionReference'},
			{reference: 'row2OptionReference'},
		];

		const {container} = render(
			<GridWithProvider columns={columns} rows={rows} />
		);

		columns.forEach((column) => {
			rows.forEach((row) => {
				const radioInputElement = container.querySelector(
					`input[value][type="radio"][data-option-reference-column=${column.reference}][data-option-reference-row=${row.reference}]`
				);

				expect(radioInputElement).toBeTruthy();
			});
		});
	});

	it('renders Label if showLabel is true', () => {
		const {container} = render(
			<GridWithProvider label="text" showLabel spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders no columns when columns comes empty', () => {
		const {container} = render(
			<GridWithProvider columns={[]} spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders no rows when row comes empty', () => {
		const {container} = render(
			<GridWithProvider rows={[]} spritemap={spritemap} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders rows', () => {
		const {container} = render(
			<GridWithProvider
				rows={[
					{
						label: 'row1',
						value: 'fieldId',
					},
					{
						label: 'row2',
						value: 'fieldId',
					},
				]}
				spritemap={spritemap}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
