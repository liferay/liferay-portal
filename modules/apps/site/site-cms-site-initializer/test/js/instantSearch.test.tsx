/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {render} from '@testing-library/react';
import React from 'react';

import {DetailedAssetUsageModal} from '../../src/main/resources/META-INF/resources/js/common/components/asset_usage/DetailedAssetUsageModal';
import {Summary} from '../../src/main/resources/META-INF/resources/js/main_view/find_and_replace/components/Summary';
import FolderItemSelectorModalContent from '../../src/main/resources/META-INF/resources/js/main_view/modal/FolderItemSelectorModalContent';
import AllRelatedAssetsFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AllRelatedAssetsFDSPropsTransformer';
import AllSpacesFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AllSpacesFDSPropsTransformer';
import AssetsFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AssetsFDSPropsTransformer';
import AssetsFilesDropFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/AssetsFilesDropFDSPropsTransformer';
import BrokenLinksFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/BrokenLinksFDSPropsTransformer';
import BulkActionTaskReportFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/BulkActionTaskReportFDSPropsTransformer';
import CategoryFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/CategoryFDSPropsTransformer';
import CategoryUsagesFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/CategoryUsagesFDSPropsTransformer';
import ExpiredAssetsFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/ExpiredAssetsFDSPropsTransformer';
import OverdueReviewsFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/OverdueReviewsFDSPropsTransformer';
import PendingWorkflowsFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/PendingWorkflowsFDSPropsTransformer';
import RecycleBinFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/RecycleBinFDSPropsTransformer';
import SharedWithMeFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/SharedWithMeFDSPropsTransformer';
import StructureUsagesFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/StructureUsagesFDSPropsTransformer';
import StructuresFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/StructuresFDSPropsTransformer';
import TagUsagesFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/TagUsagesFDSPropsTransformer';
import ViewVersionHistoryFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/ViewVersionHistoryFDSPropsTransformer';
import VocabularyFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/VocabularyFDSPropsTransformer';
import selectAssetsAction from '../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/selectAssetsAction';
import PicklistOptions from '../../src/main/resources/META-INF/resources/js/structure_builder/components/picklist_builder/PicklistOptions';
import {MockStateProvider} from './structure_builder/mocks/MockPicklistStateProvider';

const mockFrontendDataSet = jest.fn((_props: any) => null);
const mockItemSelectorModal = jest.fn((_props: any) => null);
const mockOpenItemSelectorModal = jest.fn();

jest.mock('@liferay/frontend-data-set-web', () => ({
	...(jest.requireActual('@liferay/frontend-data-set-web') as any),
	FrontendDataSet: (props: any) => mockFrontendDataSet(props),
}));

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	ItemSelectorModal: (props: any) => mockItemSelectorModal(props),
	getCMSItemSelectorGroupedFilters: jest.fn(() => []),
	openItemSelectorModal: (props: any) => mockOpenItemSelectorModal(props),
}));

const TRANSFORMER_PROPS = {
	additionalProps: {},
	bulkActions: [],
	creationMenu: {primaryItems: []},
	id: 'fdsName',
	itemsActions: [],
	views: [],
} as any;

// Every Data Set the CMS renders through a props transformer, keyed by the
// section it serves. A section left out here searches only on Enter and
// remembers nothing.

const TRANSFORMERS: Array<[string, (props: any) => any]> = [
	['All', AssetsFilesDropFDSPropsTransformer],
	['All Related Assets', AllRelatedAssetsFDSPropsTransformer],
	['All Spaces', AllSpacesFDSPropsTransformer],
	['Broken Links', BrokenLinksFDSPropsTransformer],
	['Bulk Action Task Report', BulkActionTaskReportFDSPropsTransformer],
	['Categories', CategoryFDSPropsTransformer],
	['Category Usages', CategoryUsagesFDSPropsTransformer],
	['Contents', AssetsFDSPropsTransformer],
	['Expired Assets', ExpiredAssetsFDSPropsTransformer],
	['Overdue Reviews', OverdueReviewsFDSPropsTransformer],
	['Pending Workflows', PendingWorkflowsFDSPropsTransformer],
	['Recycle Bin', RecycleBinFDSPropsTransformer],
	['Shared With Me', SharedWithMeFDSPropsTransformer],
	['Structure Usages', StructureUsagesFDSPropsTransformer],
	['Structures', StructuresFDSPropsTransformer],
	['Tag Usages', TagUsagesFDSPropsTransformer],
	['Version History', ViewVersionHistoryFDSPropsTransformer],
	['Vocabularies', VocabularyFDSPropsTransformer],
];

