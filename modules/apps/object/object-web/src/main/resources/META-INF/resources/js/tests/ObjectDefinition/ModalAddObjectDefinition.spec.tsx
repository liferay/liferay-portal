/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {ModalAddObjectDefinition} from '../../components/ViewObjectDefinitions/ModalAddObjectDefinition';

describe('ModalAddObjectDefinition', () => {
	beforeAll(() => {
		global.Liferay = {
			...global.Liferay,
			FeatureFlags: {
				...global.Liferay?.FeatureFlags,
				'LPS-135430': true,
			},
		};
	});

	it('displays the proxy warning alert when a non-default storage type is selected', async () => {
		const learnResourceContext = {
			'object-web': {
				'managing-data-from-external-systems': {
					en_US: {
						message: 'Learn more',
						url: 'https://learn.liferay.com/object-web/data-management',
					},
				},
			},
		};

		const mockStorageTypes = [
			{label: 'Default', value: 'default'},
			{label: 'Salesforce', value: 'salesforce'},
		];

		render(
			<ModalAddObjectDefinition
				handleOnClose={jest.fn()}
				learnResourceContext={learnResourceContext}
				objectDefinitionsStorageTypes={mockStorageTypes}
			/>
		);

		fireEvent.click(await screen.findByRole('combobox'));

		fireEvent.click(await screen.findByText('Salesforce'));

		expect(
			screen.getByText('proxy-objects-have-some-known-limitations')
		).toBeVisible();
	});
});
