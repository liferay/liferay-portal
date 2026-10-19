/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

import {
	createAssetListFDSPropsBuilder,
	createTitleSortedFDSPropsTransformer,
} from './utils/createAssetListFDSPropsBuilder';

export interface IBrokenLinkAsset {
	deletedBrokenLinksCount?: number;
	draftBrokenLinksCount?: number;
	expiredBrokenLinksCount?: number;
	id?: number;
	inTrashBrokenLinksCount?: number;
	objectDefinitionExternalReferenceCode?: string;
	title?: string;
}

type BrokenLinksCountKey = Extract<
	keyof IBrokenLinkAsset,
	`${string}BrokenLinksCount`
>;

function getBrokenLinkTypes(): {
	countKey: BrokenLinksCountKey;
	plural: string;
	singular: string;
}[] {
	return [
		{
			countKey: 'expiredBrokenLinksCount',
			plural: Liferay.Language.get('x-expired-assets'),
			singular: Liferay.Language.get('x-expired-asset'),
		},
		{
			countKey: 'deletedBrokenLinksCount',
			plural: Liferay.Language.get('x-deleted-assets'),
			singular: Liferay.Language.get('x-deleted-asset'),
		},
		{
			countKey: 'inTrashBrokenLinksCount',
			plural: Liferay.Language.get('x-assets-in-recycle-bin'),
			singular: Liferay.Language.get('x-asset-in-recycle-bin'),
		},
		{
			countKey: 'draftBrokenLinksCount',
			plural: Liferay.Language.get('x-draft-assets'),
			singular: Liferay.Language.get('x-draft-asset'),
		},
	];
}

export function renderBrokenLinks(itemData: IBrokenLinkAsset) {
	const labels = getBrokenLinkTypes().flatMap(
		({countKey, plural, singular}) => {
			const count = itemData[countKey] ?? 0;

			if (!count) {
				return [];
			}

			return sub(count === 1 ? singular : plural, count);
		}
	);

	return <span className="text-secondary">{labels.join(' · ')}</span>;
}

export const getBrokenLinksFDSProps =
	createAssetListFDSPropsBuilder<IBrokenLinkAsset>({
		renderSubtitle: renderBrokenLinks,
		titleRendererName: 'brokenLinkAssetTitle',
	});

export default createTitleSortedFDSPropsTransformer(getBrokenLinksFDSProps);
