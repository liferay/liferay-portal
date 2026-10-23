/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	createAssetListFDSPropsBuilder,
	createTitleSortedFDSPropsTransformer,
} from './utils/createAssetListFDSPropsBuilder';

export interface IUnlinkedAsset {
	id?: number;
	objectDefinitionExternalReferenceCode?: string;
	title?: string;
}

export function renderUnlinkedContent() {
	return (
		<span className="text-secondary">
			{Liferay.Language.get(
				'this-asset-is-not-used-in-any-content-or-page'
			)}
		</span>
	);
}

export const getUnlinkedContentFDSProps =
	createAssetListFDSPropsBuilder<IUnlinkedAsset>({
		renderSubtitle: renderUnlinkedContent,
		titleRendererName: 'unlinkedAssetTitle',
	});

export default createTitleSortedFDSPropsTransformer(getUnlinkedContentFDSProps);
