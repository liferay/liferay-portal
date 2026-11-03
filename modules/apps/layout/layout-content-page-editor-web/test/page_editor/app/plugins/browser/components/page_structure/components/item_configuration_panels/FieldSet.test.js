/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {FieldSet} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/FieldSet';

const frontendJsWebMock = jest.requireMock('frontend-js-web');

describe('FieldSet', () => {
	beforeEach(() => {
		jest.spyOn(frontendJsWebMock, 'debounce').mockImplementation(
			(fn) => fn
		);
		jest.spyOn(frontendJsWebMock, 'loadModule');
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('collapses the panel when collapsed is true', () => {
		render(
			<FieldSet
				collapsed
				fields={[
					{
						dataType: 'string',
						label: 'First Field',
						name: 'firstField',
						type: 'text',
					},
				]}
				label="My Section"
				languageId="en_US"
				onValueSelect={jest.fn()}
				values={{}}
			/>
		);

		expect(
			screen.getByRole('button', {name: 'My Section'})
		).toHaveAttribute('aria-expanded', 'false');
	});

	it('expands the panel when collapsed is not set', () => {
		render(
			<FieldSet
				fields={[
					{
						dataType: 'string',
						label: 'First Field',
						name: 'firstField',
						type: 'text',
					},
				]}
				label="My Section"
				languageId="en_US"
				onValueSelect={jest.fn()}
				values={{}}
			/>
		);

		expect(
			screen.getByRole('button', {name: 'My Section'})
		).toHaveAttribute('aria-expanded', 'true');
	});

	it('renders the custom component when customComponentModule is present', async () => {
		frontendJsWebMock.loadModule.mockResolvedValue(() => (
			<div>Custom Component</div>
		));

		render(
			<FieldSet
				customComponentModule="{Comp} from module"
				fields={[]}
				languageId="en_US"
				onValueSelect={jest.fn()}
				values={{}}
			/>
		);

		await waitFor(() => {
			expect(screen.getByText('Custom Component')).toBeInTheDocument();
		});
	});

	it('shows error when loadModule fails', async () => {
		frontendJsWebMock.loadModule.mockRejectedValue(
			new Error('Failed to load')
		);

		render(
			<FieldSet
				customComponentModule="{Comp} from module"
				fields={[]}
				languageId="en_US"
				onValueSelect={jest.fn()}
				values={{}}
			/>
		);

		await waitFor(() => {
			expect(
				screen.getByText('an-unexpected-error-occurred')
			).toBeInTheDocument();
		});
	});

	it('renders normal fields with default values when customComponentModule is not present', () => {
		render(
			<FieldSet
				fields={[
					{
						dataType: 'string',
						defaultValue: 'default1',
						label: 'First Field',
						name: 'firstField',
						type: 'text',
					},
					{
						dataType: 'string',
						defaultValue: 'default2',
						label: 'Second Field',
						name: 'secondField',
						type: 'text',
					},
				]}
				languageId="en_US"
				onValueSelect={jest.fn()}
				values={{}}
			/>
		);

		expect(screen.getByLabelText('First Field')).toHaveValue('default1');
		expect(screen.getByLabelText('Second Field')).toHaveValue('default2');
		expect(frontendJsWebMock.loadModule).not.toHaveBeenCalled();
	});

	it('passes onValueSelect and values to the custom component', async () => {
		const mockOnValueSelect = jest.fn();
		const mockValues = {field1: 'val1', field2: 'val2'};

		frontendJsWebMock.loadModule.mockResolvedValue(
			({onValueSelect, values}) => {
				const [field1, setField1] = React.useState(values.field1 || '');
				const [field2, setField2] = React.useState(values.field2 || '');

				return (
					<div>
						<input
							aria-label="Field 1"
							onChange={(event) => {
								setField1(event.target.value);
								onValueSelect('field1', event.target.value);
							}}
							value={field1}
						/>

						<input
							aria-label="Field 2"
							onChange={(event) => {
								setField2(event.target.value);
								onValueSelect('field2', event.target.value);
							}}
							value={field2}
						/>
					</div>
				);
			}
		);

		render(
			<FieldSet
				customComponentModule="{Comp} from module"
				fields={[]}
				languageId="en_US"
				onValueSelect={mockOnValueSelect}
				values={mockValues}
			/>
		);

		await waitFor(() => {
			expect(screen.getByLabelText('Field 1')).toHaveValue('val1');
		});

		expect(screen.getByLabelText('Field 2')).toHaveValue('val2');

		await userEvent.clear(screen.getByLabelText('Field 1'));
		await userEvent.type(screen.getByLabelText('Field 1'), 'newVal1');

		expect(mockOnValueSelect).toHaveBeenCalledWith('field1', 'newVal1');

		await userEvent.clear(screen.getByLabelText('Field 2'));
		await userEvent.type(screen.getByLabelText('Field 2'), 'newVal2');

		expect(mockOnValueSelect).toHaveBeenCalledWith('field2', 'newVal2');
	});

	it('wraps in ClayPanel when label and customComponentModule are present', async () => {
		frontendJsWebMock.loadModule.mockResolvedValue(() => (
			<div>Panel Content</div>
		));

		render(
			<FieldSet
				customComponentModule="{Comp} from module"
				fields={[]}
				label="My Section"
				languageId="en_US"
				onValueSelect={jest.fn()}
				values={{}}
			/>
		);

		await waitFor(() => {
			expect(screen.getByText('Panel Content')).toBeInTheDocument();
		});

		expect(screen.getByText('My Section')).toBeInTheDocument();
	});
});
