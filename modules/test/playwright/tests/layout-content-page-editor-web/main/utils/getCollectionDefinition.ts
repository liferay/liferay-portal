/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getRandomString from '../../../../utils/getRandomString';

type ListStyle =
	| 'Bordered List (Collection Provider)'
	| 'Bordered List (Journal)'
	| 'Bulleted List (Collection Provider)'
	| 'Bulleted List (Journal)'
	| 'Inline List'
	| 'Numbered List'
	| 'Unstyled List';

type Props = {
	classPK?: number;
	collectionConfig?: CollectionConfig;
	id: string;
	listStyle?: ListStyle;
	pageElements?: PageElement[];
	provider?:
		| 'Highest Rated Assets'
		| 'Items with Categories in the Same Vocabularies'
		| 'Recent Content';
};

const DEFAULT_CONFIG = {
	numberOfColumns: 1,
	numberOfItems: 50,
};

const COLLECTION_PROVIDERS = {
	'Highest Rated Assets':
		'com.liferay.asset.internal.info.collection.provider.HighestRatedAssetsInfoCollectionProvider',
	'Items with Categories in the Same Vocabularies':
		'com.liferay.asset.internal.info.collection.provider.AssetEntriesWithAssetCategoriesInTheSameAssetVocabulariesRelatedInfoItemCollectionProvider',
	'Recent Content':
		'com.liferay.asset.internal.info.collection.provider.RecentContentInfoCollectionProvider',
};

const LIST_STYLES = {
	'Bordered List (Collection Provider)':
		'com.liferay.asset.info.internal.list.renderer.AssetEntryBorderedBasicInfoListRenderer',
	'Bordered List (Journal)':
		'com.liferay.journal.web.internal.info.list.renderer.JournalArticleBorderedBasicInfoListRenderer',
	'Bulleted List (Collection Provider)':
		'com.liferay.asset.info.internal.list.renderer.BulletedAssetEntryBasicInfoListRenderer',
	'Bulleted List (Journal)':
		'com.liferay.journal.web.internal.info.list.renderer.BulletedJournalArticleBasicInfoListRenderer',
	'Inline List':
		'com.liferay.journal.web.internal.info.list.renderer.InlineJournalArticleBasicInfoListRenderer',
	'Numbered List':
		'com.liferay.journal.web.internal.info.list.renderer.NumberedJournalArticleBasicInfoListRenderer',
	'Unstyled List':
		'com.liferay.journal.web.internal.info.list.renderer.UnstyledJournalArticleBasicInfoListRenderer',
};

export default function getCollectionDefinition({
	classPK,
	id,
	listStyle,
	pageElements,
	provider,
}: Props): PageElement {
	return {
		definition: {
			...DEFAULT_CONFIG,
			collectionConfig: {
				collectionReference: {
					className: classPK
						? 'com.liferay.asset.list.model.AssetListEntry'
						: COLLECTION_PROVIDERS[provider],
					classPK,
				},
				collectionType: classPK ? 'Collection' : 'CollectionProvider',
			},
			collectionViewports: [
				{
					collectionViewportDefinition: {
						numberOfColumns: 1,
					},
					id: 'landscapeMobile',
				},
				{
					collectionViewportDefinition: {
						numberOfColumns: 1,
					},
					id: 'portraitMobile',
				},
				{
					collectionViewportDefinition: {
						numberOfColumns: 1,
					},
					id: 'tablet',
				},
			],

			listStyle: LIST_STYLES[listStyle] || '',
		},
		id,
		pageElements: [getCollectionItemDefinition(pageElements)],
		type: 'Collection',
	};
}

function getCollectionItemDefinition(
	pageElements: PageElement[] = []
): PageElement {
	return {
		id: getRandomString(),
		pageElements,
		type: 'CollectionItem',
	};
}
