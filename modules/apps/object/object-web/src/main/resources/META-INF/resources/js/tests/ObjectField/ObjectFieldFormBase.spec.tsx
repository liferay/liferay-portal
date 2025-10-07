/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';

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

describe('when the root model feature flag [LPD-34594] is disabled', () => {
	describe('the mandatory toggle', () => {
		beforeEach(() => {
			global.Liferay = {
				...global.Liferay,
				FeatureFlags: {
					...global.Liferay?.FeatureFlags,
					'LPD-34594': false,
				},
			};
		});

		it('does not render help text', async () => {
			fetchMock.get(OBJECT_RELATIONSHIPS_URL_REGEX, {
				...objectRelationship,
				deletionType: 'cascade',
				edge: true,
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

		it('is enabled when deletionType is not "disassociate"', async () => {
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

			expect(mandatoryToggle).toBeEnabled();
		});
	});
});

describe('when the root model feature flag [LPD-34594] is enabled', () => {
	describe('the mandatory toggle', () => {
		beforeEach(() => {
			global.Liferay = {
				...global.Liferay,
				FeatureFlags: {
					...global.Liferay?.FeatureFlags,
					'LPD-34594': true,
				},
			};
		});

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
