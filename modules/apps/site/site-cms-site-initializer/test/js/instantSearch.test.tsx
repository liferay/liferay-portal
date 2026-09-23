/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {DetailedAssetUsageModal} from '../../src/main/resources/META-INF/resources/js/common/components/asset_usage/DetailedAssetUsageModal';
import MergeTagsModal from '../../src/main/resources/META-INF/resources/js/main_view/categorization/tags/MergeTagsModal';
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

jest.mock(
	'../../src/main/resources/META-INF/resources/js/common/services/ApiHelper',
	() => ({
		__esModule: true,
		default: {get: jest.fn(async () => ({data: {items: []}, error: null}))},
	})
);

const mockOpenCMSModal = jest.fn();

jest.mock(
	'../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: (props: any) => mockOpenCMSModal(props)})
);

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

// The item a picker is opened for is a search result, which carries no id of
// its own; the fixture leaves it out so the id the picker derives is the one
// production derives.

const renderFolderItemSelector = () =>
	render(
		<FolderItemSelectorModalContent
			action="move"
			assetLibraries={[]}
			itemData={{embedded: {id: 1, scopeId: 1}} as any}
			loadData={jest.fn() as any}
			objectEntryFolderExternalReferenceCode={undefined}
			rootObjectEntryFolderExternalReferenceCode="CONTENTS"
			selectedData={{} as any}
		/>
	);

// The Merge Tags picker is a Data Set inside a second modal, which only opens
// once the user asks to select tags.

const renderMergeTagsPicker = async () => {
	render(
		<MergeTagsModal
			closeModal={jest.fn()}
			cmsGroupId={1}
			loadData={jest.fn() as any}
			selectIntoTags={[{label: 'Tag', value: 1}]}
		/>
	);

	await userEvent.click(screen.getByRole('button', {name: /select/i}));

	const [{contentComponent: ContentComponent}] =
		mockOpenCMSModal.mock.calls[0];

	render(<ContentComponent closeModal={jest.fn()} />);
};

const COMPONENTS: Array<[string, () => unknown, jest.Mock]> = [
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
	['Merge Tags', renderMergeTagsPicker, mockFrontendDataSet],
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
		async (_dataSet, renderDataSet, mock) => {
			await renderDataSet();

			const [props] = mock.mock.calls[0];

			const {searchAsYouType, searchSuggestionsEnabled} =
				props.fdsProps ?? props;

			expect(searchAsYouType).toBe(true);
			expect(searchSuggestionsEnabled).toBe(true);
		}
	);

	// Picklist Options holds its items rather than fetching them, so the query
	// only reaches them through a predicate of its own

	it('searches the Picklist Options Data Set by every column it shows', () => {
		render(
			<MockStateProvider>
				<PicklistOptions />
			</MockStateProvider>
		);

		const [{onItemsPropSearch}] = mockFrontendDataSet.mock.calls[0];

		const item = {
			erc: 'optionERC',
			key: 'optionKey',
			name: {en_US: 'Name'},
		};

		expect(onItemsPropSearch(item, 'name')).toBe(true);
		expect(onItemsPropSearch(item, 'optionKey')).toBe(true);
		expect(onItemsPropSearch(item, 'optionERC')).toBe(true);
		expect(onItemsPropSearch(item, 'nothing')).toBe(false);
	});

	// A picker whose Data Set id changes between opens can never read its own
	// search history back, so it searches as the user types and remembers
	// nothing

	it.each([
		[
			'Folder Item Selector',
			renderFolderItemSelector,
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
	] as Array<[string, () => unknown, jest.Mock]>)(
		'searches the %s Data Set as the user types, remembering nothing',
		(_dataSet, openPicker, mock) => {
			openPicker();

			const [{fdsProps}] = mock.mock.calls[0];

			expect(fdsProps.searchAsYouType).toBe(true);
			expect(fdsProps.searchSuggestionsEnabled).toBeUndefined();
		}
	);

	// A Data Set that opens over a page keeps its search, sort and pagination
	// out of the URL of the page behind it

	it.each([
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
		[
			'Folder Item Selector',
			renderFolderItemSelector,
			mockItemSelectorModal,
		],
		['Merge Tags', renderMergeTagsPicker, mockFrontendDataSet],
		[
			'Select Assets',
			() =>
				selectAssetsAction({
					searchAPIURL: '/o/search/v1.0/search',
				} as any),
			mockOpenItemSelectorModal,
		],
	] as Array<[string, () => unknown, jest.Mock]>)(
		'keeps the %s Data Set out of the URL of the page behind it',
		async (_dataSet, openDataSet, mock) => {
			await openDataSet();

			const [props] = mock.mock.calls[0];

			const {configInURLBehavior} = props.fdsProps ?? props;

			expect(configInURLBehavior).toBe(EConfigInURLBehavior.OFF);
		}
	);
});
