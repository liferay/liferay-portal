/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

import {getDataSetProps} from '../../components/ListTypeDefinition/ListTypeTable';

describe('getDataSetProps', () => {
	beforeAll(() => {
		global.Liferay = {
			...global.Liferay,
			FeatureFlags: {
				...global.Liferay?.FeatureFlags,
				'LPD-24055': true,
			},
		};
	});

	it('displays the add entry button when a system picklist is selected', async () => {
		const fireModal = () => {};
		const pickListId = -1;
		const readOnly = false;
		const setValues = () => {};
		const values = {system: true};

		const result = getDataSetProps(
			fireModal,
			pickListId,
			readOnly,
			setValues,
			values
		);

		const primaryItemsResult = JSON.stringify(
			result?.creationMenu?.primaryItems
		);

		const expectedPrimaryItems = JSON.stringify([
			{
				href: 'handleAddItems',
				label: 'add-item',
				target: 'event',
				type: 'item',
			},
		]);

		expect(primaryItemsResult).toBe(expectedPrimaryItems);
	});
});
