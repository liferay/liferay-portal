/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import EditObjectDetails from '../../components/ObjectDetails/EditObjectDetails';

const editObjectDetailsProps = {
	backURL: '',
	companies: [],
	dbTableName: '',
	hasPublishObjectPermission: false,
	hasUpdateObjectDefinitionPermission: false,
	isApproved: false,
	isEnableObjectEntrySchedule: false,
	isRootDescendantNode: false,
	isRootNode: false,
	label: {},
	learnResources: {},
	nonRelationshipObjectFieldsInfo: [],
	objectDefinitionExternalReferenceCode: '123',
	objectDefinitionId: 1,
	pluralLabel: {},
	portletNamespace: '',
	shortName: '',
	sites: [],
	storageTypes: [],
};

const renderComponent = (customProps = {}) => {
	render(<EditObjectDetails {...editObjectDetailsProps} {...customProps} />);
};

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	fetch: jest.fn(() =>
		Promise.resolve({
			json: () => Promise.resolve({}),
		})
	),
}));

afterAll(() => {
	jest.restoreAllMocks();
});

describe('inheritance alert', () => {
	it('is hidden when the object definition is the root', async () => {
		const label = Math.random();

		renderComponent({isRootNode: true, label: {en_US: label}});

		await waitFor(async () => {
			return expect(await screen.findByText(label)).toBeVisible();
		});

		const inheritanceObjectDefinitionAlert = screen.queryByText(
			/inheritance-is-enabled-for-at-least-one-relationship/i
		);

		expect(inheritanceObjectDefinitionAlert).toBeNull();
	});

	it('is hidden when the object definition does not have a root object definition', async () => {
		const label = Math.random();

		renderComponent({
			isRootDescendantNode: false,
			isRootNode: false,
			label: {en_US: label},
		});

		await waitFor(async () => {
			return expect(await screen.findByText(label)).toBeVisible();
		});

		const inheritanceObjectDefinitionAlert = screen.queryByText(
			/inheritance-is-enabled-for-at-least-one-relationship/i
		);

		expect(inheritanceObjectDefinitionAlert).toBeNull();
	});

	it('is visible when the object definition is a descendant of another object definition', async () => {
		renderComponent({isRootDescendantNode: true, isRootNode: false});

		const inheritanceObjectDefinitionAlert = await screen.findByText(
			/inheritance-is-enabled-for-at-least-one-relationship/i
		);

		expect(inheritanceObjectDefinitionAlert).toBeVisible();
	});
});
