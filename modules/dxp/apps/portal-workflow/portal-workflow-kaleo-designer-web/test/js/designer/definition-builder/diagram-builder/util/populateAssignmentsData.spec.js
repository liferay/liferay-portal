/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

import populateAssignmentsData from '../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/util/populateAssignmentsData';
import DeserializeUtil from '../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil';
import {
	foundUserAccounts,
	getSectionData,
	getUserReferencesDefinition,
	mockUserAccountsFetch,
} from '../../../../../utils/userAccounts';

describe('Populating the assignments of a task assigned to users', () => {
	const fetchSpy = mockUserAccountsFetch();

	afterAll(() => {
		fetchSpy.mockRestore();
	});

	it('Fetches each user by external reference code or by ID and drops the users it cannot find', async () => {
		const deserializeUtil = new DeserializeUtil(
			getUserReferencesDefinition()
		);

		const elements = deserializeUtil.getElements();

		const setElements = jest.fn();

		populateAssignmentsData(undefined, elements, setElements, jest.fn());

		await waitFor(() => {
			expect(setElements).toHaveBeenCalledTimes(1);
		});

		const task = elements.find((element) => element.id === 'Review');

		expect(task.data.assignments.sectionsData).toEqual(
			foundUserAccounts.map((userAccount) =>
				expect.objectContaining(getSectionData(userAccount))
			)
		);
	});
});
