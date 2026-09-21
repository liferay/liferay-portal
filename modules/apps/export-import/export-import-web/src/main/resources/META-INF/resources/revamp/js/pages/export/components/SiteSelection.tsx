/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {FormikFieldSites} from '../../../components/forms/formik';
import {getExportPreviewSitesCount} from '../../../services/getExportPreviewSitesCount';

/**
 * The Sites row of the export form. The sites of the instance are read by the
 * dialog the row opens; the row itself only needs to know how many there are.
 */
export default function SiteSelection({
	exportPreviewSitesAPIURL,
}: {
	exportPreviewSitesAPIURL: string;
}) {
	const [totalCount, setTotalCount] = useState<number | undefined>();

	useEffect(() => {
		getExportPreviewSitesCount(exportPreviewSitesAPIURL).then(
			(response) => {
				if (response.error === null) {
					setTotalCount(response.data.totalCount);
				}
			}
		);
	}, [exportPreviewSitesAPIURL]);

	return (
		<FormikFieldSites
			apiURL={exportPreviewSitesAPIURL}
			name="siteExternalReferenceCodes"
			totalCount={totalCount}
		/>
	);
}
