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

export interface IMissingRequiredFieldsAsset {
	id?: number;
	missingRequiredFieldsCount?: number;
	objectDefinitionExternalReferenceCode?: string;
	title?: string;
}

export function renderMissingRequiredFields({
	missingRequiredFieldsCount = 0,
}: IMissingRequiredFieldsAsset) {
	return (
		<span className="text-secondary">
			{sub(
				missingRequiredFieldsCount === 1
					? Liferay.Language.get('x-missing-required-field')
					: Liferay.Language.get('x-missing-required-fields'),
				missingRequiredFieldsCount
			)}
		</span>
	);
}

export const getMissingRequiredFieldsFDSProps =
	createAssetListFDSPropsBuilder<IMissingRequiredFieldsAsset>({
		renderSubtitle: renderMissingRequiredFields,
		titleRendererName: 'missingRequiredFieldsAssetTitle',
	});

export default createTitleSortedFDSPropsTransformer(
	getMissingRequiredFieldsFDSProps
);
