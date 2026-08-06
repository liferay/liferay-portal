/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {OBJECT_ENTRY_CLASS_NAME} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {openCMSModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import openResetAssetPermissionModal from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/ResetPermissionModalContent';
import AssetNavigationModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent';
import HomeRecentAssetsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/HomeRecentAssetsFDSPropsTransformer';
import ACTIONS from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/creationMenuActions';
import shareAction from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/shareAction';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/components/asset_usage/utils',
	() => ({openAssetUsageListModal: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/ResetPermissionModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/modal/ExportTranslationModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/modal/ScheduleDateModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/creationMenuActions',
	() => ({__esModule: true, default: {importTranslation: jest.fn()}})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/deleteItemAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/openFolderItemSelectorAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/shareAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/AssetRenderer',
	() => ({__esModule: true, default: jest.fn()})
);

describe('HomeRecentAssetsFDSPropsTransformer', () => {
	const mockAdditionalProps = {
		autocompleteURL: '',
		collaboratorURLs: {},
		contentViewURL: '',
		fileMimeTypeCssClasses: {},
		fileMimeTypeIcons: {},
		objectDefinitionCssClasses: {},
		objectDefinitionIcons: {},
	} as any;

	const mockEvent = {preventDefault: jest.fn()} as any;

	const getTransformedProps = () =>
		HomeRecentAssetsFDSPropsTransformer({
			additionalProps: mockAdditionalProps,
			itemsActions: [],
		} as any);

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('opens the asset navigation modal on the clicked asset', async () => {
		const items: any[] = [
			{embedded: {id: 1}, entryClassName: OBJECT_ENTRY_CLASS_NAME},
			{embedded: {id: 2}, entryClassName: OBJECT_ENTRY_CLASS_NAME},
		];

		await getTransformedProps().onActionDropdownItemClick({
			action: {data: {id: 'view-content'}},
			event: mockEvent,
			itemData: items[1],
			items,
			loadData: jest.fn(),
		});

		expect(openCMSModal).toHaveBeenCalled();

		(openCMSModal as jest.Mock).mock.calls[0][0].contentComponent();

		expect(AssetNavigationModalContent).toHaveBeenCalledWith(
			expect.objectContaining({currentIndex: 1})
		);
	});

	it('does not open the asset navigation modal for an asset without embedded data', async () => {
		const items: any[] = [
			{entryClassName: OBJECT_ENTRY_CLASS_NAME},
			{embedded: {id: 2}, entryClassName: OBJECT_ENTRY_CLASS_NAME},
		];

		await getTransformedProps().onActionDropdownItemClick({
			action: {data: {id: 'view-content'}},
			event: mockEvent,
			itemData: items[0],
			items,
			loadData: jest.fn(),
		});

		expect(openCMSModal).not.toHaveBeenCalled();
	});

	it('does not reset the permissions of an asset without embedded data', async () => {
		await getTransformedProps().onActionDropdownItemClick({
			action: {data: {id: 'reset-to-default-permissions'}},
			event: mockEvent,
			itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME},
			items: [],
			loadData: jest.fn(),
		});

		expect(openResetAssetPermissionModal).not.toHaveBeenCalled();
	});

	it('does not import a translation for an asset without embedded data', async () => {
		await getTransformedProps().onActionDropdownItemClick({
			action: {data: {id: 'import-translation'}},
			event: mockEvent,
			itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME},
			items: [],
			loadData: jest.fn(),
		});

		expect(ACTIONS.importTranslation).not.toHaveBeenCalled();
	});

	it('does not share an asset without embedded data', async () => {
		await getTransformedProps().onActionDropdownItemClick({
			action: {data: {id: 'share'}},
			event: mockEvent,
			itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME},
			items: [],
			loadData: jest.fn(),
		});

		expect(shareAction).not.toHaveBeenCalled();
	});
});
