/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AssetsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AssetsFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	getCMSItemSelectorGroupedFilters: jest.fn(() => []),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/components/StatusLabel',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/components/asset_usage/utils',
	() => ({openAssetUsageListModal: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/getFormattedText',
	() => ({getFormattedLabel: jest.fn((label: string) => label)})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/getScopeExternalReferenceCode',
	() => ({getScopeExternalReferenceCode: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/categorization/modal/EditAssetCategoriesModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/categorization/modal/EditAssetTagsModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/BulkDefaultPermissionModalContent',
	() => ({defaultPermissionsBulkAction: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/BulkPermissionModalContent',
	() => ({permissionsBulkAction: jest.fn()})
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
	'../../../../src/main/resources/META-INF/resources/js/main_view/find_and_replace/utils/handleFindAndReplace',
	() => ({handleFindAndReplace: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/AssetTypeInfoPanelContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/modal/ExportTranslationModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/copyOrMoveBulkAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/creationMenuActions',
	() => ({__esModule: true, default: {}})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/deleteAssetEntriesBulkAction',
	() => ({
		__esModule: true,
		default: jest.fn(),
		executeBulkDeleteAction: jest.fn(),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/deleteItemAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/executeResetPermissionObjectBulkSelectionAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/expireEntriesBulkAction',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/exportTranslationBulkAction',
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
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetDownloadBulkAction',
	() => ({triggerAssetDownloadBulkAction: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/AdditionalItemInfoRenderer',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/AuthorRenderer',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SimpleActionLinkRenderer',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SpaceRendererWithCache',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/TypeRenderer',
	() => ({__esModule: true, default: jest.fn()})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/addOnClickToCreationMenuItems',
	() => ({__esModule: true, default: jest.fn((items: any[]) => items ?? [])})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/transformFDSBulkActions',
	() => ({__esModule: true, default: jest.fn((actions: any[]) => actions)})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/transformViewsItemProps',
	() => ({__esModule: true, default: jest.fn(() => [])})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/views/GalleryView',
	() => ({__esModule: true, default: jest.fn()})
);

describe('AssetsFDSPropsTransformer', () => {
	const mockAdditionalProps = {
		assetLibraries: [],
		autocompleteURL: '',
		availableExportFileFormats: [],
		availableLocales: [],
		baseFolderViewURL: '',
		brokenLinksCheckerEnabled: false,
		candidateAssetLibraries: [],
		collaboratorURLs: {},
		contentViewURL: '',
		fileMimeTypeCssClasses: {},
		fileMimeTypeIcons: {},
		objectDefinitionCssClasses: {},
		objectDefinitionIcons: {},
		objectEntryFolderExternalReferenceCode: '',
		parentObjectEntryFolderExternalReferenceCode: '',
		redirect: '',
		rootObjectEntryFolderExternalReferenceCode: '',
	} as any;

	it('forces hideManagementBarInEmptyState to true outside the All Section', () => {
		const result = AssetsFDSPropsTransformer({
			additionalProps: mockAdditionalProps,
			creationMenu: {primaryItems: []},
			hideManagementBarInEmptyState: false,
			id: 'com.liferay.site.cms.site.initializer-contentsSection',
			views: [],
		});

		expect(result.hideManagementBarInEmptyState).toBe(true);
	});

	it('honors hideManagementBarInEmptyState in the All Section', () => {
		const result = AssetsFDSPropsTransformer({
			additionalProps: mockAdditionalProps,
			creationMenu: {primaryItems: []},
			hideManagementBarInEmptyState: false,
			id: 'com.liferay.site.cms.site.initializer-allSection',
			views: [],
		});

		expect(result.hideManagementBarInEmptyState).toBe(false);
	});

	it('marks the generate with AI creation menu items with the purple class', () => {
		const result = AssetsFDSPropsTransformer({
			additionalProps: mockAdditionalProps,
			creationMenu: {
				primaryItems: [
					{data: {action: 'generateContentWithAI'}, label: 'content'},
					{data: {action: 'generateImageWithAI'}, label: 'image'},
					{data: {action: 'addFolder'}, label: 'folder'},
				],
			},
			hideManagementBarInEmptyState: false,
			id: 'com.liferay.site.cms.site.initializer-allSection',
			views: [],
		});

		const [contentItem, imageItem, folderItem] =
			result.creationMenu.primaryItems;

		expect(contentItem.className).toBe('cms-generate-content-with-ai');
		expect(imageItem.className).toBe('cms-generate-content-with-ai');
		expect(folderItem.className).toBeUndefined();
	});
});
