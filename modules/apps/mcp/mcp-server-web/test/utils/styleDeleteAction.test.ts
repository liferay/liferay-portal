/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import styleDeleteAction from '../../src/main/resources/META-INF/resources/js/utils/styleDeleteAction';

describe('styleDeleteAction', () => {
	it('leaves the other actions as they are', () => {
		const action = {data: {id: 'edit'}, label: 'Edit'};

		expect(styleDeleteAction(action)).toBe(action);
	});

	it('marks the delete action as dangerous', () => {
		expect(
			styleDeleteAction({data: {id: 'delete'}, label: 'Delete'})
		).toEqual({
			className: 'text-danger',
			data: {id: 'delete'},
			label: 'Delete',
		});
	});
});
