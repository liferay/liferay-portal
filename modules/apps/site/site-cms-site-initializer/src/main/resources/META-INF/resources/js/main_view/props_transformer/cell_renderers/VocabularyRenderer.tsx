/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface AssetType {
	required: boolean;
	subtype: string;
	type: string;
}

const VocabularyRenderer = ({value}: {value: AssetType[]}) => {
	const getTypes = (assetTypes: AssetType[]) => {
		let types: string = '';

		for (const assetType of assetTypes) {
			if (types === '') {
				types = assetType.type;
			}
			else {
				types = types + ', ' + assetType.type;
			}
		}

		return types;
	};

	return <>{getTypes(value)}</>;
};

export default VocabularyRenderer;
