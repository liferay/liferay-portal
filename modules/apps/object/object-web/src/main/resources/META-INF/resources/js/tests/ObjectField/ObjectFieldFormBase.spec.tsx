/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, within} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';

import ObjectFieldFormBase from '../../components/ObjectField/ObjectFieldFormBase';

const OBJECT_RELATIONSHIPS_URL_REGEX =
	/\/o\/object-admin\/v1\.0\/object-relationships\/.+/;

const objectRelationship = {
	id: 1,
	label: {en_US: 'Test Relationship'},
	name: 'testRelationship',
	objectDefinitionExternalReferenceCode1: '123',
	objectDefinitionExternalReferenceCode2: '456',
	objectDefinitionId1: 1,
	objectDefinitionId2: 2,
	objectDefinitionName2: 'Test Object Definition 2',
	reverse: false,
};

const objectFieldFormBaseDefaultProps = {
	baseResourceURL: 'baseResourceURL',
	errors: {},
	handleChange: () => {},
	learnResources: {
		leanrResourceName: {general: {en_US: {message: 'Test Message'}}},
	},
	objectField: {
		businessType: 'Relationship' as ObjectFieldBusinessTypeName,
	},
	objectFieldBusinessTypesInfo: [],
	objectRelationshipId: 1,
	setValues: () => {},
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
	delete (window as any).location;
	jest.restoreAllMocks();
});

afterEach(() => {
	fetchMock.restore();
});

beforeAll(() => {
	(window as any).location = {
		href: 'http://localhost/url',
	};
});

beforeEach(() => {
	fetchMock.get('http://localhost/url', {});
});

describe('Formula field business type', () => {
	const formulaProps = {
		...objectFieldFormBaseDefaultProps,
		objectField: {
			businessType: 'Formula' as ObjectFieldBusinessTypeName,
		},
		objectRelationshipId: undefined,
	};

	it('does not render the mandatory toggle', () => {
		render(<ObjectFieldFormBase {...formulaProps} />);

		expect(
			screen.queryByRole('switch', {name: 'mandatory'})
		).not.toBeInTheDocument();
	});

	it('renders the output field as required', () => {
		render(<ObjectFieldFormBase {...formulaProps} />);

		const outputLabel = screen.getByText('output', {
			exact: false,
			selector: 'label',
		});

		expect(within(outputLabel).getByText('mandatory')).toBeInTheDocument();
	});
});

describe('Object field description', () => {
	const metadataObjectFieldNames = [
		'createDate',
		'creator',
		'displayDate',
		'expirationDate',
		'externalReferenceCode',
		'id',
		'modifiedDate',
		'reviewDate',
		'status',
	];

	const descriptionProps = {
		...objectFieldFormBaseDefaultProps,
		editingObjectField: true,
		metadataObjectFieldNames,
		objectField: {
			businessType: 'Text' as ObjectFieldBusinessTypeName,
			name: 'claimNumber',
		},
		objectRelationshipId: undefined,
	};

	beforeEach(() => {
		Liferay.FeatureFlags['LPD-80279'] = true;
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPD-80279'] = false;
	});

	const unmodifiableSystemObjectDefinition = {
		modifiable: false,
		system: true,
	} as ObjectDefinition;

	it('does not render for a system object field of an unmodifiable system object definition', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				objectDefinition={unmodifiableSystemObjectDefinition}
				objectField={{
					businessType: 'Text' as ObjectFieldBusinessTypeName,
					name: 'emailAddress',
					system: true,
				}}
			/>
		);

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not render when the feature flag is disabled', () => {
		Liferay.FeatureFlags['LPD-80279'] = false;

		render(<ObjectFieldFormBase {...descriptionProps} />);

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not render when the object field is a metadata field', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				objectField={{
					businessType: 'Date' as ObjectFieldBusinessTypeName,
					name: 'createDate',
				}}
			/>
		);

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('does not render when the object field is being created', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				editingObjectField={false}
			/>
		);

		expect(screen.queryByLabelText('description')).not.toBeInTheDocument();
	});

	it('is disabled for a relationship object field', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				objectField={{
					businessType: 'Relationship' as ObjectFieldBusinessTypeName,
					name: 'r_warrantyClaimServiceVisits_c_warrantyClaimId',
				}}
				objectRelationshipId={0}
			/>
		);

		expect(screen.getByLabelText('description')).toBeDisabled();
	});

	it('is disabled when the form is read only', () => {
		render(<ObjectFieldFormBase {...descriptionProps} readOnly />);

		expect(screen.getByLabelText('description')).toBeDisabled();
	});

	it('renders for a custom object field of an unmodifiable system object definition', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				objectDefinition={unmodifiableSystemObjectDefinition}
				objectField={{
					businessType: 'Text' as ObjectFieldBusinessTypeName,
					name: 'claimNumber',
					system: false,
				}}
			/>
		);

		expect(screen.getByLabelText('description')).toBeInTheDocument();
	});

	it('renders for a non metadata system object field', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				objectField={{
					businessType: 'Text' as ObjectFieldBusinessTypeName,
					name: 'author',
					system: true,
				}}
			/>
		);

		expect(screen.getByLabelText('description')).toBeInTheDocument();
	});

	it('renders the authored value for the default locale', () => {
		render(
			<ObjectFieldFormBase
				{...descriptionProps}
				objectField={{
					...descriptionProps.objectField,
					description: {en_US: 'The insurer claim reference.'},
				}}
			/>
		);

		expect(screen.getByLabelText('description')).toHaveValue(
			'The insurer claim reference.'
		);
	});
});

