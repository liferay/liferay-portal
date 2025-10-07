/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';

import ActionBuilder from '../../components/ObjectAction/tabs/ActionBuilder';

afterAll(() => {
	jest.restoreAllMocks();
});

afterEach(() => {
	fetchMock.restore();
});

beforeEach(() => {
	fetchMock.get(
		'/o/object-admin/v1.0/object-definitions/by-external-reference-code/',
		{
			body: {},
		}
	);
});

describe('The ActionBuilder component should', () => {
	it('display the enable condition checkbox for the onAfterLogin trigger', async () => {
		const values: Partial<ObjectAction> = {
			objectActionTriggerKey: 'onAfterLogin',
		};

		render(
			<ActionBuilder
				disableGroovyAction={true}
				errors={{}}
				isApproved={false}
				objectActionCodeEditorElements={[]}
				objectActionExecutors={[]}
				objectActionTriggers={[]}
				objectDefinitionExternalReferenceCode=""
				objectDefinitionId={0}
				objectDefinitionsRelationshipsURL=""
				objectFields={[]}
				scriptManagementConfigurationPortletURL=""
				setValues={jest.fn()}
				systemObject={false}
				validateExpressionURL=""
				values={values}
			/>
		);

		expect(screen.getByText('enable-condition')).toBeInTheDocument();
	});
});
