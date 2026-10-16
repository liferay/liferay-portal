/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {navigate} from 'frontend-js-web';

import AllSpacesFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AllSpacesFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	EConfigInURLBehavior: {OFF: 'off'},
	replaceTokens: jest.fn(),
}));

describe('AllSpacesFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('marks the delete item action with the danger class', () => {
		const [deleteAction, pinAction] = AllSpacesFDSPropsTransformer({
			additionalProps: {pinnedAssetLibraryIds: []},
			creationMenu: {primaryItems: []},
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'pin'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(pinAction.className).toBeUndefined();
	});

	it('navigates to the export and import pages of the space', () => {
		const {onActionDropdownItemClick} = AllSpacesFDSPropsTransformer({
			additionalProps: {
				exportURL: '/group{friendlyURL}/~/control_panel/manage?export',
				importURL: '/group{friendlyURL}/~/control_panel/manage?import',
				pinnedAssetLibraryIds: [],
			},
			creationMenu: {primaryItems: []},
			itemsActions: [],
		} as any);

		onActionDropdownItemClick({
			action: {data: {id: 'export'}},
			itemData: {friendlyURL: '/my-space'},
		} as any);

		expect(navigate).toHaveBeenLastCalledWith(
			'/group/my-space/~/control_panel/manage?export'
		);

		onActionDropdownItemClick({
			action: {data: {id: 'import'}},
			itemData: {friendlyURL: '/my-space'},
		} as any);

		expect(navigate).toHaveBeenLastCalledWith(
			'/group/my-space/~/control_panel/manage?import'
		);
	});
});
