/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {formIsRestricted} from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/formIsRestricted';
import getMockFormItem from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/getMockFormItem';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config',
	() => ({
		config: {
			formTypes: [
				{
					isRestricted: false,
					label: 'Form Type 1',
					value: '11111',
				},
				{
					isRestricted: true,
					label: 'Form Type 2',
					value: '22222',
				},
			],
		},
	})
);

describe('formIsRestricted', () => {
	it('checks if the item mapped to the form has permissions', () => {
		expect(
			formIsRestricted(
				getMockFormItem({
					config: {classNameId: '11111', classTypeId: '0'},
					itemId: 'form-1',
				})
			)
		).toBe(false);
		expect(
			formIsRestricted(
				getMockFormItem({
					config: {classNameId: '22222', classTypeId: '0'},
					itemId: 'form-1',
				})
			)
		).toBe(true);
	});
});
