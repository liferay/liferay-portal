/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getDashboardAssetListFDSProps from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/getDashboardAssetListFDSProps';

jest.mock('@liferay/frontend-data-set-web', () => ({
	EConfigInURLBehavior: {OFF: 'off'},
	replaceTokens: jest.fn(),
}));

describe('getDashboardAssetListFDSProps', () => {
	it('marks the delete item action with the danger class', () => {
		const [deleteAction, shareAction] = getDashboardAssetListFDSProps({
			additionalProps: {},
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'share'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(shareAction.className).toBeUndefined();
	});
});