// The Data Sets the CMS renders as React instead, paired with the mock that
// receives their props.

const COMPONENTS: Array<[string, () => void, jest.Mock]> = [
	[
		'Asset Usages',
		() =>
			render(
				<DetailedAssetUsageModal
					item={
						{
							attributes: {usages: 1},
							classPK: '1',
							name: 'Asset',
						} as any
					}
				/>
			),
		mockFrontendDataSet,
	],
	['Find and Replace', () => render(<Summary />), mockFrontendDataSet],
	[
		'Folder Item Selector',
		() =>
			render(
				<FolderItemSelectorModalContent
					action="move"
					assetLibraries={[]}
					itemData={{embedded: {id: 1, scopeId: 1}, id: 1} as any}
					loadData={jest.fn() as any}
					objectEntryFolderExternalReferenceCode={undefined}
					rootObjectEntryFolderExternalReferenceCode="CONTENTS"
					selectedData={{} as any}
				/>
			),
		mockItemSelectorModal,
	],
	[
		'Picklist Options',
		() =>
			render(
				<MockStateProvider>
					<PicklistOptions />
				</MockStateProvider>
			),
		mockFrontendDataSet,
	],
];

describe('[CMS] Instant search', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it.each(TRANSFORMERS)(
		'searches the %s section as the user types and offers search suggestions',
		(_section, transform) => {
			const props = transform(TRANSFORMER_PROPS);

			expect(props.searchAsYouType).toBe(true);
			expect(props.searchSuggestionsEnabled).toBe(true);
		}
	);

	it.each(COMPONENTS)(
		'searches the %s Data Set as the user types and offers search suggestions',
		(_dataSet, renderDataSet, mock) => {
			renderDataSet();

			const [props] = mock.mock.calls[0];

			const {searchAsYouType, searchSuggestionsEnabled} =
				props.fdsProps ?? props;

			expect(searchAsYouType).toBe(true);
			expect(searchSuggestionsEnabled).toBe(true);
		}
	);

	// The Select Assets picker is the one Data Set that searches as the user
	// types without offering suggestions, because its id is new on every open

	it('searches the Select Assets Data Set as the user types, remembering nothing', () => {
		selectAssetsAction({searchAPIURL: '/o/search/v1.0/search'} as any);

		const [{fdsProps}] = mockOpenItemSelectorModal.mock.calls[0];

		expect(fdsProps.searchAsYouType).toBe(true);
		expect(fdsProps.searchSuggestionsEnabled).toBeUndefined();
	});

	it.each([
		[
			'Folder Item Selector',
			() =>
				render(
					<FolderItemSelectorModalContent
						action="move"
						assetLibraries={[]}
						itemData={{embedded: {id: 1, scopeId: 1}, id: 1} as any}
						loadData={jest.fn() as any}
						objectEntryFolderExternalReferenceCode={undefined}
						rootObjectEntryFolderExternalReferenceCode="CONTENTS"
						selectedData={{} as any}
					/>
				),
			mockItemSelectorModal,
		],
		[
			'Select Assets',
			() =>
				selectAssetsAction({
					searchAPIURL: '/o/search/v1.0/search',
				} as any),
			mockOpenItemSelectorModal,
		],
	] as Array<[string, () => void, jest.Mock]>)(
		'keeps the %s picker out of the URL of the page behind it',
		(_dataSet, openPicker, mock) => {
			openPicker();

			const [{fdsProps}] = mock.mock.calls[0];

			expect(fdsProps.configInURLBehavior).toBe(EConfigInURLBehavior.OFF);
		}
	);
});
