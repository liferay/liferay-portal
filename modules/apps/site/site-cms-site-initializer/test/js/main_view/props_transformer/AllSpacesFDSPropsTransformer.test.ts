/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AllSpacesFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AllSpacesFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	EConfigInURLBehavior: {OFF: 'off'},
	replaceTokens: jest.fn(),
}));

describe('AllSpacesFDSPropsTransformer', () => {
	it('marks the delete item action with the danger class', () => {
		const [deleteAction, pinAction] = AllSpacesFDSPropsTransformer({
			additionalProps: {pinnedAssetLibraryIds: []},
			creationMenu: {primaryItems: []},
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'pin'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(pinAction.className).toBeUndefined();
	});
});
