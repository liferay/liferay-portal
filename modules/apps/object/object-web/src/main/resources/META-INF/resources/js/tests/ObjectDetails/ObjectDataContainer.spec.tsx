/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ObjectDataContainer} from '../../components/ObjectDetails/ObjectDataContainer';

const objectDataContainerDefaultProps = {
	dbTableName: 'WarrantyClaim',
	errors: {},
	handleChange: () => {},
	hasUpdateObjectDefinitionPermission: true,
	isApproved: true,
	setValues: () => {},
	values: {
		active: true,
		label: {en_US: 'Warranty Claim'},
		modifiable: true,
		name: 'WarrantyClaim',
		pluralLabel: {en_US: 'Warranty Claims'},
		system: false,
	},
};

const renderComponent = (customProps = {}) =>
	render(
		<ObjectDataContainer
			{...objectDataContainerDefaultProps}
			{...customProps}
		/>
	);

describe('Object definition description', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-80279'] = true;
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPD-80279'] = false;
	});

	it('does not render when the feature flag is disabled', () => {
		Liferay.FeatureFlags['LPD-80279'] = false;

		renderComponent();

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not render when the object definition is an unmodifiable system object', () => {
		renderComponent({
			values: {
				...objectDataContainerDefaultProps.values,
				modifiable: false,
				system: true,
			},
		});

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('is disabled without the update object definition permission', () => {
		renderComponent({hasUpdateObjectDefinitionPermission: false});

		expect(screen.getByLabelText('description')).toBeDisabled();
	});

	it('renders for a modifiable system object', () => {
		renderComponent({
			values: {
				...objectDataContainerDefaultProps.values,
				modifiable: true,
				system: true,
			},
		});

		expect(screen.getByLabelText('description')).toBeInTheDocument();
	});

	it('reports every typed character to the form', async () => {
		const setValues = jest.fn();

		renderComponent({setValues});

		await userEvent.type(screen.getByLabelText('description'), 'A');

		expect(setValues).toHaveBeenCalledWith({description: {en_US: 'A'}});
	});

	it('shows the authored value for the default locale', () => {
		renderComponent({
			values: {
				...objectDataContainerDefaultProps.values,
				description: {en_US: 'Claims raised against a warranty.'},
			},
		});

		expect(screen.getByLabelText('description')).toHaveValue(
			'Claims raised against a warranty.'
		);
	});
});
