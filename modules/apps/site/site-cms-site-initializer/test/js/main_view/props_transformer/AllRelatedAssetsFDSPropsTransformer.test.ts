/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AllRelatedAssetsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AllRelatedAssetsFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	getCMSItemSelectorGroupedFilters: jest.fn(() => []),
}));

describe('AllRelatedAssetsFDSPropsTransformer', () => {
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

	const transform = () =>
		AllRelatedAssetsFDSPropsTransformer({
			additionalProps: mockAdditionalProps,
			creationMenu: {primaryItems: []},
			id: 'com.liferay.site.cms.site.initializer-allRelatedAssetsSection',
			views: [],
		}) as any;

	it('does not set the info panel container when the assets section is outside a tab panel', () => {
		document.body.innerHTML =
			'<div class="cms-all-related-assets cms-section"></div>';

		expect(transform().infoPanelContainerRef).toBeUndefined();
	});

	it('keeps the props from the assets transformer', () => {
		document.body.innerHTML =
			'<div class="cms-all-related-assets cms-section"></div>';

		expect(transform().infoPanelComponent).toBeDefined();
	});

	it('scopes the info panel to the tab panel that contains the assets section', () => {
		document.body.innerHTML = `
			<div class="component-tabs">
				<div class="tab-panel-item" id="detailsTabPanel"></div>
				<div class="tab-panel-item" id="tasksTabPanel"></div>
				<div class="tab-panel-item" id="assetsTabPanel">
					<div class="cms-all-related-assets cms-section"></div>
				</div>
			</div>
		`;

		expect(transform().infoPanelContainerRef.current).toBe(
			document.getElementById('assetsTabPanel')
		);
	});
});
