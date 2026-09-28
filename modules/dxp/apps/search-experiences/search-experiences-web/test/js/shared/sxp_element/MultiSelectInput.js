/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import MultiSelectInput from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/sxp_element/MultiSelectInput';

import '@testing-library/jest-dom';

const onSetFieldValue = jest.fn();

function ControlledMultiSelectInput() {
	const [value, setValue] = useState([]);

	return (
		<MultiSelectInput
			id="tags"
			label="Tags"
			name="tags"
			setFieldTouched={() => {}}
			setFieldValue={(name, newValue) => {
				onSetFieldValue(name, newValue);

				setValue(newValue);
			}}
			value={value}
		/>
	);
}

describe('MultiSelectInput', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('adds a tag when pressing enter', async () => {
		render(<ControlledMultiSelectInput />);

		const input = screen.getByLabelText('Tags');

		await userEvent.type(input, 'tag1');

		// Checks submission event is default prevented

		expect(fireEvent.keyDown(input, {key: 'Enter'})).toBe(false);

		expect(screen.getByText('tag1')).toBeInTheDocument();
		expect(input).toHaveValue('');
	});

	it('does not submit the form when pressing enter with an empty input', () => {
		render(<ControlledMultiSelectInput />);

		expect(
			fireEvent.keyDown(screen.getByLabelText('Tags'), {key: 'Enter'})
		).toBe(false);
	});

	it('trims the tag when pressing enter', async () => {
		render(<ControlledMultiSelectInput />);

		const input = screen.getByLabelText('Tags');

		await userEvent.type(input, '  tag1  ');

		fireEvent.keyDown(input, {key: 'Enter'});

		expect(onSetFieldValue).toHaveBeenLastCalledWith('tags', [
			expect.objectContaining({label: 'tag1', value: 'tag1'}),
		]);
	});

	it('trims the tag when the input loses focus', async () => {
		render(<ControlledMultiSelectInput />);

		const input = screen.getByLabelText('Tags');

		await userEvent.type(input, '  tag1  ');

		fireEvent.blur(input);

		expect(onSetFieldValue).toHaveBeenLastCalledWith('tags', [
			{label: 'tag1', value: 'tag1'},
		]);
	});

	it('does not add a tag when the input only has spaces', async () => {
		render(<ControlledMultiSelectInput />);

		const input = screen.getByLabelText('Tags');

		await userEvent.type(input, '   ');

		fireEvent.blur(input);

		expect(onSetFieldValue).not.toHaveBeenCalled();
	});
});
