/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import removeDSRequestStatusColumn from '../../../src/main/resources/META-INF/resources/components/ds_request_details/removeDSRequestStatusColumn';

describe('removeDSRequestStatusColumn', () => {
	it('removes only the DS request status column from every view', () => {
		const views = [
			{
				name: 'table',
				schema: {
					fields: [
						{fieldName: 'title'},
						{fieldName: 'dsRequestStatus'},
						{fieldName: 'dateModified'},
					],
				},
			},
		];

		expect(removeDSRequestStatusColumn(views)).toEqual([
			{
				name: 'table',
				schema: {
					fields: [{fieldName: 'title'}, {fieldName: 'dateModified'}],
				},
			},
		]);
		expect(views[0].schema.fields).toHaveLength(3);
	});
});
