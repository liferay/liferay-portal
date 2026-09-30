/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import RecycleBinFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/RecycleBinFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	getCMSItemSelectorGroupedFilters: jest.fn(() => []),
}));

describe('RecycleBinFDSPropsTransformer', () => {
	it('marks the delete item action with the danger class', () => {
		const [deleteAction, restoreAction] = RecycleBinFDSPropsTransformer({
			additionalProps: {},
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'restore'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(restoreAction.className).toBeUndefined();
	});
});
