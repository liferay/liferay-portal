/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import StructureUsagesFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/StructureUsagesFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

describe('StructureUsagesFDSPropsTransformer', () => {
	it('marks the delete item action with the danger class', () => {
		const [deleteAction, editAction] = StructureUsagesFDSPropsTransformer({
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'edit'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(editAction.className).toBeUndefined();
	});
});
