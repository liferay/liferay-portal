/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import LocationDefaultValueInput from '../../../components/ObjectField/DefaultValueFields/LocationDefaultValueInput';

const MADRID = {
	address: 'P.º de la Castellana, 280, 28046 Madrid, Spain',
	coordinates: {latitude: 40.4764291, longitude: -3.6858155},
};

const baseProps = {
	creationLanguageId: 'en_US' as Liferay.Language.Locale,
	label: 'default-value',
	required: true,
};

jest.mock('@liferay/object-js-components-web', () => {
	const React = require('react');

	return {
		LocationInput: ({onLocationChange, serializedValue}: any) => (
			<>
				<div data-testid="serializedValue">{serializedValue}</div>

				<button
					onClick={() =>
						onLocationChange({
							address:
								'P.º de la Castellana, 280, 28046 Madrid, Spain',
							coordinates: {
								latitude: 40.4764291,
								longitude: -3.6858155,
							},
						})
					}
					type="button"
				>
					pickLocation
				</button>
			</>
		),
		stringifyLocationValue: ({address, coordinates}: any) =>
			JSON.stringify({address, coordinates}),
	};
});

describe('LocationDefaultValueInput', () => {
	it('renders the label', () => {
		render(
			<LocationDefaultValueInput
				{...baseProps}
				setValues={jest.fn()}
				values={{objectFieldSettings: []}}
			/>
		);

		expect(screen.getByText('default-value')).toBeInTheDocument();
	});

	it('saves the picked location as the default value setting', () => {
		const onSubmit = jest.fn();
		const setValues = jest.fn();

		render(
			<LocationDefaultValueInput
				{...baseProps}
				onSubmit={onSubmit}
				setValues={setValues}
				values={{objectFieldSettings: []}}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'pickLocation'}));

		const expectedObjectFieldSettings = [
			{name: 'defaultValueType', value: 'inputAsValue'},
			{name: 'defaultValue', value: JSON.stringify(MADRID)},
		];

		expect(setValues).toHaveBeenCalledWith({
			objectFieldSettings: expectedObjectFieldSettings,
		});
		expect(onSubmit).toHaveBeenCalledWith({
			objectFieldSettings: expectedObjectFieldSettings,
		});
	});

	it('shows the stored default value on the map field', () => {
		render(
			<LocationDefaultValueInput
				{...baseProps}
				defaultValue={JSON.stringify(MADRID)}
				setValues={jest.fn()}
				values={{objectFieldSettings: []}}
			/>
		);

		expect(screen.getByTestId('serializedValue')).toHaveTextContent(
			MADRID.address
		);
	});
});
