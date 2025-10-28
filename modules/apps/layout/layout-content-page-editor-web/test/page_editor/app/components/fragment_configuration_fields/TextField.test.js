/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {TextField} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/TextField';

const INPUT_NAME = 'test';

const renderTextField = (typeOptions = {}, onValueSelect = () => {}) =>
	render(
		<TextField
			field={{label: INPUT_NAME, name: INPUT_NAME, typeOptions}}
			onValueSelect={onValueSelect}
			value=""
		/>
	);

describe('TextField', () => {
	it('renders an input of type text by default', () => {
		const {getByLabelText, queryByText} = renderTextField();

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('text');
		expect(
			queryByText('you-have-entered-invalid-data')
		).not.toBeInTheDocument();
	});

	it('renders an input of type url when validation type url is defined', () => {
		const {getByLabelText} = renderTextField({validation: {type: 'url'}});

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('url');
	});

	it('renders an input of type number when validation type number is defined', () => {
		const {getByLabelText} = renderTextField({
			validation: {type: 'number'},
		});

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('number');
	});

	it('renders an input of type email when validation type email is defined', () => {
		const {getByLabelText} = renderTextField({validation: {type: 'email'}});

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('email');
	});

	it('renders an input with the pattern attribute if validation type is pattern', () => {
		const regexp = '[0-9]*';

		const {getByLabelText} = renderTextField({
			validation: {regexp, type: 'pattern'},
		});

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('text');
		expect(input.pattern).toBe(regexp);
	});

	it('renders an input with the maxLength/minLength attributes when defined in the validation', () => {
		const {getByLabelText} = renderTextField({
			validation: {maxLength: 3, minLength: 0, type: 'text'},
		});

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('text');
		expect(input.maxLength).toEqual(3);
		expect(input.minLength).toEqual(0);
	});

	it('renders an input with max/min attributes when type is number and it is defined in the validation', () => {
		const {getByLabelText} = renderTextField({
			validation: {max: 3, min: 0, type: 'number'},
		});

		const input = getByLabelText(INPUT_NAME);

		expect(input).toBeInTheDocument();
		expect(input.type).toBe('number');
		expect(input.max).toEqual('3');
		expect(input.min).toEqual('0');
	});

	it('shows the error message defined in the typeoptions when the value is not valid', async () => {
		const errorMessage = 'bad';

		const {getByLabelText, getByText} = renderTextField({
			validation: {errorMessage, max: 3, min: 0, type: 'number'},
		});

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, '5');

		expect(getByText(errorMessage)).toBeInTheDocument();
	});

	it('shows the default error message when no one is provided', async () => {
		const {getByLabelText, getByText} = renderTextField({
			validation: {max: 3, min: 0, type: 'number'},
		});

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, '5');

		expect(getByText('you-have-entered-invalid-data')).toBeInTheDocument();
	});

	it('shows the error message defined in the type options when validation type email and value is not valid', async () => {
		const errorMessage = 'Please enter a valid URL with 14-30 characters';

		const {getByLabelText, getByText, queryByText} = renderTextField({
			validation: {
				errorMessage,
				maxLength: 30,
				minLength: 14,
				type: 'email',
			},
		});

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, 't');
		fireEvent.blur(input);

		expect(getByText(errorMessage)).toBeInTheDocument();

		await userEvent.clear(input);
		await userEvent.type(input, 'giannis.antetokounmpo@liferay.com');
		fireEvent.blur(input);

		expect(queryByText(errorMessage)).toBeInTheDocument();
	});

	it('shows the error message defined in the type options when validation type url and value is not valid', async () => {
		const errorMessage = 'Please enter a valid URL with 22-35 characters';

		const {getByLabelText, getByText, queryByText} = renderTextField({
			validation: {
				errorMessage,
				maxLength: 35,
				minLength: 22,
				type: 'url',
			},
		});

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, 'h');

		expect(getByText(errorMessage)).toBeInTheDocument();

		await userEvent.clear(input);
		await userEvent.type(input, 'https://giannisantetokounmpo.liferay.com');

		expect(queryByText(errorMessage)).not.toBeInTheDocument();
	});

	it('calls the onValueSelect callback when the input changes', async () => {
		const onValueSelect = jest.fn();
		const {getByLabelText} = renderTextField({}, onValueSelect);

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, 'something');

		fireEvent.blur(input, {event: {target: {checkValidity: () => true}}});

		expect(onValueSelect).toBeCalledWith(INPUT_NAME, 'something');
	});

	it('calls the onValueSelect callback when Enter key is pressed', async () => {
		const onValueSelect = jest.fn();
		const {getByLabelText} = renderTextField({}, onValueSelect);

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, 'something');

		fireEvent.keyDown(input, {
			key: 'Enter',
			target: {
				checkValidity: () => true,
			},
		});

		expect(onValueSelect).toBeCalledWith(INPUT_NAME, 'something');
	});

	it('does not call the onValueSelect callback when the input is not valid', async () => {
		const onValueSelect = jest.fn();
		const {getByLabelText} = renderTextField(
			{validation: {max: 10, type: 'number'}},
			onValueSelect
		);

		const input = getByLabelText(INPUT_NAME);

		await userEvent.clear(input);
		await userEvent.type(input, '12');

		fireEvent.blur(input);

		expect(onValueSelect).not.toBeCalled();
	});
});
