/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

import {
	AssetListFDSProps,
	createAssetListFDSPropsBuilder,
} from './utils/createAssetListFDSPropsBuilder';

interface IBrokenLinkAsset {
	brokenLinkTitle?: string;
	brokenLinksCount?: number;
	id?: number;
	objectDefinitionExternalReferenceCode?: string;
	title?: string;
}

function renderBrokenLinks(itemData: IBrokenLinkAsset) {
	const count = itemData.brokenLinksCount ?? 0;

	if (count === 1) {
		return (
			<span className="text-secondary">
				{sub(Liferay.Language.get('x-expired-asset'), [
					itemData.brokenLinkTitle ||
						Liferay.Language.get('untitled-asset'),
				])}
			</span>
		);
	}

	return (
		<span className="text-secondary">
			{sub(Liferay.Language.get('x-expired-assets'), [String(count)])}
		</span>
	);
}

const getAssetListFDSProps = createAssetListFDSPropsBuilder<IBrokenLinkAsset>({
	renderSubtitle: renderBrokenLinks,
	requiresUpdatePermission: false,
	titleRendererName: 'brokenLinkAssetTitle',
});

export default function BrokenLinksFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: AssetListFDSProps) {
	return {
		...getAssetListFDSProps({
			additionalProps,
			itemsActions,
			...otherProps,
		}),
		sorts: [
			{
				active: true,
				direction: 'asc',
				key: 'title',
				label: Liferay.Language.get('title'),
			},
		],
	};
}
