/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {FormikFieldSites} from '../../../components/forms/formik';
import {getExportPreviewSitesCount} from '../../../services/getExportPreviewSitesCount';

export default function AssetLibrarySelection({
	exportPreviewAssetLibrariesAPIURL,
}: {
	exportPreviewAssetLibrariesAPIURL: string;
}) {
	const [totalCount, setTotalCount] = useState<number | undefined>();

	useEffect(() => {
		getExportPreviewSitesCount(exportPreviewAssetLibrariesAPIURL).then(
			(response) => {
				if (response.data) {
					setTotalCount(response.data.totalCount);
				}
			}
		);
	}, [exportPreviewAssetLibrariesAPIURL]);

	return (
		<FormikFieldSites
			apiURL={exportPreviewAssetLibrariesAPIURL}
			name="assetLibraryExternalReferenceCodes"
			totalCount={totalCount}
			type="assetLibrary"
		/>
	);
}
