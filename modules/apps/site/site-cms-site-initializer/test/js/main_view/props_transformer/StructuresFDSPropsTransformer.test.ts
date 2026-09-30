/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import StructuresFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/StructuresFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

describe('StructuresFDSPropsTransformer', () => {
	it('marks the delete item action with the danger class', () => {
		const [deleteAction, importAction] = StructuresFDSPropsTransformer({
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'import'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(importAction.className).toBeUndefined();
	});
});
