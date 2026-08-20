/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {OBJECT_ENTRY_CLASS_NAME} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {openCMSModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import openResetAssetPermissionModal from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/ResetPermissionModalContent';
import AssetNavigationModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent';
import AssetsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AssetsFDSPropsTransformer';
import ACTIONS from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/creationMenuActions';
import shareAction from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/shareAction';

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
	() => ({__esModule: true, default: {importTranslation: jest.fn()}})
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

		expect(contentItem.className).toBe('cms-generate-with-ai');
		expect(imageItem.className).toBe('cms-generate-with-ai');
		expect(folderItem.className).toBeUndefined();
	});

	describe('additionalAPIURLParametersTransformer', () => {
		const FOLDER_PARAMETERS =
			'emptySearch=true&filter=folderId eq 12345 and rootDescendantNode eq false and status in (0, 2, 3)&sort=dateModified:desc';

		const SECTION_PARAMETERS =
			"emptySearch=true&filter=cmsRoot eq true and cmsSection eq 'files' and rootDescendantNode eq false&sort=dateModified:desc";

		const transform = (
			additionalAPIURLParameters: string,
			searchParam: string
		) => {
			const {additionalAPIURLParametersTransformer} =
				AssetsFDSPropsTransformer({
					additionalProps: {
						...mockAdditionalProps,
						additionalAPIURLParameters,
					},
					creationMenu: {primaryItems: []},
					views: [],
				});

			return additionalAPIURLParametersTransformer({
				additionalAPIURLParameters,
				searchParam,
			});
		};

		it('keeps the folder scope when there is no search term', () => {
			expect(transform(FOLDER_PARAMETERS, '')).toBe(FOLDER_PARAMETERS);
			expect(transform(FOLDER_PARAMETERS, '   ')).toBe(FOLDER_PARAMETERS);
		});

		it('widens the folder scope to the folder subtree when searching', () => {
			expect(transform(FOLDER_PARAMETERS, 'bojler')).toBe(
				"emptySearch=true&filter=treePath/any(t:t eq '12345') and rootDescendantNode eq false and status in (0, 2, 3)&sort=dateModified:desc"
			);
		});

		it('drops the CMS root clause when searching in a section', () => {
			expect(transform(SECTION_PARAMETERS, 'bojler')).toBe(
				"emptySearch=true&filter=cmsSection eq 'files' and rootDescendantNode eq false&sort=dateModified:desc"
			);
		});

		it('drops the filter when no clause survives', () => {
			expect(
				transform('emptySearch=true&filter=cmsRoot eq true', 'bojler')
			).toBe('emptySearch=true');
		});

		it('keeps parameters without a filter untouched', () => {
			expect(transform('emptySearch=true', 'bojler')).toBe(
				'emptySearch=true'
			);
		});
	});

	describe('onActionDropdownItemClick', () => {
		const mockEvent = {preventDefault: jest.fn()} as any;

		const getTransformedProps = () =>
			AssetsFDSPropsTransformer({
				additionalProps: mockAdditionalProps,
				creationMenu: {primaryItems: []},
				hideManagementBarInEmptyState: false,
				id: 'com.liferay.site.cms.site.initializer-allSection',
				views: [],
			});

		beforeEach(() => {
			jest.clearAllMocks();
		});

		afterEach(() => {
			jest.restoreAllMocks();
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

		it('does not fire addToLaunch for an asset without embedded data', async () => {
			const fireSpy = jest.spyOn(Liferay, 'fire');

			await getTransformedProps().onActionDropdownItemClick({
				action: {data: {id: 'addToLaunch'}},
				event: mockEvent,
				itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME} as any,
				items: [],
				loadData: jest.fn(),
			});

			expect(fireSpy).not.toHaveBeenCalled();
		});

		it('does not fire addToLaunch for an asset without a version', async () => {
			const fireSpy = jest.spyOn(Liferay, 'fire');

			await getTransformedProps().onActionDropdownItemClick({
				action: {data: {id: 'addToLaunch'}},
				event: mockEvent,
				itemData: {
					embedded: {id: 1},
					entryClassName: OBJECT_ENTRY_CLASS_NAME,
				} as any,
				items: [],
				loadData: jest.fn(),
			});

			expect(fireSpy).not.toHaveBeenCalled();
		});

		it('does not reset the permissions of an asset without embedded data', async () => {
			await getTransformedProps().onActionDropdownItemClick({
				action: {data: {id: 'reset-to-default-permissions'}},
				event: mockEvent,
				itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME} as any,
				items: [],
				loadData: jest.fn(),
			});

			expect(openResetAssetPermissionModal).not.toHaveBeenCalled();
		});

		it('does not import a translation for an asset without embedded data', async () => {
			await getTransformedProps().onActionDropdownItemClick({
				action: {data: {id: 'import-translation'}},
				event: mockEvent,
				itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME} as any,
				items: [],
				loadData: jest.fn(),
			});

			expect(ACTIONS.importTranslation).not.toHaveBeenCalled();
		});

		it('does not share an asset without embedded data', async () => {
			await getTransformedProps().onActionDropdownItemClick({
				action: {data: {id: 'share'}},
				event: mockEvent,
				itemData: {entryClassName: OBJECT_ENTRY_CLASS_NAME} as any,
				items: [],
				loadData: jest.fn(),
			});

			expect(shareAction).not.toHaveBeenCalled();
		});
	});
});
