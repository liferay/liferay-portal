/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import {IItemsActions} from '../../src/main/resources/META-INF/resources';
import QuickActions from '../../src/main/resources/META-INF/resources/actions/QuickActions';

const testActions: IItemsActions[] = [
	{
		href: '/o/data-test-endpoint/{id}',
		icon: 'link',
		label: 'Quick Action 1',
		target: 'link',
	},
	{
		href: '/home',
		icon: 'home',
		label: 'Quick Action 2',
		target: 'link',
	},
];
const testItemData = {
	creator: {
		additionalName: '',
		contentType: 'UserAccount',
		familyName: 'Test',
		givenName: 'Test',
		id: 11111,
		name: 'Test Test',
	},
	dateCreated: '2024-01-31T09:36:32Z',
	dateModified: '2024-01-31T09:36:32Z',
	id: 38212,
	label: 'id',
	label_i18n: {
		en_US: 'id',
	},
	name: 'id',
	renderer: 'default',
	sortable: true,
	status: {
		code: 0,
		label: 'approved',
		label_i18n: 'Approved',
	},
	taxonomyCategoryBriefs: [],
	type: 'integer',
};

const clickCallback = jest.fn();

describe('QuickActions', () => {
	it('displays a list of available actions', async () => {
		render(
			<QuickActions
				actions={testActions}
				itemData={testItemData}
				itemId={1}
				onClick={clickCallback}
			/>
		);

		const firstQuickAction = screen.getByRole('link', {
			name: 'Quick Action 1',
		});
		expect(firstQuickAction).toBeInTheDocument();
		expect(firstQuickAction.getAttribute('href')).toEqual(
			`/o/data-test-endpoint/${testItemData.id}`
		);

		await userEvent.click(firstQuickAction);

		expect(clickCallback).toHaveBeenCalledWith({
			action: testActions[0],
			event: expect.anything(),
			itemData: testItemData,
			itemId: 1,
		});
	});
});