describe('when the root model feature is enabled', () => {
	describe('the mandatory toggle', () => {
		it('does not render help text when relationship does not belong to a root model structure', async () => {
			fetchMock.get(OBJECT_RELATIONSHIPS_URL_REGEX, {
				...objectRelationship,
				deletionType: 'cascade',
				edge: false,
			});

			render(
				<ObjectFieldFormBase {...objectFieldFormBaseDefaultProps} />
			);

			expect(
				screen.queryByLabelText('help-text')
			).not.toBeInTheDocument();
		});

		it('is disabled when deletionType is "disassociate"', async () => {
			fetchMock.get(OBJECT_RELATIONSHIPS_URL_REGEX, {
				...objectRelationship,
				deletionType: 'disassociate',
				edge: false,
			});

			render(
				<ObjectFieldFormBase {...objectFieldFormBaseDefaultProps} />
			);

			const mandatoryToggle = await screen.findByRole('switch', {
				name: 'mandatory',
			});

			expect(mandatoryToggle).toBeDisabled();
		});

		it('is disabled when deletionType is not "disassociate" and relationship belongs to a root model structure', async () => {
			fetchMock.get(OBJECT_RELATIONSHIPS_URL_REGEX, {
				...objectRelationship,
				deletionType: 'cascade',
				edge: true,
			});

			render(
				<ObjectFieldFormBase {...objectFieldFormBaseDefaultProps} />
			);

			const mandatoryToggle = await screen.findByRole('switch', {
				name: 'mandatory',
			});

			expect(mandatoryToggle).toBeDisabled();
		});

		it('is enabled when deletionType is not "disassociate" and relationship does not belong to a root model structure', async () => {
			fetchMock.get(OBJECT_RELATIONSHIPS_URL_REGEX, {
				...objectRelationship,
				deletionType: 'cascade',
				edge: false,
			});

			render(
				<ObjectFieldFormBase {...objectFieldFormBaseDefaultProps} />
			);

			const mandatoryToggle = await screen.findByRole('switch', {
				name: 'mandatory',
			});

			expect(mandatoryToggle).toBeEnabled();
		});

		it('renders help text and popover when relationship belongs to a root model structure', async () => {
			fetchMock.get(OBJECT_RELATIONSHIPS_URL_REGEX, {
				...objectRelationship,
				deletionType: 'cascade',
				edge: true,
			});

			render(
				<ObjectFieldFormBase {...objectFieldFormBaseDefaultProps} />
			);

			const icon = await screen.findByLabelText('help-text');

			expect(icon).toBeInTheDocument();

			expect(
				screen.queryByText('inheritance-relationships-fields')
			).not.toBeInTheDocument();

			fireEvent.mouseOver(icon);

			expect(
				await screen.findByText('inheritance-relationships-fields')
			).toBeInTheDocument();
		});
	});
});
