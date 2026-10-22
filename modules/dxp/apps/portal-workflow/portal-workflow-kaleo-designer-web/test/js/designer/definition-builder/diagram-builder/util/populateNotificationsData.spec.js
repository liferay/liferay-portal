/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

import populateNotificationsData from '../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/util/populateNotificationsData';
import DeserializeUtil from '../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil';
import {
	foundUserAccounts,
	getSectionData,
	getUserReferencesDefinition,
	mockUserAccountsFetch,
} from '../../../../../utils/userAccounts';

describe('Populating the notifications of a task that notifies users', () => {
	const fetchSpy = mockUserAccountsFetch();

	afterAll(() => {
		fetchSpy.mockRestore();
	});

	it('Fetches the recipients of the notification and of the timer notification by external reference code or by ID', async () => {
		const deserializeUtil = new DeserializeUtil(
			getUserReferencesDefinition()
		);

		const elements = deserializeUtil.getElements();

		const setElements = jest.fn();

		populateNotificationsData(undefined, elements, setElements);

		await waitFor(() => {
			expect(setElements).toHaveBeenCalledTimes(2);
		});

		const task = elements.find((element) => element.id === 'Review');

		const sectionsData = foundUserAccounts.map((userAccount) =>
			expect.objectContaining(getSectionData(userAccount))
		);

		expect(task.data.notifications.recipients[0][0].sectionsData).toEqual(
			sectionsData
		);
		expect(
			task.data.taskTimers.timerNotifications[0].recipients[0][0]
				.sectionsData
		).toEqual(sectionsData);
	});
});
