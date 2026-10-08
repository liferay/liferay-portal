/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {FormikFieldSites} from '../../../components/forms/formik';
import {PreviewAssetLibrary} from '../../../types/exportImportPreview';

export default function AssetLibrarySelection({
	previewAssetLibraries,
}: {
	previewAssetLibraries: PreviewAssetLibrary[];
}) {
	return (
		<FormikFieldSites
			name="assetLibraryExternalReferenceCodes"
			process="import"
			sites={previewAssetLibraries}
			totalCount={previewAssetLibraries.length}
			type="assetLibrary"
		/>
	);
}
