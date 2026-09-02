/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';

import {ObjectRelationshipFormBase} from '../../components/ObjectRelationship/ObjectRelationshipFormBase';

const OBJECT_DEFINITION_URL_REGEX =
	/\/o\/object-admin\/v1\.0\/object-definitions\/by-external-reference-code\/.+/;

const OBJECT_DEFINITIONS_URL_REGEX =
	/\/o\/object-admin\/v1\.0\/object-definitions\?.*/;

const objectDefinition = {
	externalReferenceCode: '123',
	id: 1,
	label: {en_US: 'Warranty Claim'},
	name: 'WarrantyClaim',
};

const objectRelationshipFormBaseDefaultProps = {
	baseResourceURL: 'baseResourceURL',
	editingObjectRelationship: true,
	errors: {},
	handleChange: () => {},
	learnResources: {},
	objectDefinitionExternalReferenceCode1: '123',
	onChangeInheritanceCheckbox: () => {},
	setValues: () => {},
	values: {
		deletionType: 'prevent',
		label: {en_US: 'Warranty Claim - Service Visits'},
		name: 'warrantyClaimServiceVisits',
		type: 'oneToMany' as ObjectRelationshipType,
	},
};

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	createResourceURL: jest.fn(() => {
		return {
			href: 'http://localhost/url',
		};
	}),
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

afterAll(() => {
	jest.restoreAllMocks();
});

afterEach(() => {
	fetchMock.restore();
});

beforeEach(() => {
	Liferay.FeatureFlags['LPD-80279'] = true;

	fetchMock.get('http://localhost/url', {
		objectRelationshipTypes: ['oneToMany'],
	});
	fetchMock.get(OBJECT_DEFINITION_URL_REGEX, objectDefinition);
	fetchMock.get(OBJECT_DEFINITIONS_URL_REGEX, {
		items: [objectDefinition],
		totalCount: 1,
	});
});

afterEach(() => {
	Liferay.FeatureFlags['LPD-80279'] = false;
});

const renderComponent = (customProps = {}) =>
	render(
		<ObjectRelationshipFormBase
			{...objectRelationshipFormBaseDefaultProps}
			{...customProps}
		/>
	);

describe('Object relationship description', () => {
	it('does not render for a system object relationship', async () => {
		renderComponent({
			values: {
				...objectRelationshipFormBaseDefaultProps.values,
				system: true,
			},
		});

		await screen.findByRole('textbox', {name: /name/i});

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not render when the feature flag is disabled', async () => {
		Liferay.FeatureFlags['LPD-80279'] = false;

		renderComponent();

		await screen.findByRole('textbox', {name: /name/i});

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not render when the object relationship is being created', async () => {
		renderComponent({editingObjectRelationship: false});

		await screen.findByRole('textbox', {name: /name/i});

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not submit on blur unless the form saves automatically', async () => {
		const onSubmit = jest.fn();

		renderComponent({onSubmit});

		const description = await screen.findByLabelText('description');

		fireEvent.blur(description);

		expect(onSubmit).not.toHaveBeenCalled();

		cleanup();

		renderComponent({autoSave: true, onSubmit});

		fireEvent.blur(await screen.findByLabelText('description'));

		await waitFor(() => expect(onSubmit).toHaveBeenCalled());
	});

	it('is disabled on the reverse side of the relationship', async () => {
		renderComponent({descriptionDisabled: true});

		await waitFor(() =>
			expect(screen.getByLabelText('description')).toBeDisabled()
		);
	});

	it('renders for an object relationship an administrator added', async () => {
		renderComponent({
			values: {
				...objectRelationshipFormBaseDefaultProps.values,
				system: false,
			},
		});

		await waitFor(() =>
			expect(screen.getByLabelText('description')).toBeInTheDocument()
		);
	});

	it('renders the help message explaining the English fallback', async () => {
		renderComponent({
			values: {
				...objectRelationshipFormBaseDefaultProps.values,
				system: false,
			},
		});

		await waitFor(() =>
			expect(
				screen.getByText(
					'provide-descriptive-text-used-only-by-ai-agents-and-api-consumers'
				)
			).toBeInTheDocument()
		);
	});

	it('shows the authored value for the default locale', async () => {
		renderComponent({
			values: {
				...objectRelationshipFormBaseDefaultProps.values,
				description: {en_US: 'Each claim has many service visits.'},
			},
		});

		await waitFor(() =>
			expect(screen.getByLabelText('description')).toHaveValue(
				'Each claim has many service visits.'
			)
		);
	});
});
