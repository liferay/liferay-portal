/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import PicklistPicker from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/PicklistPicker';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import {MockCacheProvider} from '../mocks/MockCacheProvider';

const DEFAULT_PICKLISTS = [
	{
		externalReferenceCode: '1',
		id: 1,
		listTypeEntries: [],
		name: 'papaya',
		name_i18n: {en_US: 'Papaya'},
	},
];

const renderComponent = ({picklistId}: {picklistId?: number} = {}) => {
	return render(
		<MockCacheProvider picklists={DEFAULT_PICKLISTS}>
			<PicklistPicker field={{picklistId} as Field} />
		</MockCacheProvider>
	);
};

describe('PicklistPicker', () => {
	it('New Picklist link opens in a new tab', () => {
		renderComponent();

		expect(
			screen.getByRole('link', {name: /new-picklist/})
		).toHaveAttribute('target', '_blank');
	});

	it('Dropdown options open in another tab', async () => {
		renderComponent({picklistId: 1});

		await userEvent.click(screen.getByTitle('more-actions'));

		await waitFor(() => {
			const options = [
				screen.getByRole('menuitem', {name: /edit/}),
				screen.getByRole('menuitem', {name: /new-picklist/}),
			];

			options.forEach((option) => {
				expect(option).toHaveAttribute('target', '_blank');
			});
		});
	});
});
